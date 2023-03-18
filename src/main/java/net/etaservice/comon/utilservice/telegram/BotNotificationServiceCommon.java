package net.etaservice.comon.utilservice.telegram;

import net.etaservice.appmanager.repository.AppInfoRepository;
import net.etaservice.appmanager.repository.RequetsAppRepository;
import net.etaservice.comon.domain.LimitedQueue;
import net.etaservice.comon.googlesheet.ISheetService;
import net.etaservice.comon.googlesheet.SheetsService;
import net.etaservice.comon.utilservice.telegram.customanotation.AnnotationHandler;
import net.etaservice.comon.utilservice.telegram.customanotation.BotRoute;
import net.etaservice.comon.utilservice.telegram.route.FinanceRoute;
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
public class BotNotificationServiceCommon {


    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    @Autowired
    private AnnotationHandler annotationHandler;

    @Autowired
    private BotNotification botNotification;

    @Autowired
    private ISheetService sheetsService;

    @Autowired
    private FinanceRoute financeRoute;


    @Value("${telegram.bot.username}")
    public String userName;

    @Value("${telegram.bot.apikey}")
    public String apiKey;


    public static LimitedQueue<Update> updates = new LimitedQueue<>(10);


    public ISheetService getSheetsService() {
        return sheetsService;
    }

    public static Map<String,String> commonCallBack(){
        Map<String,String> mapCallBackButton = new HashMap<>();
        mapCallBackButton.putAll(mapCallBackHome);
        mapCallBackButton.remove("appList");
        mapCallBackButton.remove("about");
        mapCallBackButton.remove("utiltools");
        mapCallBackButton.remove("personalFinance");
        return  mapCallBackButton;

    }

    public static Map<String,String> mapCallBackHome = new HashMap<>();;
    static {
        mapCallBackHome.put("home","Home");
        mapCallBackHome.put("appList","Apps Manager");
        mapCallBackHome.put("about","About BOT");
        mapCallBackHome.put("personalFinance","Finance");
        mapCallBackHome.put("utiltools","Util Tools");
        mapCallBackHome.put("workspaces","Work Spaces");
    }

    public static Map<String,String> mapCallBackFinance = new HashMap<>();
    static {
        mapCallBackFinance.put("wallets","Wallets");
        mapCallBackFinance.put("statistic","Analysis");
        mapCallBackFinance.put("insertspending","Insert Spending");
        mapCallBackFinance.put("addNote","Add Note");
        mapCallBackFinance.put("submitSpending","Submit");
        mapCallBackFinance.put("cancelSpending","Cancel");
        mapCallBackFinance.put("listSpending","List Spending");
    }

    public static Map<String,String> mapCallBackManaApps = new HashMap<>();;
    static {
        mapCallBackManaApps.put("mapparam","Mapparam");
        mapCallBackManaApps.put("genaccounts","Generator Account");
        mapCallBackManaApps.put("newsDay","News Day");
    }

    public static Map<String,String> mapCallBackManaAppsAction = new HashMap<>();;
    static {
        mapCallBackManaAppsAction.put("countRequest","Count Request");
    }


    public static Map<String,String> mergedMapCallBack(){
        Map<String,String> mapCallBackButton = new HashMap<>();
        mapCallBackButton.putAll(mapCallBackHome);
        mapCallBackButton.putAll(mapCallBackFinance);
        mapCallBackButton.putAll(mapCallBackManaApps);
        mapCallBackButton.putAll(mapWorkspaceCallBack);
        mapCallBackButton.putAll(mapActionTaskCallBack);
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

    public static Map<String,String> mapCommand= new HashMap<>();
    static {
        mapCommand.put("/newtask", "");
        mapCommand.put("/tasks", "");
        mapCommand.put("/tasksmap", "");
    }

    public static Map<String,String> mapWorkspaceCallBack = new HashMap<>();
    static {
        mapWorkspaceCallBack.put("workTask", "Task");
        mapWorkspaceCallBack.put("workKeepNote", "Keep Note");
    }

    public static Map<String,String> mapActionTaskCallBack = new LinkedHashMap<>(){
    };
    static {
        mapActionTaskCallBack.put("newTaskStep1", "Insert Task");
        mapActionTaskCallBack.put("newTaskStep2", "Insert Task 2");
        mapActionTaskCallBack.put("listTaskList", "List Task List");
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

        if (!mergedMapCallBack().containsKey(data) && !mapOptionsListSpend().containsKey(data)) {

            if (data.split(":").length > 1){
                annotationHandler.callMethodByAnoBotCallBack(data.split(":")[0], fromId, callbackQuery);
                return;
            }

            int[] indices = {3, 4};
            for (int i : indices) {
                if (updateList.size() > i && updateList.get(updateList.size() - i).getCallbackQuery() != null && mapCheckProcess().contains(updateList.get(updateList.size() - i).getCallbackQuery().getData())) {
                    boolean resultExecute = financeRoute.handleSpendingProcess(updateList, "", this, chatId);
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
                    boolean resultExecuted = financeRoute.handleSpendingProcess(updateList, textClient, this, chatId);
                    if (resultExecuted) {
                        return;
                    }
                }

                if (isCallbackQuerySoureSpendingAction(updateList)) {
                    boolean resultExecuted = financeRoute.handleActionSourceSpendingProcess(updateList, textClient, this, chatId);
                    if (resultExecuted) {
                        return;
                    }
                }
                if (isCallbackInertTaskProcess(updateList)) {
                    boolean isHandled =  annotationHandler.callMethodByAnoBotCallBack("newtask",message.getChatId(),update);
                    if (isHandled) return;
                }

                String comandHandler = handlerComandToCB(textClient);
                if (!comandHandler.isEmpty()){
                   boolean isHandled =  annotationHandler.callMethodByAnoBotCallBack(comandHandler,message.getChatId(),update);
                   if (isHandled) return;
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


    private boolean isCallbackInertTaskProcess(List<Update> updateList) {
        Optional<String> callbackData = Optional.empty();
        int[] indexes = { 2};
        for (int index : indexes) {
            if (updateList.size() >= index) {
                Update update = updateList.get(updateList.size() - index);
                callbackData = Optional.ofNullable(update.getCallbackQuery())
                        .map(CallbackQuery::getData);
                try {
                    String functionId = callbackData.get().trim().split(":")[0];
                    if (mapActionTaskCallBack.containsKey(functionId)) {
                        return true;
                    }
                } catch (Exception e){
                    continue;
                }
            }
        }
        return false;
    }


    private boolean isCallbackQuerySoureSpendingAction(List<Update> updateList) {
        int[] indices = {2};
        for (int i : indices) {
            if (updateList.size()-1 < i) continue;
            if (updateList.get(updateList.size() - i) == null) continue;
            if (updateList.get(updateList.size() - i).getCallbackQuery() == null) continue;
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


    public List<List<InlineKeyboardButton>> createInlineKeyboard(Map<String, String> buttonMap, Map<String, String> optionsMap) {
        int rows = (int) Math.ceil(buttonMap.size() / 3.0);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < rows; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                if (count >= buttonMap.size()) {
                    break;
                }
                String key = (String) buttonMap.keySet().toArray()[count];
                String value = buttonMap.get(key);
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(value);
                button.setCallbackData(key);
                row.add(button);
                count++;
            }
            keyboard.add(row);
        }
        if (optionsMap != null) {
            List<List<InlineKeyboardButton>> optionButton = createInlineKeyboard(optionsMap, null);
            keyboard.addAll(optionButton);
        }
        return keyboard;
    }



    public static List<List<InlineKeyboardButton>> buildCommonButton() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        Map<String, String> callBackMap = commonCallBack();
        b1.setText(callBackMap.get("home"));
        b1.setCallbackData("home");
        row1.add(b1);
        keyboard.add(row1);
        return keyboard;
    }

    public List<List<InlineKeyboardButton>> buildHomeButton() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        Map<String, String> callBackMap = mergedMapCallBack();
        b1.setText(callBackMap.get("personalFinance"));
        b1.setCallbackData("personalFinance");
        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText(callBackMap.get("workspaces"));
        b2.setCallbackData("workspaces");
        row1.add(b1);
        row1.add(b2);
        keyboard.add(row1);
        InlineKeyboardButton b12 = new InlineKeyboardButton();
        InlineKeyboardButton b22 = new InlineKeyboardButton();
        b12.setCallbackData("utiltools");
        b12.setText(callBackMap.get("utiltools"));
        b22.setText(callBackMap.get("appList"));
        b22.setCallbackData("appList");
        row2.add(b22);
        row2.add(b12);
        keyboard.add(row2);
        return keyboard;
    }

    private String handlerComandToCB(String command){
        if (command.indexOf("/") == 0){
            String[] sp = command.trim().split(" ");
            if (sp.length > 1){
                String callBack = sp[0].replace("/","").trim();
                if (callBack.trim().split("-").length > 1){
                    callBack = callBack.trim().split("-")[0];
                }
                return callBack;
            } else if (sp.length == 1){
                return command.trim().replace("/", "");
            }
        }
        return "";
    }


}
