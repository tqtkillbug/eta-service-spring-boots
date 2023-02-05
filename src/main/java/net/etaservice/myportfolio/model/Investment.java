package net.etaservice.myportfolio.model;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "investment")
public class Investment {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_investment", nullable = false)
    private String nameInvestment;

    @Column(name = "invest_in", nullable = false)
    private String investIn;

    @Column(name = "investment_info", nullable = false)
    private String investmentInfo;

    @OneToOne
    @JoinColumn(name = "transaction_id",referencedColumnName = "id", nullable = false)
    private Transaction transaction;

    @Column(nullable = false)
    @ColumnDefault("0")
    private BigDecimal profit;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Date createDate;
}
