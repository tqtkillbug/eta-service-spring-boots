package net.etaservice.appmanager.api;

import net.etaservice.configapp.websocket.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/requestEndpoint")
    @SendTo("/user/queue/reply")
    public String handleRequest(@Payload String request) {
        // Xử lý yêu cầu từ máy khách
        String response = String.valueOf(WebSocketEventListener.onlineUsers);
        messagingTemplate.convertAndSend("/topic/onlineUserNow", response);
        return response;
    }
}