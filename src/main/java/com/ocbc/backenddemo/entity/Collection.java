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
@Table(name = "collection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Collection {

    @Id
    @GeneratedValue
    private Long id;

    private String user;
    private String collectionName;

    @NotNull(message = "The amount cannot be empty.")
    private double amount;

    public Collection(String user, String collectionName, double amount) {
        this.user = user;
        this.collectionName = collectionName;
        this.amount = amount;
    }
}
