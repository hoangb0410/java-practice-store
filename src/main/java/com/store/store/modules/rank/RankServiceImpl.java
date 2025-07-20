package com.store.store.modules.rank;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.store.store.common.ErrorHelper;
import com.store.store.common.exception.ApiException;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Rank;
import com.store.store.model.User;
import com.store.store.modules.rank.dto.CreateRankRequest;
import com.store.store.modules.rank.dto.GetRanksRequest;
import com.store.store.modules.rank.dto.UpdateRankRequest;
import com.store.store.modules.user.UserRepository;

@Service
public class RankServiceImpl implements IRankService {

    private final RankRepository rankRepository;
    private final UserRepository userRepository;

    public RankServiceImpl(RankRepository rankRepository, UserRepository userRepository) {
        this.rankRepository = rankRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getRanks(GetRanksRequest req) {
        try {
            Specification<Rank> spec = (root, query, cb) -> {
                if (req.getSearch() != null && !req.getSearch().isBlank()) {
                    return cb.like(cb.lower(root.get("name")), "%" + req.getSearch().toLowerCase() + "%");
                }
                return cb.conjunction();
            };

            if (Boolean.TRUE.equals(req.getAll())) {
                return ResponseEntity.ok(ApiResponse.success(rankRepository.findAll(spec), 200));
            }

            return ResponseEntity.ok(ApiResponse.success(PaginateHelper.paginate(req, rankRepository, spec), 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching ranks: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Object>> createRank(CreateRankRequest request) {
        try {
            if (rankRepository.findByName(request.getName()).isPresent()) {
                ErrorHelper.badRequest("Rank with this name already exists");
            }

            Rank rank = Rank.builder()
                    .name(request.getName())
                    .pointsThreshold(request.getPointsThreshold())
                    .amount(request.getAmount())
                    .fixedPoint(request.getFixedPoint())
                    .percentage(request.getPercentage())
                    .maxPercentagePoints(request.getMaxPercentagePoints())
                    .build();
            rankRepository.save(rank);
            // Update user ranks after creating a new rank
            updateAllUserRanks();
            return ResponseEntity.ok(ApiResponse.success(rank, 201));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error create rank: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> findById(Long id) {
        try {
            Optional<Rank> rank = rankRepository.findById(id);
            if (rank.isEmpty()) {
                ErrorHelper.notFound("Rank not found");
            }
            return ResponseEntity.ok(ApiResponse.success(rank.get(), 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Find rank failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Object>> updateRank(Long id, UpdateRankRequest request) {
        try {
            Optional<Rank> optionalRank = rankRepository.findById(id);
            if (optionalRank.isEmpty()) {
                ErrorHelper.notFound("Rank not found with ID: " + id);
            }
            Rank rank = optionalRank.get();

            if (request.getName() != null) {
                Optional<Rank> existingRank = rankRepository.findByName(request.getName());
                if (existingRank.isPresent() && !existingRank.get().getId().equals(id)) {
                    ErrorHelper.badRequest("Rank with this name already exists");
                }
                rank.setName(request.getName());
            }

            if (request.getPointsThreshold() != null) {
                rank.setPointsThreshold(request.getPointsThreshold());
            }
            if (request.getAmount() != null) {
                rank.setAmount(request.getAmount());
            }
            if (request.getFixedPoint() != null) {
                rank.setFixedPoint(request.getFixedPoint());
            }
            if (request.getPercentage() != null) {
                rank.setPercentage(request.getPercentage());
            }
            if (request.getMaxPercentagePoints() != null) {
                rank.setMaxPercentagePoints(request.getMaxPercentagePoints());
            }
            rankRepository.save(rank);
            // Update user ranks after updating a rank
            updateAllUserRanks();
            return ResponseEntity.ok(ApiResponse.success(rank, 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Update rank failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Object>> deleteRank(Long id) {
        try {
            if (!rankRepository.existsById(id)) {
                ErrorHelper.notFound("Rank not found with ID: " + id);
            }
            // Clear rank for all users associated with this rank
            userRepository.clearRankByRankId(id);

            rankRepository.deleteById(id);
            // Update user ranks after deleting a rank
            updateAllUserRanks();
            return ResponseEntity.ok(ApiResponse.success("Rank deleted successfully", 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Delete rank failed: " + e.getMessage());
            return null;
        }
    }

    private void updateAllUserRanks() {
        try {
            List<Rank> ranks = rankRepository.findAllByOrderByPointsThresholdDesc();
            if (ranks.isEmpty()) {
                userRepository.clearRankForAll();
                return;
            }
            List<User> users = userRepository.findAll();
            for (User u : users) {
                for (Rank r : ranks) {
                    if (u.getPoints() >= r.getPointsThreshold()) {
                        u.setRank(r);
                        break;
                    }
                }
            }
            userRepository.saveAll(users);
        } catch (Exception e) {
            System.out.println("Error while updating user ranks: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
