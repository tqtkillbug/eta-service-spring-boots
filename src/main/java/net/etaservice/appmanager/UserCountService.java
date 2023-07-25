package net.etaservice.appmanager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserCountService {

    private int userCount = 0;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void incrementUserCount() {
        userCount++;
        sendUserCountUpdate();
    }

    public void decrementUserCount() {
        userCount--;
        sendUserCountUpdate();
    }

    private void sendUserCountUpdate() {
        messagingTemplate.convertAndSend("/topic/user-count", userCount); // Sends the user count to all connected clients subscribed to "/topic/user-count"
    }
}