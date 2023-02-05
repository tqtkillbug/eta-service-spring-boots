package net.etaservice.myportfolio.model;

import lombok.Data;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class Transaction {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ammount", nullable = false)
    private BigDecimal amount;

    @OneToOne
    @JoinColumn(name = "type_spend_receive_id",referencedColumnName = "id", nullable = false)
    private TypeSpendReceive type;

    @OneToOne
    @JoinColumn(name = "source_id",referencedColumnName = "id", nullable = false)
    private SourceTransaction source;

    @Column(name = "type_transaction", nullable = false)
    private int typeTransaction;

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    private Date createDate;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "is_delete", nullable = false, columnDefinition = "int default 0")
    private int isDelete;

    @Column(name = "is_valid", nullable = false, columnDefinition = "int default 1")
    private int isValid;

    @ManyToOne
    @JoinColumn(name = "loan",referencedColumnName = "id")
    private Loan loan;



}
