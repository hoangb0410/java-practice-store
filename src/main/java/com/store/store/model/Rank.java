package com.store.store.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ranks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Rank extends BaseModel {
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer pointsThreshold;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer fixedPoint;

    @Column(nullable = false)
    private Float percentage;

    @Column(nullable = false)
    private Integer maxPercentagePoints;

    @OneToMany(mappedBy = "rank")
    @JsonIgnore
    private List<User> users;
}
