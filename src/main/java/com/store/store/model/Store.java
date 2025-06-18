package com.store.store.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Store extends BaseModel {
    @Column(nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isApproved = false;

    private String otp;

    private Integer otpExpireTime;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isVerify = false;

    @ManyToMany
    @JoinTable(name = "users_stores", joinColumns = @JoinColumn(name = "store_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @OneToMany(mappedBy = "store")
    private List<UserStore> userStores;

    @OneToMany(mappedBy = "store")
    private List<Reward> rewards;

    @OneToMany(mappedBy = "store")
    private List<Transaction> transactions;
}
