package com.example.demo.service;

import com.example.demo.controller.CommandController;
import com.example.demo.entity.User;
import com.example.demo.model.TransactionModel;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
class UserServiceTest {

    @MockBean
    CommandController commandController;

    @MockBean
    UserService userService;

    @MockBean
    DebtService debtService;

    @MockBean
    UserRepository userRepository;


    @Test
    public void createUserTest() {
        userService.createUser("test2");
        assertNotNull(userRepository.findOneByUsername("test2"));
    }

    @Test
    public void topupCommandTest() {
        Map<String, Object> map = new HashMap<>();
        User user = new User("test3", BigDecimal.ZERO);
        userService.topupCommand("test3", BigDecimal.ZERO, map);
        assertEquals(user.getBalance(), BigDecimal.ZERO);
    }

    @Test
    public void payCommandTest() {
        Map<String, Object> map = new HashMap<>();
        User user = new User("test4", BigDecimal.ZERO);
        userService.payCommand("test4", "test5", BigDecimal.ZERO, map);
        assertEquals(user.getBalance(), BigDecimal.ZERO);
    }

    @Test
    public void depositTest() {
        User user = new User("test6", BigDecimal.ZERO);
        userService.deposit(user.getUsername(), BigDecimal.ZERO);
        assertEquals(user.getBalance(), BigDecimal.ZERO);
    }

    @Test
    public void withdrawTest() {
        User user = new User("test6", BigDecimal.ZERO);
        userService.withdraw(user.getUsername(), BigDecimal.ZERO);
        assertEquals(user.getBalance(), BigDecimal.ZERO);
    }

    @Test
    public void createTransactionRecordTest() {
        TransactionModel transaction = new TransactionModel();
        transaction.setTransactionName("Alice");
        transaction.setTransactionAmount(BigDecimal.valueOf(100));
        assertEquals(transaction.getTransactionName(), "Alice");
        assertEquals(transaction.getTransactionAmount(), BigDecimal.valueOf(100));
    }
}