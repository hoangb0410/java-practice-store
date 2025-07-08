package com.store.store.modules.reward;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.jwt.JwtService;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Reward;
import com.store.store.modules.reward.dto.CreateRewardRequest;
import com.store.store.modules.reward.dto.GetRewardsRequest;
import com.store.store.modules.reward.dto.UpdateRewardRequest;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class RewardServiceImpl implements IRewardService {
    private final RewardRepository rewardRepository;
    private final HttpServletRequest request;
    private final JwtService jwtService;

    public RewardServiceImpl(RewardRepository rewardRepository, HttpServletRequest request, JwtService jwtService) {
        this.rewardRepository = rewardRepository;
        this.request = request;
        this.jwtService = jwtService;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getRewards(GetRewardsRequest req) {
        try {
            Specification<Reward> spec = (root, query, cb) -> {
                if (req.getSearch() != null && !req.getSearch().isBlank()) {
                    return cb.like(cb.lower(root.get("name")), "%" + req.getSearch().toLowerCase() + "%");
                }
                return cb.conjunction();
            };

            if (Boolean.TRUE.equals(req.getAll())) {
                return ResponseEntity.ok(ApiResponse.success(rewardRepository.findAll(spec), 200));
            }
            return ResponseEntity.ok(ApiResponse.success(PaginateHelper.paginate(req, rewardRepository, spec), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error fetching rewards: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createReward(CreateRewardRequest req) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ErrorHelper.badRequest("No token provided");
            }
            String token = authHeader.substring(7);
            String subject = jwtService.extractSubject(token);
            String[] parts = subject.split(":");
            if (parts.length != 2 || !"store".equals(parts[0])) {
                return ErrorHelper.badRequest("Invalid token subject");
            }

            Long storeId = Long.parseLong(parts[1]);

            if (rewardRepository.findByNameAndStoreId(req.getName(), storeId).isPresent()) {
                return ErrorHelper.badRequest("Reward with this name already exists for this store");
            }

            Reward reward = Reward.builder()
                    .name(req.getName())
                    .pointsRequired(req.getPointsRequired())
                    .expirationDate(req.getExpirationDate())
                    .quantity(req.getQuantity())
                    .description(req.getDescription())
                    .imageUrl(req.getImageUrl())
                    .storeId(storeId)
                    .build();

            rewardRepository.save(reward);
            return ResponseEntity.ok(ApiResponse.success(reward, 201));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error creating reward: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> findById(Long id) {
        try {
            Optional<Reward> reward = rewardRepository.findById(id);
            if (reward.isEmpty()) {
                return ErrorHelper.notFound("Reward not found");
            }
            return ResponseEntity.ok(ApiResponse.success(reward.get(), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error fetching reward by ID: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateReward(Long id, UpdateRewardRequest req) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ErrorHelper.badRequest("No token provided");
            }
            String token = authHeader.substring(7);
            String subject = jwtService.extractSubject(token);
            String[] parts = subject.split(":");
            if (parts.length != 2 || !"store".equals(parts[0])) {
                return ErrorHelper.badRequest("Invalid token subject");
            }

            Long storeId = Long.parseLong(parts[1]);
            Optional<Reward> optionalReward = rewardRepository.findById(id);
            if (optionalReward.isEmpty()) {
                return ErrorHelper.notFound("Reward not found with ID: " + id);
            }

            Reward reward = optionalReward.get();

            if (req.getName() != null) {
                Optional<Reward> existingReward = rewardRepository.findByNameAndStoreId(req.getName(), storeId);
                if (existingReward.isPresent() && !existingReward.get().getId().equals(id)) {
                    return ErrorHelper.badRequest("Reward with this name already exists for this store");
                }
                reward.setName(req.getName());
            }

            if (req.getPointsRequired() != null) {
                reward.setPointsRequired(req.getPointsRequired());
            }

            if (req.getExpirationDate() != null) {
                reward.setExpirationDate(req.getExpirationDate());
            }
            if (req.getQuantity() != null) {
                reward.setQuantity(req.getQuantity());
            }
            if (req.getDescription() != null) {
                reward.setDescription(req.getDescription());
            }
            if (req.getImageUrl() != null) {
                reward.setImageUrl(req.getImageUrl());
            }
            rewardRepository.save(reward);
            return ResponseEntity.ok(ApiResponse.success(reward, 200));

        } catch (Exception e) {
            return ErrorHelper.badRequest("Error updating reward: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteReward(Long id) {
        try {
            if (!rewardRepository.existsById(id)) {
                return ErrorHelper.notFound("Reward not found with ID: " + id);
            }
            rewardRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Reward deleted successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error deleting reward: " + e.getMessage());
        }
    }
}
