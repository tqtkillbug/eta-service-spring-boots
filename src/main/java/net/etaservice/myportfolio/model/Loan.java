package net.etaservice.myportfolio.model;


import lombok.Data;

import javax.persistence.*;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "loans")
public class Loan {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_loan", nullable = false)
    private String nameLoan;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "remain_debt_amount",nullable = false)
    private BigDecimal remainLoanAmount;

    @Column(name = "period_months", nullable = false)
    private int periodMonths;

    @Column(name = "remain_period_months", nullable = false)
    private int remainPeriodMonths;

    @Column(name = "type_loan", nullable = false)
    private int typeLoan;

    @Column(name = "loan_add_date", nullable = false)
    private Date loanAddDate;

    @Column(name = "all_attempt_loan_date", nullable = false)
    private Date allAttempLoadDate;

    @Column(name = "monthly_payment_day", nullable = false)
    private int monthlyPaymentDay;

    @Column(name = "is_delete", nullable = false, columnDefinition = "int default 0")
    private int isDelete;

    @Column(name = "is_paid", nullable = false, columnDefinition = "int default 0")
    private int isPaid;

    @OneToMany(mappedBy="loan")
    private List<Transaction> transactionList;









}
