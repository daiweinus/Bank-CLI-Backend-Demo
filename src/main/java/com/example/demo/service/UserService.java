package com.example.demo.service;

import com.example.demo.entity.Debt;
import com.example.demo.entity.User;
import com.example.demo.model.TransactionModel;
import com.example.demo.repository.DebtRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is service class for usr command implementation.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final DebtService debtService;

    @Autowired
    public UserService(UserRepository userRepository, DebtRepository debtRepository, DebtService debtService) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
        this.debtService = debtService;
    }


    /**
     * Find Usr by username.
     *
     * @param username User username
     * @return User
     */
    public User findOneByUsername(String username) {
        return userRepository.findOneByUsername(username).orElse(null);
    }

    /**
     * Create a new User if not exist.
     *
     * @param username User username
     */
    public void createUser(String username) {
        if (findOneByUsername(username) == null)
            userRepository.save(new User(username, BigDecimal.ZERO));
    }

    /**
     * Implement topup command.
     *
     * @param username User username
     * @param amount   the amount of user topup
     * @param map      hashmap for record transaction
     */
    @Transactional
    public void topupCommand(String username, BigDecimal amount, Map<String, Object> map) {
        // topUp amount
        deposit(username, amount);
        // after user top up done, check user debt if found pay it.
        payDebt(username, map);
    }

    /**
     * Implement pay command.
     *
     * @param payerName User username
     * @param payeeName User username
     * @param amount    the amount of user pay
     * @param map       hashmap for record transaction
     */
    @Transactional
    public void payCommand(String payerName, String payeeName, BigDecimal amount, Map<String, Object> map) {
        User payer = findOneByUsername(payerName);
        // if payer.balance more than pay amount, payment equal amount
        if (payer.getBalance().compareTo(amount) >= 0) {
            withdraw(payerName, amount);
            deposit(payeeName, amount);
            // create transaction record
            createTransactionRecord(payeeName, amount, map);
        } else {
            // payer.balance less than pay amount, payment equal payer.balance
            BigDecimal payment = payer.getBalance();
            BigDecimal remainingPayment = amount.subtract(payment);
            withdraw(payerName, payment);
            deposit(payeeName, payment);
            // update remaining debt
            debtService.updateDebt(payerName, payeeName, remainingPayment);
            // create transaction record
            createTransactionRecord(payeeName, payment, map);
        }
        // after amount transacted to payee, check payee debt if found pay it.
        payDebt(payeeName, map);
    }

    /**
     * Implement transaction of deposit.
     *
     * @param username User username
     * @param amount   the amount of user deposit
     */
    @Transactional
    public void deposit(String username, BigDecimal amount) {
        User user = findOneByUsername(username);
        if (user != null) {
            user.setBalance(user.getBalance().add(amount));
            userRepository.save(user);
        }
    }

    /**
     * Implement transaction of withdraw.
     *
     * @param username User username
     * @param amount   the amount of user withdraw
     */
    @Transactional
    public void withdraw(String username, BigDecimal amount) {
        User user = findOneByUsername(username);
        if (user != null) {
            user.setBalance(user.getBalance().subtract(amount));
            userRepository.save(user);
        }
    }

    /**
     * Implement transaction of pay user debt.
     *
     * @param username User username
     * @param map      hashmap for record transaction
     */
    public void payDebt(String username, Map<String, Object> map) {
        User user = findOneByUsername(username);
        List<Debt> debts = debtService.findByDebtor(username);

        // if found debts
        if (debts != null) {
            for (Debt debt : debts) {
                BigDecimal debtAmount = debt.getAmount();

                // if user balance more than debt amount
                if (user.getBalance().compareTo(debtAmount) >= 0) {

                    // withdraw debtAmount from user
                    withdraw(username, debtAmount);

                    // deposit debtAmount to creditor
                    deposit(debt.getCreditorName(), debtAmount);

                    // create transaction record
                    createTransactionRecord(debt.getCreditorName(), debtAmount, map);

                    // delete debt from table
                    debtRepository.delete(debt);
                } else if (user.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal payment = user.getBalance();
                    BigDecimal remainingDebt = debtAmount.subtract(payment);

                    withdraw(username, payment);
                    deposit(debt.getCreditorName(), payment);

                    // create transaction record
                    createTransactionRecord(debt.getCreditorName(), payment, map);

                    // update remaining debt
                    debt.setAmount(remainingDebt);
                    debtRepository.save(debt);
                }
            }
        }
    }

    /**
     * Create the record of uer transaction.
     *
     * @param username User username of transacted
     * @param amount   the amount of user transacted
     * @param map      hashmap for record transaction
     */
    public void createTransactionRecord(String username, BigDecimal amount, Map<String, Object> map) {
        TransactionModel transaction = new TransactionModel();
        List<TransactionModel> transactionList = new ArrayList<>();

        transaction.setTransactionName(username);
        transaction.setTransactionAmount(amount);
        transactionList.add(transaction);

        map.put("transaction", transactionList);
    }

}
