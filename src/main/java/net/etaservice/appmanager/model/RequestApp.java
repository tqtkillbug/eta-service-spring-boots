package net.etaservice.appmanager.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name ="request_app")
@Getter
@Setter
@ToString
public class RequestApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name", nullable = false)
    private String appName;

    @Column(name = "ip_adress", nullable = false)
    private String ipAddress;

    @Column(name = "request_date", nullable = false)
    private Date requestDate;


}
