package net.etaservice.generatoraccount;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Account {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String password;

    @Column(name = "is_deleted",columnDefinition = "int default 0", nullable = false)
    private int isDeleted;


}
