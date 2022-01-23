package com.example.demo.repository;

import com.example.demo.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a spring data repository for the Debt entity.
 */
@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    /**
     * Find debtors from table DEBT.
     *
     * @param username debtor name
     * @return Optional Debt
     */
    @Query("SELECT d FROM Debt d WHERE d.debtorName = ?1")
    List<Debt> findByDebtor(String username);


    /**
     * Find creditors from table DEBT.
     *
     * @param username creditor username
     * @return Optional Debt
     */
    @Query("SELECT d FROM Debt d WHERE d.creditorName = ?1")
    List<Debt> findByCreditor(String username);

    /**
     * Find Debt by both debtor name and creditor name
     *
     * @param debtorName   debtor username
     * @param creditorName creditor username
     * @return Optional Debt
     */
    @Query("SELECT d FROM Debt d WHERE d.debtorName = ?1 and d.creditorName = ?2")
    Debt findByDebtorAndCreditor(String debtorName, String creditorName);

}