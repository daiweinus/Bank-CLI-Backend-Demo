package com.example.demo.service;

import com.example.demo.entity.Debt;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class DebtServiceTest {

    @MockBean
    DebtRepository debtRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    DebtService debtService;

    @Test
    public void findByDebtorTest() {
        Debt debt = new Debt("test1", "test2", BigDecimal.ZERO);
        debtRepository.save(debt);
        assertNotNull(debtService.findByDebtor("test1"));
    }

    @Test
    public void findByCreditorTest() {
        Debt debt = new Debt("test3", "test4", BigDecimal.ZERO);
        debtRepository.save(debt);
        assertNotNull(debtService.findByCreditor("test4"));
    }

    @Test
    public void findByDebtorAndCreditorTest() {
        Debt debt = new Debt("test5", "test6", BigDecimal.valueOf(100));
        debtRepository.save(debt);
        assertNull(debtService.findByDebtorAndCreditor("test5", "test6"));
    }

}