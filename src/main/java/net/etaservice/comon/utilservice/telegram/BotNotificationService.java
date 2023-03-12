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
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
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


    public static LimitedQueue<Update> updates = new LimitedQueue<>(10);


    public SheetsService getSheetsService() {
        return sheetsService;
    }

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
        mapCallBackButton.put("wallets","Wallets");
        mapCallBackButton.put("spendingThisMonth","Total spending this month");
        mapCallBackButton.put("statistic","Analysis");
        mapCallBackButton.put("insertspending","Insert Spending");
        mapCallBackButton.put("addNote","Thêm ghi chú");
        mapCallBackButton.put("submitSpending","Submit");
        mapCallBackButton.put("cancelSpending","Cancel");
        mapCallBackButton.put("listSpending","List Spending");
        return  mapCallBackButton;
    }
    public static Map<String,String> mapSpendingType(){
        Map<String,String> mapSpendingTypeCallBack = new HashMap<>();
        mapSpendingTypeCallBack.put("anuong","Ăn uống");
        mapSpendingTypeCallBack.put("tieuvat","Tiêu vặt");
        mapSpendingTypeCallBack.put("muadodung","Mua đồ dùng");
        mapSpendingTypeCallBack.put("tienphong","Tiền phòng");
        mapSpendingTypeCallBack.put("tiengiatdo","Tiền giặt đồ");
        mapSpendingTypeCallBack.put("tiendichoi","Tiền đi chơi");
        mapSpendingTypeCallBack.put("tranotindung","Trả nợ tín dụng");
        mapSpendingTypeCallBack.put("tuthien","Từ Thiện");
        mapSpendingTypeCallBack.put("cattoc","Cắt tóc");
        mapSpendingTypeCallBack.put("chomuon","Cho mượn");
        mapSpendingTypeCallBack.put("muadichvu","Mua dịch vụ");
        return  mapSpendingTypeCallBack;
    }

    public static Map<String,String> mapSourceSpending(){
        Map<String,String> mapSourceSpendingCallBack = new HashMap<>();
        mapSourceSpendingCallBack.put("bidv","BIDV");
        mapSourceSpendingCallBack.put("tienmat","Tiền mặt");
        mapSourceSpendingCallBack.put("tindung","Tín dụng(ghi nợ)");
        mapSourceSpendingCallBack.put("momo","Momo");
        mapSourceSpendingCallBack.put("zalopay","ZaloPlay");
        mapSourceSpendingCallBack.put("shoppePay","Shoppe Pay");
        mapSourceSpendingCallBack.put("vnpay","VNPay");
        mapSourceSpendingCallBack.put("timo","Timo");
        return  mapSourceSpendingCallBack;
    }

    public static Map<String,String> mapSourceSpendingAction(){
        Map<String,String> m = new HashMap<>();
        m.put("deposit","Deposit");
        m.put("withdraw","Withdraw");
        return m;
    }

    public static Map<String,String> mapOptionsListSpend(){
        Map<String,String> m = new HashMap<>();
        m.put("todaySpendings","Today");
        return m;
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
        List<Update> updateList = updates.getListElement();
        CallbackQuery query = callbackQuery.getCallbackQuery();
        String data = query.getData();
        Long fromId = query.getFrom().getId();
        String chatId = String.valueOf(fromId);
        SendMessage message = new SendMessage();
        message.setChatId(fromId);

        if (!mapCallBackButton().containsKey(data) && !mapOptionsListSpend().containsKey(data)) {

            if (data.split(":").length > 1){
                annotationHandler.callMethodByAnoBotCallBack(data.split(":")[0], fromId, callbackQuery);
                return;
            }

            int[] indices = {3, 4};
            for (int i : indices) {
                if (updateList.size() > i && updateList.get(updateList.size() - i).getCallbackQuery() != null && mapCheckProcess().contains(updateList.get(updateList.size() - i).getCallbackQuery().getData())) {
                    boolean resultExecute = botController.handleSpendingProcess(updateList, "", this, chatId);
                    if (resultExecute) return;
                }
            }
            message.setText("Function Not Exist Or Not Available!");
            botNotification.sendMessge(message);
            return;
        }

        annotationHandler.callMethodByAnoBotCallBack(data, fromId, callbackQuery);
    }

    public void handleMessageReceive(Update update) throws Exception {
        addUpdate(update);
        Message message = update.getMessage();
        String textClient = message.getText();
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildCommonButton());
        List<Update> updateList = updates.getListElement();
        switch (textClient) {
            case "/home":
                sendMessage.setText("Hello Chairman Tien Tran, Welcome to TQT Manager BOT");
                inlineKeyboardMarkup.getKeyboard().addAll(buildHomeButton());
                break;
            default:
                if (isCallbackQuerySpendingProcess(updateList)) {
                    boolean resultExecuted = botController.handleSpendingProcess(updateList, textClient, this, chatId);
                    if (resultExecuted) {
                        return;
                    }
                }

                if (isCallbackQuerySoureSpendingAction(updateList)) {
                    boolean resultExecuted = botController.handleActionSourceSpendingProcess(updateList, textClient, this, chatId);
                    if (resultExecuted) {
                        return;
                    }
                }
                sendMessage.setText("Invalid Command, please choose option following");
                break;
        }
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotification.sendMessge(sendMessage);
    }

    private boolean isCallbackQuerySpendingProcess(List<Update> updateList) {
        Optional<String> callbackData = Optional.empty();
        int[] indexes = { 2, 6 };
        for (int index : indexes) {
            if (updateList.size() >= index) {
                Update update = updateList.get(updateList.size() - index);
                callbackData = Optional.ofNullable(update.getCallbackQuery())
                        .map(CallbackQuery::getData);
                if (callbackData.isPresent() && mapCheckProcess().contains(callbackData.get())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCallbackQuerySoureSpendingAction(List<Update> updateList) {
        int[] indices = {2};
        for (int i : indices) {
            String callBackData = updateList.get(updateList.size() - i).getCallbackQuery().getData();
            if (callBackData.split(":").length < 2){
                return false;
            }
            if (updateList.size() > i && updateList.get(updateList.size() - i).getCallbackQuery() != null && mapSourceSpendingAction().containsKey(callBackData.split(":")[0])) {
                return true;
            }
        }
        return false;
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

}
