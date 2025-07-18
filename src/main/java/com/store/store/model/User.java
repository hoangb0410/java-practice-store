package com.store.store.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseModel {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private Integer points = 0;

    @JsonIgnore
    private String otp;

    @JsonIgnore
    private Integer otpExpireTime;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isAdmin = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerify = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id", nullable = false)
    @JsonIgnore
    private Rank rank;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserStore> userStores;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Redemption> redemptions;
}
