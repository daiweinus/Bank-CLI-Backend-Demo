package com.example.demo.controller;

import com.example.demo.entity.Debt;
import com.example.demo.entity.User;
import com.example.demo.model.ResponseModel;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DebtService;
import com.example.demo.service.UserService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CommandControllerTest {

    @MockBean
    ResponseModel responseModel;

    @MockBean
    User user;

    @MockBean
    List<User> userList;

    @MockBean
    Debt debt;

    @MockBean
    UserService userService;

    @MockBean
    DebtService debtService;

    @MockBean
    CommandController commandController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DebtRepository debtRepository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(userRepository.findAll()).thenReturn(userList);
        User c = new User("test", BigDecimal.ZERO);
        when(userRepository.save(c)).thenReturn(user);
    }

    @Test
    public void loginClientTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        userService = new UserService(userRepository, debtRepository, debtService);
        debtService = new DebtService(debtRepository);
        commandController = new CommandController(userService, debtService);

        ResponseEntity<?> responseEntity = commandController.loginCommand("Alice");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        String jsonStr = Objects.requireNonNull(responseEntity.getBody()).toString();
        assertNotNull(jsonStr);
    }

    @Test
    public void topupClientTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CommandController commandController = new CommandController(userService, debtService);
        ResponseEntity<?> responseEntity = commandController.topupCommand("Alice", "100");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void payClientTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CommandController commandController = new CommandController(userService, debtService);
        ResponseEntity<?> responseEntity = commandController.payCommand("Alice", "Bob", "100");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }
}