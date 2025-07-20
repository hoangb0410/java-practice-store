package com.store.store.modules.redemption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.store.store.common.ErrorHelper;
import com.store.store.common.exception.ApiException;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Rank;
import com.store.store.model.Redemption;
import com.store.store.model.Reward;
import com.store.store.model.User;
import com.store.store.model.UserStore;
import com.store.store.modules.rank.RankRepository;
import com.store.store.modules.redemption.dto.CreateRedemptionRequest;
import com.store.store.modules.redemption.dto.GetListRedemptionsRequest;
import com.store.store.modules.reward.RewardRepository;
import com.store.store.modules.user.UserRepository;
import com.store.store.modules.user_store.repositories.UserStoreRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class RedemptionServiceImpl implements IRedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final UserStoreRepository userStoreRepository;
    private final RankRepository rankRepository;

    public RedemptionServiceImpl(RedemptionRepository redemptionRepository, RewardRepository rewardRepository,
            UserRepository userRepository, UserStoreRepository userStoreRepository, RankRepository rankRepository) {
        this.redemptionRepository = redemptionRepository;
        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
        this.userStoreRepository = userStoreRepository;
        this.rankRepository = rankRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Object>> createRedemption(Long userId, CreateRedemptionRequest request) {
        try {
            Optional<Reward> rewardOpt = rewardRepository.findById(request.getRewardId());
            if (rewardOpt.isEmpty()) {
                ErrorHelper.notFound("Reward not found");
            }

            Reward reward = rewardOpt.get();
            Long storeId = reward.getStore().getId();

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                ErrorHelper.notFound("User not found");
            }

            User user = userOpt.get();

            Optional<UserStore> userStoreOpt = userStoreRepository.findByUserIdAndStoreId(userId, storeId);
            if (userStoreOpt.isEmpty()) {
                ErrorHelper.badRequest("User has not been added to the store");
            }

            int totalPoints = reward.getPointsRequired() * request.getQuantity();
            if (totalPoints > user.getPoints()) {
                ErrorHelper.badRequest("Not enough points to redeem");
            }

            if (totalPoints > 10000) {
                ErrorHelper.badRequest("Exceed maximum redeemable points");
            }

            int remainingQuantity = reward.getQuantity() - request.getQuantity();
            if (remainingQuantity < 0) {
                ErrorHelper.badRequest("Not enough reward quantity");
            }
            reward.setQuantity(remainingQuantity);
            rewardRepository.save(reward);

            Redemption redemption = Redemption.builder()
                    .user(user)
                    .rewardName(reward.getName())
                    .quantity(request.getQuantity())
                    .pointsDeducted(totalPoints)
                    .redemptionDate(LocalDateTime.now())
                    .build();
            redemptionRepository.save(redemption);

            int newPoints = user.getPoints() - totalPoints;
            user.setPoints(newPoints);
            userRepository.save(user);

            List<Rank> ranks = rankRepository.findAllByOrderByPointsThresholdDesc();
            for (Rank rank : ranks) {
                if (newPoints >= rank.getPointsThreshold()) {
                    user.setRank(rank);
                    userRepository.save(user);
                    break;
                }
            }
            return ResponseEntity.ok(ApiResponse.success(redemption, 201));

        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error creating redemption: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getListRedemptions(GetListRedemptionsRequest request) {
        try {
            Specification<Redemption> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (request.getUserId() != null) {
                    predicates.add(cb.equal(root.get("user").get("id"), request.getUserId()));
                }

                if (StringUtils.hasText(request.getRewardName())) {
                    predicates.add(cb.like(cb.lower(root.get("rewardName")),
                            "%" + request.getRewardName().toLowerCase() + "%"));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };
            if (Boolean.TRUE.equals(request.getAll())) {
                return ResponseEntity.ok(ApiResponse.success(redemptionRepository.findAll(spec), 200));
            }
            return ResponseEntity
                    .ok(ApiResponse.success(PaginateHelper.paginate(request, redemptionRepository, spec), 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching redemptions: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getRedemptionDetails(Long id) {
        try {
            Optional<Redemption> redemptionOpt = redemptionRepository.findById(id);
            if (redemptionOpt.isEmpty()) {
                ErrorHelper.notFound("Redemption not found");
            }
            return ResponseEntity.ok(ApiResponse.success(redemptionOpt.get(), 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching redemption details: " + e.getMessage());
            return null;
        }
    }
}
