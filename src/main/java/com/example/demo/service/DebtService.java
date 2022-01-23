package com.example.demo.service;

import com.example.demo.entity.Debt;
import com.example.demo.repository.DebtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DebtService {

    private final DebtRepository debtRepository;

    @Autowired
    public DebtService(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    /**
     * Find debtors from table DEBT.
     *
     * @param username debtor name
     * @return Optional Debt
     */
    public List<Debt> findByDebtor(String username) {
        return debtRepository.findByDebtor(username);
    }

    /**
     * Find creditors from table DEBT.
     *
     * @param username creditor username
     * @return Optional Debt
     */
    public List<Debt> findByCreditor(String username) {
        return debtRepository.findByCreditor(username);
    }

    /**
     * Find Debt by both debtor name and creditor name
     *
     * @param debtorName   debtor username
     * @param creditorName creditor username
     * @return Optional Debt
     */
    public Debt findByDebtorAndCreditor(String debtorName, String creditorName) {
        return debtRepository.findByDebtorAndCreditor(debtorName, creditorName);
    }


    /**
     * Update debt, and create new debt if not exist.
     *
     * @param payerName payer username
     * @param payeeName payee username
     * @param amount    the debt amount
     */
    public void updateDebt(String payerName, String payeeName, BigDecimal amount) {
        Debt debt = findByDebtorAndCreditor(payerName, payeeName);

        // if found debt update it, else crate new one
        if (debt != null) {
            BigDecimal totalDebt = amount.add(debt.getAmount());
            debt.setAmount(totalDebt);
            debtRepository.save(debt);
        } else {
            Debt newDebt = new Debt(payerName, payeeName, amount);
            debtRepository.save(newDebt);
        }
    }

}
