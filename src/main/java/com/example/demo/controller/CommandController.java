package com.example.demo.controller;

import com.example.demo.model.ResponseModel;
import com.example.demo.service.DebtService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the controller for REST API interface.
 */
@RestController
@RequestMapping(value = "command", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommandController {

    private final UserService userService;
    private final DebtService debtService;


    @Autowired
    public CommandController(UserService userService, DebtService debtService) {
        this.userService = userService;
        this.debtService = debtService;
    }


    /**
     * Response login command.
     *
     * @param username user username
     * @return responseEntity
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginCommand(@Valid @RequestParam("username") String username) {
        Map<String, Object> map = new HashMap<>();
        map.put("isSuccess", true);

        userService.createUser(username);
        buildResponseModel(map, username);
        return ResponseEntity.ok().body(map);
    }

    /**
     * Response topup command.
     *
     * @param username the user username
     * @param amount   the amount of user topup
     * @return responseEntity
     */
    @PostMapping(value = "/topup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> topupCommand(@Valid @RequestParam("username") String username, @RequestParam("amount") String amount) {
        Map<String, Object> map = new HashMap<>();
        map.put("isSuccess", true);
        BigDecimal topupAmount = new BigDecimal(amount);

        if (topupAmount.compareTo(BigDecimal.ZERO) <= 0) {
            map.put("isSuccess", false);
            map.put("errorMessage", "Amount should be more than $0.");
        }

        userService.topupCommand(username, topupAmount, map);
        buildResponseModel(map, username);

        return ResponseEntity.ok().body(map);
    }

    /**
     * Response pay command.
     *
     * @param payerName User username
     * @param payeeName User username
     * @param amount    the amount of pay
     * @return responseEntity
     */
    @PostMapping(value = "/pay", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> payCommand(@Valid @RequestParam("payerName") String payerName, @RequestParam("payeeName") String payeeName, @RequestParam("amount") String amount) {
        Map<String, Object> map = new HashMap<>();
        BigDecimal payment = new BigDecimal(amount);
        map.put("isSuccess", true);

        if (payment.compareTo(BigDecimal.ZERO) <= 0) {
            map.put("isSuccess", false);
            map.put("errorMessage", "Amount should be more than $0.");
        }
        if (userService.findOneByUsername(payeeName) == null) {
            map.put("isSuccess", false);
            map.put("errorMessage", "Payee name not exist.");
        }
        if (payerName.equalsIgnoreCase(payeeName)) {
            map.put("isSuccess", false);
            map.put("errorMessage", "Payee name cannot be yourself.");
        }

        userService.payCommand(payerName, payeeName, payment, map);
        buildResponseModel(map, payerName);

        return ResponseEntity.ok().body(map);
    }

    /**
     * Build response model.
     *
     * @param map      hashmap for record response data
     * @param username user username
     */
    private void buildResponseModel(Map<String, Object> map, String username) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setUser(userService.findOneByUsername(username));
        responseModel.setDebtor(debtService.findByDebtor(username));
        responseModel.setCreditor(debtService.findByCreditor(username));
        map.put("data", responseModel);
    }

}
