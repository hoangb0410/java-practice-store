package com.store.store.modules.rank;

import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.model.Rank;
import com.store.store.modules.rank.dto.CreateRankRequest;
import com.store.store.modules.rank.dto.GetRanksRequest;
import com.store.store.modules.rank.dto.UpdateRankRequest;

@Service
public class RankServiceImpl implements IRankService {

    private final RankRepository rankRepository;

    public RankServiceImpl(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
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
            return ErrorHelper.badRequest("Error fetching ranks: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createRank(CreateRankRequest request) {
        try {
            if (rankRepository.findByName(request.getName()).isPresent()) {
                return ErrorHelper.badRequest("Rank with this name already exists");
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
            return ResponseEntity.ok(ApiResponse.success(rank, 201));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Error create rank: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> findById(Long id) {
        try {
            Optional<Rank> rank = rankRepository.findById(id);
            if (rank.isEmpty()) {
                return ErrorHelper.notFound("Rank not found");
            }
            return ResponseEntity.ok(ApiResponse.success(rank.get(), 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Find rank failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateRank(Long id, UpdateRankRequest request) {
        try {
            Optional<Rank> optionalRank = rankRepository.findById(id);
            if (optionalRank.isEmpty()) {
                return ErrorHelper.notFound("Rank not found with ID: " + id);
            }
            Rank rank = optionalRank.get();

            if (request.getName() != null) {
                Optional<Rank> existingRank = rankRepository.findByName(request.getName());
                if (existingRank.isPresent() && !existingRank.get().getId().equals(id)) {
                    return ErrorHelper.badRequest("Rank with this name already exists");
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
            return ResponseEntity.ok(ApiResponse.success(rank, 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Update rank failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteRank(Long id) {
        try {
            if (!rankRepository.existsById(id)) {
                return ErrorHelper.notFound("Rank not found with ID: " + id);
            }
            rankRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Rank deleted successfully", 200));
        } catch (Exception e) {
            return ErrorHelper.badRequest("Delete rank failed: " + e.getMessage());
        }
    }
}
