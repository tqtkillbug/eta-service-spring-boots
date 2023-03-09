package net.etaservice.comon.utilservice.telegram;

import net.etaservice.appmanager.model.AppInfo;
import net.etaservice.appmanager.repository.AppInfoRepository;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import net.etaservice.comon.domain.LimitedQueue;
import net.etaservice.comon.googlesheet.SheetsService;
import net.etaservice.comon.utilservice.telegram.customanotation.AnnotationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.*;

@Component
@PropertySource("application-${spring.profiles.active}.properties")
@EnableConfigurationProperties
public class BotNotificationService {


    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    @Autowired
    private AnnotationHandler annotationHandler;

    @Autowired
    private BotNotification botNotification;

    @Autowired
    private SheetsService sheetsService;

    @Autowired
    private BotController botController;


    @Value("${telegram.bot.username}")
    public String userName;

    @Value("${telegram.bot.apikey}")
    public String apiKey;


    private static LimitedQueue<Update> updates = new LimitedQueue<>(10);



   public static Map<String,String> mapCallBackButton(){
        Map<String,String> mapCallBackButton = new HashMap<>();
        mapCallBackButton.put("home","Home");
        mapCallBackButton.put("appList","App List Manager");
        mapCallBackButton.put("chatGpt","Chat GPT");
        mapCallBackButton.put("infoMap","Get Info Mapparam App");
        mapCallBackButton.put("infoGenAcc","Get Info Generator Account App");
        mapCallBackButton.put("about","About BOT");
        mapCallBackButton.put("personalFinance","Manage personal finance");
        mapCallBackButton.put("utiltools","Util Tools");
        mapCallBackButton.put("currBalance","Current account balance");
        mapCallBackButton.put("spendingThisMonth","Total spending this month");
        mapCallBackButton.put("statistic","Statistics Financial");
        mapCallBackButton.put("insertspending","Insert Spending");
        return  mapCallBackButton;
    }

    public static List<String> mapCheckProcess(){
       List<String> strings = new ArrayList<>();
       strings.add("insertspending");
       return strings;
    }

    public LimitedQueue<Update> getHistoryUpdate(){
       return updates;
    }

    public void addUpdate(Update update){
       updates.add(update);
    }

    public void sendTranferMessage(SendMessage message){
        botNotification.sendMessge(message);
    }


    public void handlerCallbackQuery(Update callbackQuery) throws Exception {
         addUpdate(callbackQuery);
        CallbackQuery query = callbackQuery.getCallbackQuery();
        String data = query.getData();
        SendMessage message = new SendMessage();
        message.setChatId(query.getFrom().getId());
        if (!mapCallBackButton().containsKey(data)){
            message.setText("Function Not Exist Or Not Available!");
        }
        annotationHandler.callMethodByAnoBotCallBack(data, query.getFrom().getId());
    }

    public void hanlderMessageReceive(Update update) throws Exception {
       addUpdate(update);
       Message message = (update.getMessage());
       String textClient = message.getText();
       String chatId = String.valueOf(message.getChatId());
       SendMessage sendMessage = new SendMessage();
       sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildCommonButton());
        switch (textClient){
           case "/home":
               sendMessage.setText("Hello Chairman Tien Tran, Welcome to TQT Manager BOT");
               inlineKeyboardMarkup.getKeyboard().addAll(buildHomeButton());
//               ArrayList<Update> chat = botNotification.getHistoryMessage(message.getChatId());
               break;
           default:
               sendMessage.setText("Invalid Command, please choose option following");
               List<Update> updateLits = updates.getListElement();
               Update update1 = updateLits.get(updateLits.size() - 2);
               System.out.println(updateLits.get(updateLits.size() - 2));
               if (updateLits.get(updateLits.size() - 2).getCallbackQuery() != null){
                   if (mapCheckProcess().contains(updateLits.get(updateLits.size() - 2).getCallbackQuery().getData())){
                       botController.handlerProcess(updateLits, textClient);
                   }
               }


        }
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotification.sendMessge(sendMessage);
    }




   public List<List<InlineKeyboardButton>> buildCommonButton() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        Map<String, String> callBackMap = mapCallBackButton();
        b1.setText(callBackMap.get("home"));
        b1.setCallbackData("home");
        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText(callBackMap.get("about"));
        b2.setCallbackData("about");
        row1.add(b1);
        row1.add(b2);
        keyboard.add(row1);
        return keyboard;
    }

    public List<List<InlineKeyboardButton>> buildHomeButton() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        Map<String, String> callBackMap = mapCallBackButton();
        b1.setText(callBackMap.get("appList"));
        b1.setCallbackData("appList");
        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText(callBackMap.get("personalFinance"));
        b2.setCallbackData("personalFinance");
        row1.add(b1);
        row1.add(b2);
        keyboard.add(row1);
        InlineKeyboardButton b12 = new InlineKeyboardButton();
        b12.setCallbackData("utiltools");
        b12.setText(callBackMap.get("utiltools"));
        row2.add(b12);
        keyboard.add(row2);
        return keyboard;
    }


    public SheetsService getSheetsService() {
        return sheetsService;
    }

    public String getInfoAppMapPram(){
        AppInfo appInfo =  appInfoRepository.findByAppCode("MAP");
        LocalDate localDate = LocalDate.now();
        Date date = java.sql.Date.valueOf(localDate);
        long count = requetsAppRepository.countByRequestDate(date);
        return appInfo.toString() + "Request today: " + count;
    }
}
