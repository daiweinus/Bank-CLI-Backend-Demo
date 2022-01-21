package com.ocbc.backenddemo.controller;


import com.ocbc.backenddemo.model.ResponseModel;
import com.ocbc.backenddemo.service.CollectionService;
import com.ocbc.backenddemo.service.DebtService;
import com.ocbc.backenddemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "command", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommandController {

    private final UserService userService;
    private final DebtService debtService;
    private final CollectionService collectService;

    @Autowired
    public CommandController(UserService userService, DebtService debtService, CollectionService collectionService) {
        this.userService = userService;
        this.debtService = debtService;
        this.collectService = collectionService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginCommand(@Valid @RequestParam("userName") String userName) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("isSuccess", true);
            userService.loginCommand(userName);
            buildResponseModel(map, userName);
        } catch (Exception e) {
            map.put("isSuccess", false);
        }
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/topup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> topupCommand(@Valid @RequestParam("userName") String userName, @RequestParam("amount") String amount) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("isSuccess", true);
            if (Double.parseDouble(amount) <= 0) {
                map.put("isSuccess", false);
                map.put("errorMessage", "Amount should be more than $0.");
            } else {
                userService.topupCommand(userName, amount, map, false);
            }
            buildResponseModel(map, userName);
        } catch (Exception e) {
            map.put("isSuccess", false);
        }
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> payCommand(@Valid @RequestParam("userName") String userName, @RequestParam("anotherUserName") String anotherUserName, @RequestParam("amount") String amount) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("isSuccess", true);
            if (userName.equals(anotherUserName.toLowerCase())) {
                map.put("isSuccess", false);
                map.put("errorMessage", "Payee cannot be yourself.");
            }
            if (Double.parseDouble(amount) <= 0) {
                map.put("isSuccess", false);
                map.put("errorMessage", "Amount should be more than $0.");
            } else {
                userService.payCommand(userName, anotherUserName, Double.parseDouble(amount), map, null, null);
            }
            buildResponseModel(map, userName);
        } catch (Exception e) {
            map.put("isSuccess", false);
        }
        return ResponseEntity.ok().body(map);
    }

    private void buildResponseModel(Map<String, Object> map, String userName) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setUser(userService.getUser(userName));
        responseModel.setCollection(collectService.getCollectionByName(userName));
        responseModel.setDebt(debtService.getDebtByUser(userName));
        map.put("data", responseModel);
    }
}

