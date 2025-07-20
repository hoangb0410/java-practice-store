package com.store.store.modules.user_store;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.store.store.common.ErrorHelper;
import com.store.store.common.exception.ApiException;
import com.store.store.common.pagination.PaginateHelper;
import com.store.store.common.response.ApiResponse;
import com.store.store.constants.PointType;
import com.store.store.model.Rank;
import com.store.store.model.Store;
import com.store.store.model.Transaction;
import com.store.store.model.User;
import com.store.store.model.UserStore;
import com.store.store.modules.rank.RankRepository;
import com.store.store.modules.store.StoreRepository;
import com.store.store.modules.user.UserRepository;
import com.store.store.modules.user_store.dto.CreateTransactionRequest;
import com.store.store.modules.user_store.dto.GetListTransactionRequest;
import com.store.store.modules.user_store.repositories.TransactionRepository;
import com.store.store.modules.user_store.repositories.UserStoreRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class UserStoreServiceImpl implements IUserStoreService {
    private final UserStoreRepository userStoreRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final RankRepository rankRepository;
    private final TransactionRepository transactionRepository;

    public UserStoreServiceImpl(UserStoreRepository userStoreRepository, UserRepository userRepository,
            StoreRepository storeRepository, RankRepository rankRepository,
            TransactionRepository transactionRepository) {
        this.userStoreRepository = userStoreRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.rankRepository = rankRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> addUserToStore(Long userId, Long storeId) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                ErrorHelper.notFound("User not found with id: " + userId);
            }
            User user = optionalUser.get();

            Optional<Store> optionalStore = storeRepository.findById(storeId);
            if (optionalStore.isEmpty()) {
                ErrorHelper.notFound("Store not found with id: " + storeId);
            }
            Store store = optionalStore.get();

            if (userStoreRepository.existsByUserIdAndStoreId(userId, storeId)) {
                ErrorHelper.badRequest("User already added to this store");
            }

            UserStore userStore = UserStore.builder()
                    .user(user)
                    .store(store)
                    .build();
            userStoreRepository.save(userStore);

            return ResponseEntity.ok(ApiResponse.success("Add user to store successfully", 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("An error occurred while adding user to store: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> removeUser(Long userId, Long storeId) {
        try {
            Optional<UserStore> optional = userStoreRepository.findByUserIdAndStoreId(userId, storeId);
            if (optional.isEmpty()) {
                ErrorHelper.notFound("User is not added to this store");
            }

            userStoreRepository.delete(optional.get());

            return ResponseEntity.ok(ApiResponse.success("Removed user from store successfully", 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error removing user from store: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getListUsersOfStore(Long storeId) {
        try {
            Optional<Store> optionalStore = storeRepository.findById(storeId);
            if (optionalStore.isEmpty()) {
                ErrorHelper.notFound("Store not found with id: " + storeId);
            }
            List<User> users = optionalStore.get().getUsers();
            return ResponseEntity.ok(ApiResponse.success(users, 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error retrieving users of store: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createTransaction(Long userId, Long storeId,
            CreateTransactionRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                ErrorHelper.notFound("User not found with id: " + userId);
            }
            User user = optionalUser.get();

            Optional<Store> optionalStore = storeRepository.findById(storeId);
            if (optionalStore.isEmpty()) {
                ErrorHelper.notFound("Store not found with id: " + storeId);
            }
            Store store = optionalStore.get();

            Optional<UserStore> optionalUserStore = userStoreRepository.findByUserIdAndStoreId(userId, storeId);
            if (optionalUserStore.isEmpty()) {
                ErrorHelper.badRequest("User is not added to this store");
            }
            Rank rank = user.getRank();

            Integer point = 0;
            PointType pointType = request.getPointType();
            Integer amount = request.getAmount();
            if (pointType == PointType.FIXED) {
                point = (amount / rank.getAmount()) * rank.getFixedPoint();
            } else if (pointType == PointType.PERCENTAGE) {
                point = Math.min(
                        (int) Math.floor((amount / 1000.0) * (rank.getPercentage() / 100.0)),
                        rank.getMaxPercentagePoints());
            }

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .pointType(request.getPointType().name())
                    .pointsEarned(point)
                    .user(user)
                    .store(store)
                    .transactionDate(LocalDateTime.now())
                    .build();
            transactionRepository.save(transaction);
            addPoints(user, point);
            return ResponseEntity.ok(ApiResponse.success(transaction, 201));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error creating transaction: " + e.getMessage());
            return null;
        }
    }

    private void addPoints(User user, int addedPoints) {
        int newPoints = user.getPoints() + addedPoints;
        user.setPoints(newPoints);
        List<Rank> ranks = rankRepository.findAllByOrderByPointsThresholdDesc();
        for (Rank rank : ranks) {
            if (newPoints >= rank.getPointsThreshold()) {
                user.setRank(rank);
                break;
            }
        }
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getListTransactions(Long storeId, GetListTransactionRequest req) {
        try {
            Specification<Transaction> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("store").get("id"), storeId));
                if (req.getUserId() != null) {
                    predicates.add(cb.equal(root.get("user").get("id"), req.getUserId()));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };

            if (Boolean.TRUE.equals(req.getAll())) {
                List<Transaction> transactions = transactionRepository.findAll(spec);
                return ResponseEntity.ok(ApiResponse.success(transactions, 200));
            }

            return ResponseEntity.ok(ApiResponse.success(
                    PaginateHelper.paginate(req, transactionRepository, spec), 200));

        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching transactions: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getTransactionDetails(Long storeId, Long transactionId) {
        try {
            Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndStoreId(transactionId,
                    storeId);
            if (optionalTransaction.isEmpty()) {
                ErrorHelper.notFound("Transaction not found");
            }
            Transaction transaction = optionalTransaction.get();
            return ResponseEntity.ok(ApiResponse.success(transaction, 200));
        } catch (Exception e) {
            if (e instanceof ApiException)
                throw e;
            ErrorHelper.badRequest("Error fetching transaction details: " + e.getMessage());
            return null;
        }
    }
}
