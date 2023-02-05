package net.etaservice.myportfolio.model;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "type_spend_receive")
public class TypeSpendReceive {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_transaction", nullable = false)
    private int typeTransaction;

    @Column(name = "type_name", nullable = false)
    private String typeName;

    @Column(nullable = false)
    private String description;

    @Column( name =  "created",nullable = false)
    private Date createDate;
}
