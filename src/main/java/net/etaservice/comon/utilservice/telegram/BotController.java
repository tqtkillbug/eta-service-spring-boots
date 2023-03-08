package net.etaservice.comon.utilservice.telegram;

import com.google.api.services.sheets.v4.model.ValueRange;
import net.etaservice.comon.Constant;
import net.etaservice.comon.googlesheet.SheetUtils;
import net.etaservice.comon.googlesheet.SheetsService;
import net.etaservice.comon.utilservice.telegram.customanotation.BotCallBack;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BotController {


    @BotCallBack(name = "home")
    public void handlerStart(BotNotificationService botNotificationService, Long chatId) throws Exception {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        message.setText("/home");
        update.setMessage(message);
        botNotificationService.hanlderMessageReceive(update);
    }

    @BotCallBack(name = "about")
    public void handlerAbout(BotNotificationService botNotificationService, Long chatId) throws Exception {
        String aboutBot = "******************TQT MANAGER BOT*************** \n This Bot is design and develop by TQT(Tran Quang Tien) \n Contact : tqtteams1st@gmail.com";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(aboutBot);
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }


    @BotCallBack(name = "appList")
    public void handlerAppList() {
        System.out.println("AppList Calling");
    }

    @BotCallBack(name = "utiltools")
    public void handlerUtilTool() {
        System.out.println("utiltools Calling");
    }


    @BotCallBack(name = "personalFinance")
    public void handlerManagerPeronalFinance(BotNotificationService botNotificationService, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Functions to get balance, statistics for your personal financial management:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "currBalance")
    public void getCurrBalance(BotNotificationService botNotificationService, Long chatId) throws GeneralSecurityException, IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setText("No Data");
        SheetsService sheetsService = botNotificationService.getSheetsService();
        String spreadsheetId = Constant.SHEET_CHI_TIEU;
        String cell = "Common!E13";
        StringBuilder textResult = new StringBuilder("");
        ValueRange response = sheetsService.service().spreadsheets().values()
                .get(spreadsheetId, cell)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values != null && !values.isEmpty()) {
            textResult.append("Số tiền khả dụng của bạn hiện tại là: ");
            textResult.append(values.get(0).get(0));
            sendMessage.setText(textResult.toString());
        }
        inlineKeyboardMarkup.setKeyboard(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "statistic")
    public void statisticTotalBalance(BotNotificationService botNotificationService, Long chatId) throws GeneralSecurityException, IOException {
        Map<String,String> mapDataResult = getDataFinancialFromSheet(botNotificationService);
        String buildTable = buildStatisticFinancial(mapDataResult);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(buildTable);
        sendMessage.setParseMode("Markdown");
        botNotificationService.sendTranferMessage(sendMessage);
    }

    private Map<String,String> getDataFinancialFromSheet(BotNotificationService botNotificationService) throws GeneralSecurityException, IOException {
        Map<String,String> cellAndValueMap = new HashMap<>();
        SheetsService sheetsService = botNotificationService.getSheetsService();
        String spreadsheetId = Constant.SHEET_CHI_TIEU;
        String rangeShet = "TQT!E2:E16";
        ValueRange response = sheetsService.service().spreadsheets().values()
                .get(spreadsheetId, rangeShet)
                .execute();
        String range = rangeShet.split("!")[1];
        cellAndValueMap.put("E2",SheetUtils.getCellValue(response,range,"E2"));
        cellAndValueMap.put("E3",SheetUtils.getCellValue(response,range,"E3"));
        cellAndValueMap.put("E4",SheetUtils.getCellValue(response,range,"E4"));
        cellAndValueMap.put("E5",SheetUtils.getCellValue(response,range,"E5"));
        cellAndValueMap.put("E7",SheetUtils.getCellValue(response,range,"E7"));
        cellAndValueMap.put("E8",SheetUtils.getCellValue(response,range,"E8"));
        cellAndValueMap.put("E9",SheetUtils.getCellValue(response,range,"E9"));
        cellAndValueMap.put("E10",SheetUtils.getCellValue(response,range,"E10"));
        cellAndValueMap.put("E16",SheetUtils.getCellValue(response,range,"E16"));
        return cellAndValueMap;
    }




private String buildStatisticFinancial(Map<String, String> data) {
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("| Cash                 |    ").append(data.get("E2")).append("\n");
    messageBuilder.append("| BIDV                 |    ").append(data.get("E3")).append("\n");
    messageBuilder.append("| Timo                 |    ").append(data.get("E4")).append("\n");
    messageBuilder.append("| Timo credit     |    ").append(data.get("E5")).append("\n");
    messageBuilder.append("| Momo              |    ").append(data.get("E7")).append("\n");
    messageBuilder.append("| ShopeePay      |    ").append(data.get("E8")).append("\n");
    messageBuilder.append("| ZaloPay           |    ").append(data.get("E9")).append("\n");
    messageBuilder.append("| VnPay              |    ").append(data.get("E10")).append("\n");
    messageBuilder.append("| Total balances  |    ").append(data.get("E16")).append("\n");
    return messageBuilder.toString();
}





    public List<List<InlineKeyboardButton>> buildFunctionPersonalFinance() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        Map<String, String> callBackMap = BotNotificationService.mapCallBackButton();
        b1.setText(callBackMap.get("currBalance"));
        b1.setCallbackData("currBalance");
        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText(callBackMap.get("statistic"));
        b2.setCallbackData("statistic");
        row1.add(b1);
        row1.add(b2);
        keyboard.add(row1);
        return keyboard;
    }



}
