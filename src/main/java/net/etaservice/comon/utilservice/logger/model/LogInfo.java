package net.etaservice.comon.utilservice.logger.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "log_info")
public class LogInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String profile;
    private String app;
    private String info;


    @CreationTimestamp
    private Date createDate;

}
