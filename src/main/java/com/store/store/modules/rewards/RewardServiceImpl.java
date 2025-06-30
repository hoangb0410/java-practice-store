package com.store.store.modules.rewards;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Reward;
import com.store.store.modules.rewards.dto.GetRewardsRequest;

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
}
