package net.etaservice.appmanager.repository;

import net.etaservice.appmanager.model.AppInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {

    AppInfo findByAppCode(String appCode);

}
