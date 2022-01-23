package com.example.demo.model;

import com.example.demo.entity.Debt;
import com.example.demo.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This is model for call api response.
 */
@Data
public class ResponseModel {

    User user;

    List<Debt> debtor = new ArrayList<>();

    List<Debt> creditor = new ArrayList<>();

}
