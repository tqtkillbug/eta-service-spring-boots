package net.codejava.commoncontroller;

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
        return "anh yeu em nhieu lam";
    }
}
