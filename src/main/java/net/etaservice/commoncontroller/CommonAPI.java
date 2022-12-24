package net.etaservice.commoncontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonAPI {

    @GetMapping("/news/last")
    public String getListNewsLast(){
        return "/";
    }

    @GetMapping("/test")
    public String getListNewsLasts(){
        return "Test jenkins auto build and deploy";
    }
}
