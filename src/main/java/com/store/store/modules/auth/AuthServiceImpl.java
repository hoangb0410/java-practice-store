package com.store.store.modules.auth;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.email.EmailService;
import com.store.store.common.email.OTPUtil;
import com.store.store.common.exception.ApiException;
import com.store.store.common.jwt.JwtService;
import com.store.store.common.redis.RedisService;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Rank;
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
import com.store.store.modules.rank.RankRepository;
import com.store.store.modules.store.StoreRepository;
import com.store.store.modules.user.UserRepository;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final StoreRepository storeRepository;
    private final EmailService emailService;
    private final OTPUtil otpUtil;
    private final RankRepository rankRepository;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService, RedisService redisService, StoreRepository storeRepository,
            EmailService emailService, OTPUtil otpUtil, RankRepository rankRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.storeRepository = storeRepository;
        this.emailService = emailService;
        this.otpUtil = otpUtil;
        this.rankRepository = rankRepository;
    }

    @Value("${otp.secret-key}")
    private String otpSecretKey;

    @Override
    public ResponseEntity<ApiResponse<Object>> register(RegisterRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                ErrorHelper.badRequest("Email already exists");
            }
            if (userRepository.findByPhone(request.getPhone()).isPresent()) {
                ErrorHelper.badRequest("Phone number already exists");
            }

            Optional<Rank> lowestRankOpt = rankRepository.findTopByOrderByPointsThresholdAsc();
            System.out.println("AAA");
            if (lowestRankOpt.isEmpty()) {
                System.out.println("BBB");
                ErrorHelper.badRequest("Default rank not found");
            }
            System.out.println("CCC");
            Rank lowestRank = lowestRankOpt.get();

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .rank(lowestRank) // default rank
                    .build();
            userRepository.save(user);
            SendOtpRequest otpRequest = new SendOtpRequest();
            otpRequest.setEmail(user.getEmail());
            otpRequest.setType("user");
            ResponseEntity<ApiResponse<Object>> otpResponse = sendOTP(otpRequest);

            Object hash = otpResponse.getBody().getData();
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("hash", hash);

            return ResponseEntity.ok(ApiResponse.success(data, 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("User registration failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> login(LoginRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
            if (optionalUser.isEmpty()) {
                ErrorHelper.badRequest("Invalid credentials");
            }
            User user = optionalUser.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                ErrorHelper.badRequest("Invalid credentials");
            }

            if (!Boolean.TRUE.equals(user.getIsVerify())) {
                ErrorHelper.badRequest("Your account is not verified yet.");
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
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Login failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> logout(Long id, String type) {
        try {
            String key = "refresh_token:" + type + ":" + id;
            redisService.delete(key);
            return ResponseEntity.ok(ApiResponse.success("Logout successful", 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Logout failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> refreshToken(RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();
            if (!jwtService.isTokenValid(refreshToken)) {
                ErrorHelper.badRequest("Invalid refresh token");
            }

            String subject = jwtService.extractSubject(refreshToken);
            String[] parts = subject.split(":");
            if (parts.length != 2) {
                ErrorHelper.badRequest("Invalid token subject");
            }

            String type = parts[0];
            Long id = Long.parseLong(parts[1]);
            String key = "refresh_token:" + type + ":" + id;
            String storedToken = redisService.get(key);
            if (storedToken == null || !storedToken.equals(refreshToken)) {
                ErrorHelper.badRequest("Refresh token not found or mismatched");
            }

            if ("user".equals(type)) {
                Optional<User> user = userRepository.findById(id);
                if (user.isEmpty()) {
                    ErrorHelper.notFound("User not found");
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
                    ErrorHelper.notFound("Store not found");
                }

                String newAccessToken = jwtService.generateAccessToken(store.get());

                StoreAuthResponse response = StoreAuthResponse.builder()
                        .store(store.get())
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken)
                        .build();

                return ResponseEntity.ok(ApiResponse.success(response, 200));
            } else {
                ErrorHelper.badRequest("Unknown token type");
                return null;
            }

        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Token refresh failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> storeRegister(StoreRegisterRequest request) {
        try {
            if (storeRepository.findByEmail(request.getEmail()).isPresent()) {
                ErrorHelper.badRequest("Email already exists");
            }

            Store store = Store.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .isApproved(false)
                    .build();
            storeRepository.save(store);
            SendOtpRequest otpRequest = new SendOtpRequest();
            otpRequest.setEmail(store.getEmail());
            otpRequest.setType("store");
            ResponseEntity<ApiResponse<Object>> otpResponse = sendOTP(otpRequest);

            Object hash = otpResponse.getBody().getData();
            Map<String, Object> data = new HashMap<>();
            data.put("store", store);
            data.put("hash", hash);
            return ResponseEntity.ok(ApiResponse.success(data, 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Store registration failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> storeLogin(LoginRequest request) {
        try {
            Optional<Store> optionalStore = storeRepository.findByEmail(request.getEmail());
            if (optionalStore.isEmpty()) {
                ErrorHelper.badRequest("Invalid credentials");
            }
            Store store = optionalStore.get();

            if (!passwordEncoder.matches(request.getPassword(), store.getPassword())) {
                ErrorHelper.badRequest("Invalid credentials");
            }

            if (!Boolean.TRUE.equals(store.getIsVerify())) {
                ErrorHelper.badRequest("Your store is not verified yet.");
            }

            if (!Boolean.TRUE.equals(store.getIsApproved())) {
                ErrorHelper.badRequest("Your account is not approved yet.");
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
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Store login failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> sendOTP(SendOtpRequest request) {
        try {
            String email = request.getEmail();
            String type = request.getType();

            String limitKey = String.format("otp_limit:%s:%s", type, email);
            if (redisService.hasKey(limitKey)) {
                ErrorHelper.badRequest("Please wait 3 minutes before requesting another OTP");
            }

            String otp = otpUtil.generateOTP();
            emailService.sendOTP(email, "Confirm OTP", otp);
            redisService.set(limitKey, "sent", 180L); // TTL 3 min

            long expiryTime = Instant.now().plusSeconds(300).toEpochMilli(); // 5 min

            String encrypted = otpUtil.hashData(
                    new JSONObject()
                            .put("otp", otp)
                            .put("time", expiryTime)
                            .put("email", email)
                            .put("type", type)
                            .put("isVerified", false)
                            .toString());

            if (type.equalsIgnoreCase("user")) {
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty()) {
                    ErrorHelper.notFound("User not found");
                }
                User user = userOpt.get();
                user.setOtp(otp);
                user.setOtpExpireTime(300);
                userRepository.save(user);
            } else {
                Optional<Store> storeOpt = storeRepository.findByEmail(email);
                if (storeOpt.isEmpty()) {
                    ErrorHelper.notFound("Store not found");
                }
                Store store = storeOpt.get();
                store.setOtp(otp);
                store.setOtpExpireTime(300);
                storeRepository.save(store);
            }

            return ResponseEntity.ok(ApiResponse.success(encrypted, 200));

        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error sending OTP: " + e.getMessage());
            return null;
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
                ErrorHelper.internalServerError("OTP_TIMEOUT");
            }

            if (!otp.equals(request.getOtp())) {
                ErrorHelper.badRequest("OTP_INVALID");
            }

            String type = request.getType().toLowerCase();

            if (type.equals("user")) {
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isEmpty()) {
                    ErrorHelper.notFound("User not found");
                }
                User user = userOpt.get();
                user.setIsVerify(true);
                userRepository.save(user);
            } else if (type.equals("store")) {
                Optional<Store> storeOpt = storeRepository.findByEmail(email);
                if (storeOpt.isEmpty()) {
                    ErrorHelper.notFound("Store not found");
                }
                Store store = storeOpt.get();
                store.setIsVerify(true);
                storeRepository.save(store);
            }

            String limitKey = String.format("otp_limit:%s:%s", type, email);
            redisService.delete(limitKey);

            String newEncrypted = otpUtil.hashData(
                    new JSONObject()
                            .put("time", Instant.now().plusSeconds(300).toEpochMilli())
                            .put("email", email)
                            .put("isVerified", true)
                            .put("type", type)
                            .toString());

            return ResponseEntity.ok(ApiResponse.success(newEncrypted, 200));

        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error verifying OTP: " + e.getMessage());
            return null;
        }
    }
}
