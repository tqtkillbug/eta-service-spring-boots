package net.etaservice.appmanager;

import net.etaservice.appmanager.model.AppInfo;
import net.etaservice.appmanager.repository.AppInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppInfoService {

    @Autowired
    private AppInfoRepository appInfoRepository;


    public AppInfo getAppInfoByCode(String code) {
        return appInfoRepository.findByAppCode(code);
    }

    public AppInfo saveAppInfo(AppInfo appInfo){
        return appInfoRepository.save(appInfo);
    }
}
