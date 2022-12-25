package net.etaservice.appmanager.model;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "mapparam_app_info")
public class AppInfo {

    private String appName;

    private Long id;

    private String lastNotify;

    private String version;

    private String host;

    private String appCode;

}
