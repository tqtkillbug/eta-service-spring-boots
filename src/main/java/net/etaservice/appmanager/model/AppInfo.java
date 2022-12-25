package net.etaservice.appmanager.model;

import lombok.Data;
import net.etaservice.appmanager.model.dto.AppInfoDTO;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@Table(name = "apps_info")
public class AppInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String appName;

    @Column(name = "last_notify", nullable = false)
    private String lastNotify;

    private String version;

    private String host;

    @Column(name = "app_code", nullable = false)
    private String appCode;

    public AppInfoDTO toDTO(){
        AppInfoDTO appInfoDTO = new AppInfoDTO();
        appInfoDTO.setAppName(this.getAppName());
        appInfoDTO.setVersion(this.getVersion());
        appInfoDTO.setLastNotify(this.getLastNotify());
        return  appInfoDTO;
    }

}
