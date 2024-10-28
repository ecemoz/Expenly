package com.yildiz.expenly.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private LocalDate expenseDate;
    private LocalTime expenseTime;
    private BigDecimal totalExpense;
    private BigDecimal taxAmount;
    private BigDecimal taxlessExpense;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
}
