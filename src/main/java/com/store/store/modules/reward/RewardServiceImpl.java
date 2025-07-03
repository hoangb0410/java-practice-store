package com.store.store.modules.reward;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Reward;
import com.store.store.modules.reward.dto.CreateRewardRequest;
import com.store.store.modules.reward.dto.GetRewardsRequest;
import com.store.store.modules.reward.dto.UpdateRewardRequest;

@Service
public class RewardServiceImpl implements IRewardService {
    private final RewardRepository rewardRepository;

    public RewardServiceImpl(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
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
            if (rewardRepository.findByName(req.getName()).isPresent()) {
                return ErrorHelper.badRequest("Reward with this name already exists");
            }

            Reward reward = Reward.builder()
                    .name(req.getName())
                    .pointsRequired(req.getPointsRequired())
                    .expirationDate(req.getExpirationDate())
                    .quantity(req.getQuantity())
                    .description(req.getDescription())
                    .imageUrl(req.getImageUrl())
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
            Optional<Reward> optionalReward = rewardRepository.findById(id);
            if (optionalReward.isEmpty()) {
                return ErrorHelper.notFound("Reward not found with ID: " + id);
            }

            Reward reward = optionalReward.get();

            if (req.getName() != null) {
                Optional<Reward> existingReward = rewardRepository.findByName(req.getName());
                if (existingReward.isPresent() && !existingReward.get().getId().equals(id)) {
                    return ErrorHelper.badRequest("Reward with this name already exists");
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
