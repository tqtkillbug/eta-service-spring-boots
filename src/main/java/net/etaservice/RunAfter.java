package net.etaservice;

import net.etaservice.comon.utilservice.telegram.BotNotification;
import net.etaservice.comon.utilservice.telegram.BotNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class RunAfter {
//
//    @Autowired
//    private BotNotification botNotification;
//
//    @Scheduled(initialDelay = 1000, fixedDelay=Long.MAX_VALUE)
//    public void runAfterStartup() {
//        try {
//
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(botNotification);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }


}
