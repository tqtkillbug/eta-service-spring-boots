package net.etaservice.appmanager.model.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class AppInfoDTO {

    private String appName;
    private String lastNotify;
    private String version;
    private long totalVisit;
}
