package net.etaservice.configapp.websocket;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Service
public class WebSocketEventListener {

    public static int onlineUsers = 0;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        onlineUsers++;
        messagingTemplate.convertAndSend("/topic/onlineUsers", onlineUsers);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        onlineUsers--;
        if (onlineUsers < 0) onlineUsers = 0;
        messagingTemplate.convertAndSend("/topic/onlineUsers", onlineUsers);
    }
}
