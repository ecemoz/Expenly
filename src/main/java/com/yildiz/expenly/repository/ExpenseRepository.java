package com.yildiz.expenly.repository;

import com.yildiz.expenly.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCompany_Id(Long companyId);
}
