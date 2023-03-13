package net.etaservice.appmanager.api;

import com.google.gson.Gson;
import net.etaservice.appmanager.model.AppInfo;
import net.etaservice.appmanager.model.RequestApp;
import net.etaservice.appmanager.model.dto.AppInfoDTO;
import net.etaservice.appmanager.repository.AppInfoRepository;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import net.etaservice.configapp.metric.ApiMetrics;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/v1/free/app")
public class RequestAPIController {

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private NewRepository newRepository;

    @Autowired
    private ApiMetrics apiMetrics;


    @CrossOrigin
    @PostMapping("/ping")
    public String pingRequest(RequestApp requestApp, HttpServletRequest request) {
        String response = "";
        if (requestApp == null) {
        } else {
            if (requestApp.getAppName() != null){
                apiMetrics.increaseCount("api/v1/free/app/ping/" +requestApp.getAppName());
                AppInfo appInfo = appInfoRepository.findByAppCode(requestApp.getAppName());
                if (appInfo != null){
                    AppInfoDTO appInfoDTO = appInfo.toDTO();
                    response = new Gson().toJson(appInfoDTO);
                }
            }
        }
        return response;
    }

//    @GetMapping("/test")
//    public String test() {
//        List<New> newList = new ArrayList<>();
//        newList = newRepository.getListNewLastByLimit(4);
//        Collections.shuffle(newList);
//        String newJson =  new Gson().toJson(newList);
//        return newJson;
//    }

    @CrossOrigin
    @GetMapping("/news/last")
    public String getListNewsLast(){
        apiMetrics.increaseCount("api/v1/free/app/news/last");
        List<New> newList = new ArrayList<>();
        newList = newRepository.getListNewLastByLimit(5);
        Collections.shuffle(newList);
        String jsonNews =  new Gson().toJson(newList);
        return jsonNews;
    }

}
