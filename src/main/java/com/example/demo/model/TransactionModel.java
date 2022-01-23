package com.example.demo.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * This is a model for transaction record.
 */
@Data
public class TransactionModel {

    private String transactionName;

    private BigDecimal transactionAmount;

}
