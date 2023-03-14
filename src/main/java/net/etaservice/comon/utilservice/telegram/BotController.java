package net.etaservice.comon.utilservice.telegram;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.SneakyThrows;
import net.etaservice.appmanager.repository.AppInfoRepository;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import net.etaservice.comon.StringUtils;
import net.etaservice.comon.googlesheet.SheetUtils;
import net.etaservice.comon.googlesheet.SheetsService;
import net.etaservice.comon.utilservice.telegram.customanotation.BotCallBack;
import net.etaservice.comon.utilservice.telegram.customanotation.BotRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.script.ScriptException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@BotRoute
public class BotController {




    @BotCallBack(name = "home")
    public void handlerStart(BotNotificationServiceCommon BotNotificationServiceCommon, Long chatId, Update updateParam) throws Exception {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        message.setText("/home");
        update.setMessage(message);
        BotNotificationServiceCommon.handleMessageReceive(update);
    }

    @BotCallBack(name = "about")
    public void handlerAbout(BotNotificationServiceCommon BotNotificationServiceCommon, Long chatId, Update updateParam) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("" +
                "<b>TQT MANAGER BOT</b>\n" +
                "<b><i>Design & Develop By TQT</i></b>\n" +
                "<code>tqtteams1st@gmail.com</code>\n" +
                "<code>0855686609</code>");
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        BotNotificationServiceCommon.sendTranferMessage(sendMessage);
    }




    @BotCallBack(name = "utiltools")
    public void handlerUtilTool(BotNotificationServiceCommon BotNotificationServiceCommon, Long chatId, Update updateParam) {
        System.out.println("utiltools Calling");
    }





}
