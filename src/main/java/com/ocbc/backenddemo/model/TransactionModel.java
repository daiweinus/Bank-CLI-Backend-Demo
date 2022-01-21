package com.ocbc.backenddemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionModel {
    private String transactedUser;
    private String transactedAmount;
}
