package net.etaservice.appmanager.api;

import net.etaservice.appmanager.model.RequestApp;
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

    @CrossOrigin
    @PostMapping("/ping")
    public String pingRequest(@RequestBody RequestApp requestApp, HttpServletRequest request) {
        if (requestApp == null) {
        } else {
            RequestApp requestAppSave = new RequestApp();
            requestAppSave.setRequestDate(new Date());
            requestAppSave.setAppName(requestApp.getAppName());
            requestAppSave.setIpAddress(request.getRemoteAddr());
            requetsAppRepository.saveAndFlush(requestAppSave);
        }
        return "OK";
    }

    @GetMapping("/test")
    public String test() {
        return "OKOKOKOKOKOKO";
    }
}
