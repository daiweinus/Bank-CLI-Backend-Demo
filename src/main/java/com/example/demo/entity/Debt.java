package com.example.demo.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * This is an entity of Debt.
 */
@Entity
@Table(name = "DEBT")
@Data
@RequiredArgsConstructor
public class Debt {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = "DEBTORNAME", nullable = false)
    private String debtorName;

    @NotNull
    @Column(name = "CREDITORNAME", nullable = false)
    private String creditorName;

    @NotNull
    @Column(name = "AMOUNT", precision = 20, scale = 2, nullable = false)
    private BigDecimal amount;

    public Debt(String debtorName, String creditorName, BigDecimal amount) {
        this.creditorName = creditorName;
        this.debtorName = debtorName;
        this.amount = amount;
    }
}
