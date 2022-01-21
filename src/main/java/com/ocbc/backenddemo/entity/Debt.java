package com.ocbc.backenddemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "debt")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Debt {

    @Id
    @GeneratedValue
    private Long id;

    private String user;
    private String debtUser;

    @NotNull(message = "The amount cannot be empty.")
    private double amount;

    public Debt(String user, String debtUser, double amount) {
        this.user = user;
        this.debtUser = debtUser;
        this.amount = amount;
    }

}