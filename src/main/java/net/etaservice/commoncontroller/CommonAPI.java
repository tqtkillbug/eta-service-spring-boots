package net.etaservice.commoncontroller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.appmanager.model.RequestApp;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import net.etaservice.comon.utilservice.telegram.BotNotification;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.repository.NewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(" ")
public class CommonAPI {

    @Autowired
    private NewRepository newRepository;

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    @CrossOrigin
    @GetMapping("/news/last")
    public String getListNewsLast(HttpServletRequest request){
        List<New> newList = new ArrayList<>();
        log.info("GET LIST NEWS");
        newList = newRepository.getListNewLastByLimit(5);
        Collections.shuffle(newList);
        String jsonNews =  new Gson().toJson(newList);
        return jsonNews;
    }

    @GetMapping("/test")
    public String getListNewsLasts(){
        long h = requetsAppRepository.countByRequestDate(new Date());

        return "Test jenkins auto build and deploy" + h;
    }
}
