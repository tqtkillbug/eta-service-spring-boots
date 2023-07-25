package net.etaservice.appmanager.api;

import net.etaservice.appmanager.UserCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private UserCountService userCountService;

    @MessageMapping("/user-joined")
    public void userJoined(String username) {
        userCountService.incrementUserCount();
    }

    @MessageMapping("/user-left")
    public void userLeft(String username) {
        userCountService.decrementUserCount();
    }
}