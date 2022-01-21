package com.ocbc.backenddemo.repository;

import com.ocbc.backenddemo.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
}
