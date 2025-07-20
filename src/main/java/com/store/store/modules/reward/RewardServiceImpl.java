package com.store.store.modules.reward;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.exception.ApiException;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Reward;
import com.store.store.model.Store;
import com.store.store.modules.reward.dto.CreateRewardRequest;
import com.store.store.modules.reward.dto.GetRewardsRequest;
import com.store.store.modules.reward.dto.UpdateRewardRequest;
import com.store.store.modules.store.StoreRepository;

@Service
public class RewardServiceImpl implements IRewardService {
    private final RewardRepository rewardRepository;
    private final StoreRepository storeRepository;

    public RewardServiceImpl(RewardRepository rewardRepository, StoreRepository storeRepository) {
        this.rewardRepository = rewardRepository;
        this.storeRepository = storeRepository;
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
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching rewards: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createReward(Long storeId, CreateRewardRequest req) {
        try {
            if (rewardRepository.findByNameAndStoreId(req.getName(), storeId).isPresent()) {
                ErrorHelper.badRequest("Reward with this name already exists for this store");
            }

            Optional<Store> optionalStore = storeRepository.findById(storeId);
            if (optionalStore.isEmpty()) {
                ErrorHelper.notFound("Store not found with id: " + storeId);
            }

            Store store = optionalStore.get();

            Reward reward = Reward.builder()
                    .name(req.getName())
                    .pointsRequired(req.getPointsRequired())
                    .expirationDate(req.getExpirationDate())
                    .quantity(req.getQuantity())
                    .description(req.getDescription())
                    .imageUrl(req.getImageUrl())
                    .store(store)
                    .build();

            rewardRepository.save(reward);
            return ResponseEntity.ok(ApiResponse.success(reward, 201));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error creating reward: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> findById(Long id) {
        try {
            Optional<Reward> reward = rewardRepository.findById(id);
            if (reward.isEmpty()) {
                ErrorHelper.notFound("Reward not found");
            }
            return ResponseEntity.ok(ApiResponse.success(reward.get(), 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching reward by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateReward(Long id, Long storeId, UpdateRewardRequest req) {
        try {
            Optional<Reward> optionalReward = rewardRepository.findById(id);
            if (optionalReward.isEmpty()) {
                ErrorHelper.notFound("Reward not found with ID: " + id);
            }

            Reward reward = optionalReward.get();

            if (req.getName() != null) {
                Optional<Reward> existingReward = rewardRepository.findByNameAndStoreId(req.getName(), storeId);
                if (existingReward.isPresent() && !existingReward.get().getId().equals(id)) {
                    ErrorHelper.badRequest("Reward with this name already exists for this store");
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
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error updating reward: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteReward(Long id) {
        try {
            if (!rewardRepository.existsById(id)) {
                ErrorHelper.notFound("Reward not found with ID: " + id);
            }
            rewardRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Reward deleted successfully", 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error deleting reward: " + e.getMessage());
            return null;
        }
    }
}
