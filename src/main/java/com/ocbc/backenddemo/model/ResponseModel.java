package com.ocbc.backenddemo.model;

import com.ocbc.backenddemo.entity.Collection;
import com.ocbc.backenddemo.entity.Debt;
import com.ocbc.backenddemo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {

    User user;
    List<Collection> collection = new ArrayList<>();
    List<Debt> debt = new ArrayList<>();

}
