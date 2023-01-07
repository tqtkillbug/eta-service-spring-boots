package net.etaservice.appmanager.api;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.appmanager.model.AppInfo;
import net.etaservice.appmanager.model.RequestApp;
import net.etaservice.appmanager.model.dto.AppInfoDTO;
import net.etaservice.appmanager.repository.AppInfoRepository;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/v1/free/app")
@Slf4j
public class RequestAPIController {

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    @Autowired
    private AppInfoRepository appInfoRepository;


    @Autowired
    private NewRepository newRepository;


    @CrossOrigin
    @PostMapping("/ping")
    public String pingRequest(RequestApp requestApp, HttpServletRequest request) {
        log.info("TEST API");
        String response = "hihihiihi";
//        if (requestApp == null) {
//        } else {
//            RequestApp requestAppSave = new RequestApp();
//            requestAppSave.setRequestDate(new Date());
//            requestAppSave.setAppName(requestApp.getAppName());
//            requestAppSave.setIpAddress(requestApp.getIpAddress());
//            requetsAppRepository.saveAndFlush(requestAppSave);
//            if (requestApp.getAppName() != null){
//                AppInfo appInfo = appInfoRepository.findByAppCode(requestApp.getAppName());
//                if (appInfo != null){
//                    AppInfoDTO appInfoDTO = appInfo.toDTO();
//                    response = new Gson().toJson(appInfoDTO);
//                }
//            }
//        }
        return response;
    }

    @GetMapping("/test")
    public String test() {
        List<New> newList = new ArrayList<>();
        newList = newRepository.getListNewLastByLimit(4);
        Collections.shuffle(newList);
        String newJson =  new Gson().toJson(newList);
        return newJson;
    }

    @GetMapping("/news/last")
    public String getListNewsLast(){
        List<New> newList = new ArrayList<>();
        newList = newRepository.getListNewLastByLimit(4);
        Collections.shuffle(newList);
        String jsonNews =  new Gson().toJson(newList);
        return jsonNews;
    }

}
