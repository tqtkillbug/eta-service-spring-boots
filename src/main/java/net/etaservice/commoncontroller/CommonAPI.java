package net.etaservice.commoncontroller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class CommonAPI {

    @Autowired
    private NewRepository newRepository;

    @CrossOrigin
    @GetMapping("/api/v1/free/app/news/last")
    public String getListNewsLast(HttpServletRequest request){
        List<New> newList = new ArrayList<>();
        log.info("GET LIST NEWS");
        newList = newRepository.getListNewLastByLimit(4);
        log.info(newList.size() + "HIIHIHIHIHIHIIH");
        Collections.shuffle(newList);
        String jsonNews =  new Gson().toJson(newList);
        return jsonNews;
    }

    @GetMapping("/api/v1/free/app/news/test")
    public String getListNewsLasts(){
        return "Test jenkins auto build and deploy";
    }
}
