package net.etaservice.myportfolio.model;


import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "source_transaction")
public class SourceTransaction  {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_transaction", nullable = false)
    private int typeTransaction;

    @OneToOne
    @JoinColumn(name = "wallet_id",referencedColumnName = "id")
    private WalletInfo walletInfo;

    @Column(name = "source_name", nullable = false)
    private String sourceName;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name =  "created", nullable = false)
    private Date createDate;

}
