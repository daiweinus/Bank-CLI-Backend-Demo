package com.ocbc.backenddemo.controller;

import com.ocbc.backenddemo.entity.Debt;
import com.ocbc.backenddemo.entity.User;
import com.ocbc.backenddemo.model.ResponseModel;
import com.ocbc.backenddemo.repository.CollectionRepository;
import com.ocbc.backenddemo.repository.DebtRepository;
import com.ocbc.backenddemo.repository.UserRepository;
import com.ocbc.backenddemo.service.CollectionService;
import com.ocbc.backenddemo.service.DebtService;
import com.ocbc.backenddemo.service.UserService;
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

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@DataJpaTest
public class CommandControllerTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    DebtRepository debtRepository;

    @MockBean
    CollectionRepository collectionRepository;

    @MockBean
    ResponseModel responseModel;

    @MockBean
    User user;

    @MockBean
    List<User> userList;

    @MockBean
    Debt debt;

    UserService userService;
    DebtService debtService;
    CollectionService collectionService;
    CommandController commandController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(userRepository.findAll()).thenReturn(userList);
        User c = new User("Bob", 0);
        when(userRepository.save(c)).thenReturn(user);
    }

    @Test
    public void testLoginClient() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        userService = new UserService(userRepository, debtRepository, collectionRepository, debtService, collectionService);
        debtService = new DebtService(userRepository, debtRepository, collectionRepository);
        collectionService = new CollectionService(userRepository, debtRepository, collectionRepository);
        commandController = new CommandController(userService, debtService, collectionService);

        ResponseEntity<?> responseEntity = commandController.loginCommand("Bob");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        String jsonStr = Objects.requireNonNull(responseEntity.getBody()).toString();
        assertNotNull(jsonStr);
    }

    @Test
    public void testTestTopupClient() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CommandController commandController = new CommandController(userService, debtService, collectionService);
        ResponseEntity<?> responseEntity = commandController.topupCommand("Bob", "100");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        responseEntity = commandController.topupCommand("Bob", "-100");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testPayClient() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CommandController commandController = new CommandController(userService, debtService, collectionService);
        ResponseEntity<?> responseEntity = commandController.payCommand("Bob", "Alice", "100");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        responseEntity = commandController.payCommand("Bob", "Alice", "-100");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }
}