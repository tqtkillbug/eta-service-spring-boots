package net.etaservice.comon.utilservice.telegram;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j

public class BotNotification extends TelegramLongPollingBot {

    @Autowired
    private BotNotificationServiceCommon notificationService;

    public String getBotUsername() {
        return "tqtmanager_bot";
    }

    @Override
    public String getBotToken() {
        return "5992308410:AAHPEcbiWqsIfQ9yhH4_1Ndi2NozWCNjuCQ";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
         this.notificationService.handlerCallbackQuery(update);
        } else  if (update.hasMessage() && update.getMessage().hasText()) {
          this.notificationService.handleMessageReceive(update);
        }
    }

   public void sendMessge(SendMessage message){
       try {
           execute(message);
       } catch (TelegramApiException e) {
           log.error("Send message to telegram error!");
           e.printStackTrace();
       }
   }


}
