package com.store.store.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "redemptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Redemption extends BaseModel {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "reward_name", nullable = false)
    private String rewardName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "points_deducted", nullable = false)
    private Integer pointsDeducted;

    @Column(name = "redemption_date", nullable = false)
    private LocalDateTime redemptionDate;
}
