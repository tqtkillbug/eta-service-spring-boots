package net.etaservice.commoncontroller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/com/app")
public class CommonAPI {

    @Autowired
    private NewRepository newRepository;

    @CrossOrigin
    @GetMapping("/news/last")
    public String getListNewsLast(HttpServletRequest request){
        List<New> newList = new ArrayList<>();
        log.info("GET LIST NEWS");
        newList = newRepository.getListNewLastByLimit(4);
        Collections.shuffle(newList);
        String jsonNews =  new Gson().toJson(newList);
        return jsonNews;
    }

    @GetMapping("/test")
    public String getListNewsLasts(){
        return "Test jenkins auto build and deploy";
    }
}
