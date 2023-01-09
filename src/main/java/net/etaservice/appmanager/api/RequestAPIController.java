package net.etaservice.appmanager.api;

import com.google.gson.Gson;
import net.etaservice.appmanager.model.AppInfo;
import net.etaservice.appmanager.model.RequestApp;
import net.etaservice.appmanager.model.dto.AppInfoDTO;
import net.etaservice.appmanager.repository.AppInfoRepository;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("api/v1/free/app")
public class RequestAPIController {

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    @Autowired
    private AppInfoRepository appInfoRepository;

    @CrossOrigin
    @PostMapping("/ping")
    public String pingRequest(RequestApp requestApp, HttpServletRequest request) {
        String response = "";
        if (requestApp == null) {
        } else {
            RequestApp requestAppSave = new RequestApp();
            requestAppSave.setRequestDate(new Date());
            requestAppSave.setAppName(requestApp.getAppName());
            requestAppSave.setIpAddress(requestApp.getIpAddress());
            requetsAppRepository.saveAndFlush(requestAppSave);
            if (requestApp.getAppName() != null){
                AppInfo appInfo = appInfoRepository.findByAppCode(requestApp.getAppName());
                if (appInfo != null){
                    AppInfoDTO appInfoDTO = appInfo.toDTO();
                    response = new Gson().toJson(appInfoDTO);
                }
            }
        }
        return response;
    }

    @GetMapping("/test")
    public String test() {
        return "HAH ANH YEU EM";
    }
}
