package net.etaservice.comon.utilservice.telegram;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfiguration {

    @Bean
    public BotNotification botNotificationInit() {
        try {
            BotNotification botNotification = new BotNotification();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(botNotification);
            return botNotification;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return new BotNotification();
    }

}
