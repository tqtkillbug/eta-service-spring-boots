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
public class BotController {


    @Autowired
    private AppInfoRepository appInfoRepository;

    @Autowired
    private RequetsAppRepository requetsAppRepository;

    private static List<Object> listDataSheet = new ArrayList<>();

    private static Map<String, String> optionMapButtonFinance() {
        Map<String, String> map = new HashMap<>();
        map.put("cancelSpending", "Cancel");
        return map;
    }

    @BotCallBack(name = "home")
    public void handlerStart(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws Exception {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        message.setText("/home");
        update.setMessage(message);
        botNotificationService.handleMessageReceive(update);
    }

    @BotCallBack(name = "about")
    public void handlerAbout(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("" +
                "<b>      TQT MANAGER BOT</b>\n" +
                "<b><i>Design & Develop By TQT</i></b>\n" +
                "<code>tqtteams1st@gmail.com</code>\n" +
                "<code>0855686609</code>");
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }


    @BotCallBack(name = "appList")
    public void handlerAppList(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        findRowEmpty(botNotificationService);
    }

    @BotCallBack(name = "utiltools")
    public void handlerUtilTool(BotNotificationService botNotificationService, Long chatId, Update updateParam) {
        System.out.println("utiltools Calling");
    }


    @BotCallBack(name = "personalFinance")
    public void handlerManagerPeronalFinance(BotNotificationService botNotificationService, Long chatId, Update updateParam) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Functions to get balance, statistics for your personal financial management:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "wallets")
    public void getCurrBalance(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setText("List Your Wallet Existing, Choose wallet want to manage");
        Map<String, String> mapManaWallet = new HashMap<>();
        for (var entry : BotNotificationService.mapSourceSpending().entrySet()) {
            mapManaWallet.put("manaWallet:" + entry.getKey(), entry.getValue());
        }
        inlineKeyboardMarkup.setKeyboard(createInlineKeyboard(mapManaWallet, null));
        inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "statistic")
    public void statisticTotalBalance(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        Map<String, String> mapDataResult = getDataFinancialFromSheet(botNotificationService);
        String buildTable = buildStatisticFinancial(mapDataResult);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText(buildTable);
        sendMessage.setParseMode("html");
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "insertspending")
    public void insertNewSpending(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String cuurDate = currentDate.format(formatter);
        sendMessage.setText("Bắt đầu nhập khoản chi tiêu, hôm nay là ngày " + cuurDate);

        listDataSheet.clear();
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "addNote")
    public void addNote(BotNotificationService botNotificationService, Long chatId, Update updateParam) {
        List<Update> updates = BotNotificationService.updates.getListElement();
        if (updates.get(updates.size() - 5).getCallbackQuery() != null && updates.get(updates.size() - 5).getCallbackQuery().getData().equals("insertspending")) {
            if (listDataSheet.size() == 6) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Hãy điền ghi chú cho khoản chi tiêu này:");
                botNotificationService.sendTranferMessage(sendMessage);
            }
        }

    }

    @BotCallBack(name = "submitSpending")
    public void submitSpending(BotNotificationService botNotificationService, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        if (listDataSheet.size() > 5) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            boolean isComplete = insertSpendingDataToSheet(botNotificationService, listDataSheet);
            String message = "Submit spending suscess!";
            if (!isComplete) {
                message = "Submit spending error!, please try again!";
            }
            sendMessage.setText(message);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
            inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            botNotificationService.sendTranferMessage(sendMessage);
        }
    }

    @BotCallBack(name = "cancelSpending")
    public void cancelSpending(BotNotificationService botNotificationService, Long chatId, Update updateParam) {
        String message = "Cancel spending suscess!";
        listDataSheet.clear();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "manaWallet")
    public void manageWallet(BotNotificationService botNotificationService, Long chatId, Update update) {
        String walletId = update.getCallbackQuery().getData().split(":")[1];
        String message = "You are choosing <b>" + BotNotificationService.mapSourceSpending().get(walletId) + "</b> please select actions with your wallet";
        SendMessage sendMessage = new SendMessage();
        Map<String, String> mapWalletAction = new HashMap<>();
        for (var entry : BotNotificationService.mapSourceSpendingAction().entrySet()) {
            mapWalletAction.put(entry.getKey() + ":" + walletId, entry.getValue());
        }
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(createInlineKeyboard(mapWalletAction, null));
        inline.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "deposit")
    public void deppositToWallet(BotNotificationService botNotificationService, Long chatId, Update update) {
        String walletId = update.getCallbackQuery().getData().split(":")[1];
        String message = "Enter amout you want deposit to <b>" + BotNotificationService.mapSourceSpending().get(walletId) + "</b>";
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(botNotificationService.buildCommonButton());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        botNotificationService.sendTranferMessage(sendMessage);
    }


    @BotCallBack(name = "withdraw")
    public void withdrawFromWallet(BotNotificationService botNotificationService, Long chatId, Update update) {
        String walletId = update.getCallbackQuery().getData().split(":")[1];
        String message = "Enter amout you want withdraw from <b>" + BotNotificationService.mapSourceSpending().get(walletId) + "</b>";
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(botNotificationService.buildCommonButton());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    public boolean handleActionSourceSpendingProcess(List<Update> updateList, String amount, BotNotificationService botNotificationService, String chatId) throws GeneralSecurityException, IOException, ScriptException {
        if (updateList.get(updateList.size() - 2).getCallbackQuery() != null && StringUtils.isNumberic(amount)) {
            String callBackData = updateList.get(updateList.size() - 2).getCallbackQuery().getData();
            String action = callBackData.split(":")[0];
            String rangeSheet = "COMMON!D75:D82";
            String range = rangeSheet.split("!")[1];
            ValueRange response = botNotificationService.getSheetsService().getDataSheetWithFormula(rangeSheet);
            String walletId = callBackData.split(":")[1];
            String formula = SheetUtils.getCellValue(response, range, mapSourceSpendingAction().get(walletId));
            String newFormula = formula;
            String message = "";
            if (action.equals("deposit")) {
                newFormula += "+" + amount.trim();
                message = "Deposit <i>" + StringUtils.formatCuurencyVnd(amount) + "</i> to <b>" + BotNotificationService.mapSourceSpending().get(walletId) + "</b> suscess!";
            } else if (action.equals("withdraw")) {
                message = "Withdraw <i> " + StringUtils.formatCuurencyVnd(amount) + "</i> from <b>" + BotNotificationService.mapSourceSpending().get(walletId) + "</b> suscess!";
                newFormula += "-" + amount.trim();
            }
            List<List<Object>> values = Arrays.asList(Arrays.asList(newFormula));
            ValueRange body = new ValueRange().setValues(values);
            String cellUpdate = "COMMON!" + mapSourceSpendingAction().get(walletId) + ":" + mapSourceSpendingAction().get(walletId);
            UpdateValuesResponse sheetsUpdate = botNotificationService.getSheetsService().inserData(cellUpdate, body);
            if (sheetsUpdate == null || sheetsUpdate.getUpdatedRows() < 1) {
                message = "Process Error!";
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(message);
            sendMessage.setChatId(chatId);
            sendMessage.setParseMode(ParseMode.HTML);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
            inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            botNotificationService.sendTranferMessage(sendMessage);
            return true;
        }
        return false;
    }


    @BotCallBack(name = "listSpending")
    public void listSpending(BotNotificationService botNotificationService, Long chatId, Update update) {
        String message = "Choose a option to get list spending!";
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(createInlineKeyboard(BotNotificationService.mapOptionsListSpend(), null));
        inline.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "todaySpendings")
    public void getListSpendingToday(BotNotificationService botNotificationService, Long chatId, Update update) throws GeneralSecurityException, IOException {
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        List<List<Object>> listSpendingToday = getListSpending(currentDay, botNotificationService);
        StringBuilder buidler = new StringBuilder();
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        for (List<Object> row : listSpendingToday) {
           buidler.append("| <b>").append(row.get(1)).append("</b>")
                   .append("  |  <b>").append(row.get(3)).append("</b>")
                   .append("  |  <b>").append(row.get(5)).append("</b>")
                   .append("\n");
            buidler.append("|------------------------------------------\n");
            String subAmount = row.get(1).toString();
            subAmount = subAmount.replaceAll("[^\\d.,]", "");
            subAmount = subAmount.replace(".", "");
            BigDecimal bigDecimal = new BigDecimal(subAmount);
            total = total.add(bigDecimal);
        }
        buidler.append("|  Total amount: ").append("<b><i>").append(StringUtils.formatCuurencyVnd(String.valueOf(total))).append("</i></b>").append("      |");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(buidler.toString());
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(botNotificationService.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botNotificationService.sendTranferMessage(sendMessage);
    }

    private List<List<Object>> getListSpending(Integer date, BotNotificationService botNotificationService) throws GeneralSecurityException, IOException {
        SheetsService sheetsService = botNotificationService.getSheetsService();
        String rangeShet = "3/2023!A30:G200";
        ValueRange response = sheetsService.getDataSheet(rangeShet);
        List<List<Object>> resultData = new ArrayList<>();
        List<List<Object>> values = response.getValues();
        for (List<Object> rowData : values) {
            if (rowData.size() > 3) {
                if (date.toString().equals(rowData.get(0))) {
                    resultData.add(rowData);
                }
            }
        }
        return resultData;
    }

    public static Map<String, String> mapSourceSpendingAction() {
        Map<String, String> mapCellSoureSpending = new HashMap<>();
        mapCellSoureSpending.put("bidv", "D76");
        mapCellSoureSpending.put("tienmat", "D75");
        mapCellSoureSpending.put("tindung", "D77");
        mapCellSoureSpending.put("momo", "D78");
        mapCellSoureSpending.put("zalopay", "D79");
        mapCellSoureSpending.put("shoppePay", "D80");
        mapCellSoureSpending.put("vnpay", "D81");
        mapCellSoureSpending.put("timo", "D82");
        return mapCellSoureSpending;
    }


    private boolean insertSpendingDataToSheet(BotNotificationService botNotificationService, List<Object> rowData) throws GeneralSecurityException, IOException {
        SheetsService sheetsService = botNotificationService.getSheetsService();
        int indexRowNew = findRowEmpty(botNotificationService);
        String range = "3/2023!A" + indexRowNew + ":G" + indexRowNew;
        List<List<Object>> values = Arrays.asList(Arrays.asList(rowData.toArray()));
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse sheetsUpdate = sheetsService.inserData(range, body);
        if (sheetsUpdate == null || sheetsUpdate.getUpdatedRows() < 1) {
            return false;
        }
        return true;
    }


    private int findRowEmpty(BotNotificationService botNotificationService) throws GeneralSecurityException, IOException {
        SheetsService sheetsService = botNotificationService.getSheetsService();
        String cell = "3/2023!A1:A150";
        ValueRange response = sheetsService.getDataSheet(cell);
        List<List<Object>> values = response.getValues();
        if (values == null) return 1;
        return values.size() + 1;
    }

    private Map<String, String> getDataFinancialFromSheet(BotNotificationService botNotificationService) throws GeneralSecurityException, IOException {
        Map<String, String> cellAndValueMap = new HashMap<>();
        SheetsService sheetsService = botNotificationService.getSheetsService();
        String rangeShet = "COMMON!E2:E16";
        ValueRange response = sheetsService.getDataSheet(rangeShet);
        String range = rangeShet.split("!")[1];
        cellAndValueMap.put("E2", SheetUtils.getCellValue(response, range, "E2"));
        cellAndValueMap.put("E3", SheetUtils.getCellValue(response, range, "E3"));
        cellAndValueMap.put("E4", SheetUtils.getCellValue(response, range, "E4"));
        cellAndValueMap.put("E5", SheetUtils.getCellValue(response, range, "E5"));
        cellAndValueMap.put("E7", SheetUtils.getCellValue(response, range, "E7"));
        cellAndValueMap.put("E8", SheetUtils.getCellValue(response, range, "E8"));
        cellAndValueMap.put("E9", SheetUtils.getCellValue(response, range, "E9"));
        cellAndValueMap.put("E10", SheetUtils.getCellValue(response, range, "E10"));
        cellAndValueMap.put("E16", SheetUtils.getCellValue(response, range, "E16"));
        cellAndValueMap.put("E13", SheetUtils.getCellValue(response, range, "E13"));
        return cellAndValueMap;
    }


    private String buildStatisticFinancial(Map<String, String> data) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>Cash</b>                 |    ").append("<b><code>").append(data.get("E2")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>BIDV</b>                 |    ").append("<b><code>").append(data.get("E3")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>Timo</b>                 |    ").append("<b><code>").append(data.get("E4")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>Timo credit</b>     |    ").append("<b><code>").append(data.get("E5")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>Momo</b>              |    ").append("<b><code>").append(data.get("E7")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>ShopeePay</b>     |    ").append("<b><code>").append(data.get("E8")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>ZaloPay</b>            |    ").append("<b><code>").append(data.get("E9")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>VnPay</b>               |    ").append("<b><code>").append(data.get("E10")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>Total Blance</b>    |    ").append("<b><code>").append(data.get("E13")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        messageBuilder.append("| <b>Total Asset</b>      |    ").append("<b><code>").append(data.get("E16")).append("</code></b>").append("\n");
        messageBuilder.append("|------------------------------------------\n");
        return messageBuilder.toString();
    }


    public List<List<InlineKeyboardButton>> buildFunctionPersonalFinance() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        Map<String, String> callBackMap = BotNotificationService.mapCallBackButton();
        b1.setText(callBackMap.get("listSpending"));
        b1.setCallbackData("listSpending");
        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText(callBackMap.get("statistic"));
        b2.setCallbackData("statistic");
        row1.add(b1);
        row1.add(b2);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton b12 = new InlineKeyboardButton();
        InlineKeyboardButton b22 = new InlineKeyboardButton();
        b12.setText(callBackMap.get("insertspending"));
        b12.setCallbackData("insertspending");
        b22.setText(callBackMap.get("wallets"));
        b22.setCallbackData("wallets");
        row2.add(b12);
        row2.add(b22);
        keyboard.add(row1);
        keyboard.add(row2);
        return keyboard;
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


    @SneakyThrows
    public boolean handleSpendingProcess(List<Update> updates, String value, BotNotificationService botNotificationService, String chatId) {
        if (updates.get(updates.size() - 2).getCallbackQuery() != null && updates.get(updates.size() - 2).getCallbackQuery().getData().equals("insertspending")) {
            if (StringUtils.isNumberic(value)) {
                LocalDate currentDate = LocalDate.now();
                int currentDay = currentDate.getDayOfMonth();
                listDataSheet.add(currentDay);
                listDataSheet.add(Double.parseDouble(value));
                listDataSheet.add("");
                String message = buildTextForSpendingProcess(listDataSheet);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                SendMessage sendMessage = new SendMessage();
                inlineKeyboardMarkup.setKeyboard(createInlineKeyboard(BotNotificationService.mapSpendingType(), optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                botNotificationService.sendTranferMessage(sendMessage);
                return true;
            }
        } else if (updates.get(updates.size() - 3).getCallbackQuery() != null && updates.get(updates.size() - 3).getCallbackQuery().getData().equals("insertspending")) {
            String dataCallBack = updates.get(updates.size() - 1).getCallbackQuery().getData();
            if (listDataSheet.size() == 3 && BotNotificationService.mapSpendingType().containsKey(dataCallBack)) {
                listDataSheet.add(BotNotificationService.mapSpendingType().get(dataCallBack));
                listDataSheet.add("");
                String message = buildTextForSpendingProcess(listDataSheet);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                SendMessage sendMessage = new SendMessage();
                inlineKeyboardMarkup.setKeyboard(createInlineKeyboard(BotNotificationService.mapSourceSpending(), optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                botNotificationService.sendTranferMessage(sendMessage);
                return true;
            }
        } else if (updates.get(updates.size() - 4).getCallbackQuery() != null && updates.get(updates.size() - 4).getCallbackQuery().getData().equals("insertspending")) {
            String dataCallBack = updates.get(updates.size() - 1).getCallbackQuery().getData();
            if (listDataSheet.size() == 5 && BotNotificationService.mapSourceSpending().containsKey(dataCallBack)) {
                listDataSheet.add(BotNotificationService.mapSourceSpending().get(dataCallBack));
                String message = buildTextForSpendingProcess(listDataSheet);
                Map<String, String> preSubmitButton = new HashMap<>();
                preSubmitButton.put("addNote", "Ghi chú");
                preSubmitButton.put("submitSpending", "Submit");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                SendMessage sendMessage = new SendMessage();
                inlineKeyboardMarkup.setKeyboard(createInlineKeyboard(preSubmitButton, optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                botNotificationService.sendTranferMessage(sendMessage);
                return true;
            }
        } else if (updates.get(updates.size() - 6).getCallbackQuery() != null && updates.get(updates.size() - 6).getCallbackQuery().getData().equals("insertspending")) {
            if (listDataSheet.size() == 6 && !value.isEmpty()) {
                listDataSheet.add(value);
                String message = buildTextForSpendingProcess(listDataSheet);
                Map<String, String> preSubmitButton = new HashMap<>();
                preSubmitButton.put("submitSpending", "Submit");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                SendMessage sendMessage = new SendMessage();
                inlineKeyboardMarkup.setKeyboard(createInlineKeyboard(preSubmitButton, optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                botNotificationService.sendTranferMessage(sendMessage);
                return true;
            }
        }
        return false;
    }

    private String buildTextForSpendingProcess(List<Object> listData) {
        StringBuilder stringBuilder = new StringBuilder();
        if (listData.size() > 1) {
            stringBuilder.append("Số tiền chi tiêu: ").append("<b><i>").append(StringUtils.formatCuurencyVnd(listDataSheet.get(1).toString())).append("</i></b>");
        }
        if (listData.size() > 3) {
            stringBuilder.append(" được dùng cho ").append("<b>").append(listDataSheet.get(3).toString().toLowerCase(Locale.ROOT)).append("</b>");
        }
        if (listData.size() > 5) {
            stringBuilder.append(" với nguồn tiền từ ").append("<b>").append(listDataSheet.get(5)).append("</b>").append("\n");
        }
        if (listData.size() > 6) {
            stringBuilder.append("Note: ").append("<b>").append(listDataSheet.get(6)).append("</b>");
        }
        return stringBuilder.toString();
    }


}
