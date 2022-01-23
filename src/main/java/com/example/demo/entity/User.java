package com.example.demo.entity;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

/**
 * This is an entity of User.
 */
@Entity
@Table(name = "USER")
@Data
@RequiredArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(name = "USERNAME", length = 20, nullable = false)
    private String username;

    @Column(name = "BALANCE", precision = 20, scale = 2, nullable = false)
    private BigDecimal balance;

    public User(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }
}
