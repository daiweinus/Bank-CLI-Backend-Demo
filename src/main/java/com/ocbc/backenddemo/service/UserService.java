package com.ocbc.backenddemo.service;


import com.ocbc.backenddemo.entity.Collection;
import com.ocbc.backenddemo.entity.Debt;
import com.ocbc.backenddemo.entity.User;
import com.ocbc.backenddemo.model.TransactionModel;
import com.ocbc.backenddemo.repository.CollectionRepository;
import com.ocbc.backenddemo.repository.DebtRepository;
import com.ocbc.backenddemo.repository.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Data
public class UserService {

    private final UserRepository userRepository;
    private final DebtRepository debtRepository;
    private final CollectionRepository collectionRepository;
    private final DebtService debtService;
    private final CollectionService collectService;


    /**
     * check username in database,
     * if username be found return User, else means new user return null
     *
     * @param UserName client username
     * @return User
     */
    public User getUser(String UserName) {
        List<User> userList = userRepository.findAll();
        return userList.stream().filter(c -> c.getUser().equals(UserName)).findFirst().orElse(null);
    }

    /**
     * if user is null create new user and return, else return user
     *
     * @param userName client username
     * @return User
     */
    public User loginCommand(String userName) {
        User user = getUser(userName);

        if (user == null) {
            User newUser = new User(userName, 0);
            newUser = userRepository.save(newUser);
            return newUser;
        }
        return user;
    }

    /**
     * top up amount to client account, then check client debt list.
     * if got any debt, continue pay debt.
     *
     * @param userName client username
     * @param amount   topup amount
     * @param hashMap  topup status
     * @param isPay    check client debt
     */
    public void topupCommand(String userName, String amount, Map<String, Object> hashMap, boolean isPay) {
        User user = getUser(userName);
        if (user != null) {
            double balance = user.getBalance() + Double.parseDouble(amount);
            user.setBalance(balance);
            userRepository.save(user);
            if (debtService.getDebtByUser(userName) != null) {
                checkDebtList(userName, isPay, hashMap);
                payDebt();
            }
        }
    }

    /**
     * if payer balance more than pay amount, pay and delete debt records.
     * if payer balance less than pay amount, pay and save to debt records.
     *
     * @param payerName  payer client username
     * @param payeeName  payee client username
     * @param amount     pay amount
     * @param map        pay status
     * @param collection collection
     * @param debt       debt
     */
    public void payCommand(String payerName, String payeeName, double amount, Map<String, Object> map, Collection collection, Debt debt) {
        User payer = getUser(payerName);
        User payee = getUser(payeeName);
        if (payee != null) {
            double transactionAmount;
            double remainingBalance = 0;
            if (payer.getBalance() >= amount) {
                transactionAmount = amount;
                if (collection != null) {
                    collectionRepository.delete(collection);
                    debtRepository.delete(debt);
                }
            } else {
                transactionAmount = payer.getBalance();
                double collectAmount = amount - transactionAmount;
                collectService.addCollection(payeeName, payerName, collectAmount);
                debtService.addDebt(payerName, payeeName, collectAmount);
            }
            remainingBalance = payer.getBalance() - transactionAmount;
            payer.setBalance(remainingBalance);
            userRepository.save(payer);
            topupCommand(payeeName, String.valueOf(transactionAmount), map, true);

            Debt debtAnotherUser = debtService.getDebt(payerName, payeeName);

            if (map != null && transactionAmount != 0 && debtAnotherUser != null) {
                List<TransactionModel> transferList = new ArrayList<>();
                TransactionModel transfer = new TransactionModel();
                String transferStr = String.valueOf(transactionAmount);
                if ((transactionAmount % 1) == 0) {
                    transferStr = String.valueOf((int) transactionAmount);
                }
                transfer.setTransactedUser(payeeName);
                transfer.setTransactedAmount(transferStr);
                transferList.add(transfer);
                map.put("transfer", transferList);
            }
        } else {
            map.put("isSuccess", false);
            map.put("errorMessage", "Amount should be more than $0.");
        }

    }

    /**
     * check client debt records.
     *
     * @param userName client username
     * @param isPay    default value false
     * @param map      debt status
     */
    private void checkDebtList(String userName, boolean isPay, Map<String, Object> map) {
        List<Debt> debtList = debtService.getDebtByUser(userName);
        List<TransactionModel> transactionList = new ArrayList<>();
        double remainingBalance;
        User user;
        for (Debt d : debtList) {
            user = getUser(userName);
            remainingBalance = user.getBalance();
            if (remainingBalance > 0) {
                Collection collection = collectService.getCollectionByName(d.getDebtUser(), d.getUser());
                User collectUser = getUser(collection.getUser());
                double transferredAmount = debtService.deductDebt(d, collection, remainingBalance, user, collectUser);
                if (!isPay && transferredAmount > 0) {
                    TransactionModel transfer = new TransactionModel();
                    String transferStr = String.valueOf(transferredAmount);
                    if ((transferredAmount % 1) == 0) {
                        transferStr = String.valueOf((int) transferredAmount);
                    }
                    transfer.setTransactedUser(d.getDebtUser());
                    transfer.setTransactedAmount(transferStr);
                    transactionList.add(transfer);
                }

            }
        }

        if (map != null) {
            map.put("transfer", transactionList);
        }
    }

    /**
     * pay debt function.
     */
    private void payDebt() {
        List<User> userList = userRepository.findAll();
        userList.forEach(c -> {
            double balance = c.getBalance();
            List<Debt> debtList = debtService.getDebtByUser(c.getUser());
            debtList.forEach(d -> {
                if (balance > 0) {
                    Collection collection = collectService.getCollectionByName(d.getDebtUser(), d.getUser());
                    payCommand(d.getUser(), d.getDebtUser(), d.getAmount(), null, collection, d);
                }
            });
        });
    }
}
