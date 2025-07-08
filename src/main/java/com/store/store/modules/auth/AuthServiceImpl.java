package com.store.store.modules.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.store.store.common.ErrorHelper;
import com.store.store.common.email.EmailService;
import com.store.store.common.email.OTPUtil;
import com.store.store.common.jwt.JwtService;
import com.store.store.common.redis.RedisService;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Store;
import com.store.store.model.User;
import com.store.store.modules.auth.dto.LoginRequest;
import com.store.store.modules.auth.dto.RefreshTokenRequest;
import com.store.store.modules.auth.dto.RegisterRequest;
import com.store.store.modules.auth.dto.SendOtpRequest;
import com.store.store.modules.auth.dto.StoreAuthResponse;
import com.store.store.modules.auth.dto.StoreRegisterRequest;
import com.store.store.modules.auth.dto.UserAuthResponse;
import com.store.store.modules.auth.dto.VerifyOtpRequest;
import com.store.store.modules.store.StoreRepository;
import com.store.store.modules.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final StoreRepository storeRepository;
    private final EmailService emailService;
    private final OTPUtil otpUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService, RedisService redisService, StoreRepository storeRepository,
            EmailService emailService, OTPUtil otpUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.storeRepository = storeRepository;
        this.emailService = emailService;
        this.otpUtil = otpUtil;
    }

    @Value("${otp.secret-key}")
    private String otpSecretKey;

    @Override
    public ResponseEntity<ApiResponse<Object>> register(RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ErrorHelper.badRequest("Email already exists");
            }
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                return ErrorHelper.badRequest("Phone number already exists");
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success(user, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("User registration failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> login(LoginRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (optionalUser.isEmpty()) {
                return ErrorHelper.badRequest("Invalid credentials");
            }
            User user = optionalUser.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ErrorHelper.badRequest("Invalid credentials");
            }

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            String key = "refresh_token:user:" + user.getId();
            redisService.set(key, refreshToken, 5L * 30 * 24 * 60 * 60); // 5 months in seconds

            UserAuthResponse response = UserAuthResponse.builder()
                    .user(user)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Login failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ErrorHelper.badRequest("No token provided");
            }

            String token = authHeader.substring(7);
            String subject = jwtService.extractSubject(token); // "user:123" or "store:456"
            String[] parts = subject.split(":");
            if (parts.length != 2) {
                return ErrorHelper.badRequest("Invalid token");
            }

            String type = parts[0];
            Long id = Long.parseLong(parts[1]);

            String key = "refresh_token:" + type + ":" + id;
            redisService.delete(key);

            return ResponseEntity.ok(ApiResponse.success("Logout successful", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Logout failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            if (!jwtService.isTokenValid(refreshToken)) {
                return ErrorHelper.badRequest("Invalid refresh token");
            }

            String subject = jwtService.extractSubject(refreshToken);
            String[] parts = subject.split(":");
            if (parts.length != 2) {
                return ErrorHelper.badRequest("Invalid token subject");
            }

            String type = parts[0];
            Long id = Long.parseLong(parts[1]);
            String key = "refresh_token:" + type + ":" + id;
            String storedToken = redisService.get(key);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                return ErrorHelper.badRequest("Refresh token not found or mismatched");
            }

            if ("user".equals(type)) {
                Optional<User> user = userRepository.findById(id);
                if (user.isEmpty()) {
                    return ErrorHelper.notFound("User not found");
                }

                String newAccessToken = jwtService.generateAccessToken(user.get());

                UserAuthResponse response = UserAuthResponse.builder()
                        .user(user.get())
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();

                return ResponseEntity.ok(ApiResponse.success(response, 200));
            } else if ("store".equals(type)) {
                Optional<Store> store = storeRepository.findById(id);
                if (store.isEmpty()) {
                    return ErrorHelper.notFound("Store not found");
                }

                String newAccessToken = jwtService.generateAccessToken(store.get());

                StoreAuthResponse response = StoreAuthResponse.builder()
                        .store(store.get())
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();

                return ResponseEntity.ok(ApiResponse.success(response, 200));
            } else {
                return ErrorHelper.badRequest("Unknown token type");
            }

        } catch (Exception e) {
            return ErrorHelper.badRequest("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> storeRegister(StoreRegisterRequest request) {
        try {
            if (storeRepository.findByEmail(request.getEmail()).isPresent()) {
                return ErrorHelper.badRequest("Email already exists");
            }

            Store store = Store.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .isApproved(false)
                    .build();
            storeRepository.save(store);
            return ResponseEntity.ok(ApiResponse.success(store, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Store registration failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> storeLogin(LoginRequest request) {
        try {
            Optional<Store> optionalStore = storeRepository.findByEmail(request.getEmail());
            if (optionalStore.isEmpty()) {
                return ErrorHelper.badRequest("Invalid credentials");
            }
            Store store = optionalStore.get();

            if (!passwordEncoder.matches(request.getPassword(), store.getPassword())) {
                return ErrorHelper.badRequest("Invalid credentials");
            }

            String accessToken = jwtService.generateAccessToken(store);
            String refreshToken = jwtService.generateRefreshToken(store);

            String key = "refresh_token:store:" + store.getId();
            redisService.set(key, refreshToken, 5L * 30 * 24 * 60 * 60); // 5 months in seconds

            StoreAuthResponse response = StoreAuthResponse.builder()
                    .store(store)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Store login failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> sendOTP(SendOtpRequest request) {
        try {
            String email = request.getEmail();
            String hash = request.getHash(); // example: e46ef2b91c879a407ba7c194048f9485 - viethoangb0410+1@gmail.com

            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ErrorHelper.notFound("User not found");
            }

            String checkHash = DigestUtils.md5DigestAsHex(
                    (email + otpSecretKey + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                            .getBytes(StandardCharsets.UTF_8));

            if (!checkHash.equals(hash)) {
                return ErrorHelper.badRequest("HASH_IS_NOT_CORRECT");
            }

            String otp = otpUtil.generateOTP();
            emailService.sendOTP(email, "Confirm OTP", otp);

            long expiryTime = Instant.now().plusSeconds(300).toEpochMilli(); // 5 min

            String encrypted = otpUtil.hashData(
                    new JSONObject()
                            .put("otp", otp)
                            .put("time", expiryTime)
                            .put("email", email)
                            .put("isVerified", false)
                            .toString());

            User user = userOpt.get();
            user.setOtp(otp);
            user.setOtpExpireTime(300); // second
            userRepository.save(user);

            return ResponseEntity.ok(ApiResponse.success(encrypted, 200));

        } catch (Exception e) {
            return ErrorHelper.badRequest("Error sending OTP: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> verifyOTP(VerifyOtpRequest request) {
        try {
            String decrypted = otpUtil.decryptData(request.getHash());
            JSONObject hashInfo = new JSONObject(decrypted);

            String otp = hashInfo.getString("otp");
            String email = hashInfo.getString("email");
            long time = hashInfo.getLong("time");

            if (Instant.now().toEpochMilli() > time) {
                return ErrorHelper.internalServerError("OTP_TIMEOUT");
            }

            if (!otp.equals(request.getOtp())) {
                return ErrorHelper.badRequest("OTP_INVALID");
            }

            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ErrorHelper.notFound("User not found");
            }

            User user = userOpt.get();
            user.setIsVerify(true);
            userRepository.save(user);

            String newEncrypted = otpUtil.hashData(
                    new JSONObject()
                            .put("time", Instant.now().plusSeconds(300).toEpochMilli())
                            .put("email", email)
                            .put("isVerified", true)
                            .toString());

            return ResponseEntity.ok(ApiResponse.success(newEncrypted, 200));

        } catch (Exception e) {
            return ErrorHelper.badRequest("Error verifying OTP: " + e.getMessage());
        }
    }
}
