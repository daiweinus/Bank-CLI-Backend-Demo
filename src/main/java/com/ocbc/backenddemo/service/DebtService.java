package com.ocbc.backenddemo.service;

import com.ocbc.backenddemo.entity.Collection;
import com.ocbc.backenddemo.entity.Debt;
import com.ocbc.backenddemo.entity.User;
import com.ocbc.backenddemo.repository.CollectionRepository;
import com.ocbc.backenddemo.repository.DebtRepository;
import com.ocbc.backenddemo.repository.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class DebtService {

    private final UserRepository userRepository;

    private final DebtRepository debtRepository;

    private final CollectionRepository collectionRepository;

    public DebtService(UserRepository userRepository, DebtRepository debtRepository, CollectionRepository collectionRepository) {
        this.userRepository = userRepository;
        this.debtRepository = debtRepository;
        this.collectionRepository = collectionRepository;
    }

    /**
     * if client username found in database return it
     *
     * @param userName client username
     * @param debtName username in debt database
     * @return Debt
     */
    public Debt getDebt(String userName, String debtName) {
        List<Debt> debtList = debtRepository.findAll();
        return debtList.stream().filter(
                c -> c.getUser().equals(userName) && c.getDebtUser().equals(debtName)).findFirst().orElse(null);
    }

    /**
     * if client username found in database return it
     *
     * @param userName client username
     * @return List<Debt>
     */
    public List<Debt> getDebtByUser(String userName) {
        List<Debt> debtList = debtRepository.findAll();
        return debtList.stream()
                .filter(c -> c.getUser().equals(userName))
                .collect(Collectors.toList());
    }

    /**
     * add debt record
     *
     * @param username     client username
     * @param debtUserName debt client username
     * @param amount       amount
     * @return Debt
     */
    public Debt addDebt(String username, String debtUserName, double amount) {
        List<Debt> debtList = debtRepository.findAll();
        Debt debt = debtList.stream().filter(d -> d.getUser().equals(username) && d.getDebtUser().equals(debtUserName)).findFirst().orElse(null);
        if (debt != null) {
            double debtTotal = Double.sum(debt.getAmount(), amount);
            debt.setAmount(debtTotal);
            debt = debtRepository.save(debt);
            return debt;
        } else {
            Debt newDebt = new Debt(username, debtUserName, amount);
            newDebt = debtRepository.save(newDebt);
            return newDebt;
        }
    }

    /**
     * calculate debt, if pay amount more than debt amount delete record.
     * else deduce pay amount from debt record.
     *
     * @param debt           Debt
     * @param collection     Collection
     * @param amount         pay amount
     * @param user           client user
     * @param collectionUser collection user
     * @return deductibleAmount
     */
    public double deductDebt(Debt debt, Collection collection, double amount, User user, User collectionUser) {
        double debtAmount = debt.getAmount();
        double deductibleAmount;
        double remainingBalance = user.getBalance();
        deductibleAmount = Math.min(amount, debtAmount);
        remainingBalance = remainingBalance - deductibleAmount;
        double debtTotal = debt.getAmount() - deductibleAmount;
        user.setBalance(remainingBalance);
        userRepository.save(user);

        double totalBalance = collectionUser.getBalance() + deductibleAmount;
        collectionUser.setBalance(totalBalance);
        userRepository.save(collectionUser);

        if (debtTotal == 0) {
            debtRepository.delete(debt);
            collectionRepository.delete(collection);
        } else {
            debt.setAmount(debtTotal);
            debtRepository.save(debt);
            collection.setAmount(debtTotal);
            collectionRepository.save(collection);
        }
        return deductibleAmount;
    }
}
