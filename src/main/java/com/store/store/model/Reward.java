package com.store.store.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reward extends BaseModel {
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    @JsonIgnore
    private Store store;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Column(name = "points_required", nullable = false)
    private Integer pointsRequired = 0;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String description;
}
