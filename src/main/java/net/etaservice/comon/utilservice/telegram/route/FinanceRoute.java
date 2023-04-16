package net.etaservice.comon.utilservice.telegram.route;

import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.comon.Constant;
import net.etaservice.comon.DateUtils;
import net.etaservice.comon.StringUtils;
import net.etaservice.comon.googlesheet.SheetUtils;
import net.etaservice.comon.utilservice.telegram.BotNotificationServiceCommon;
import net.etaservice.comon.utilservice.telegram.customanotation.BotCallBack;
import net.etaservice.comon.utilservice.telegram.customanotation.BotRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

@BotRoute
@Component
@Slf4j
public class FinanceRoute {

    @Autowired
    private BotNotificationServiceCommon serviceCommon;

    private static List<Object> listDataSheet = new ArrayList<>();

    private static Map<String, String> optionMapButtonFinance() {
        Map<String, String> map = new HashMap<>();
        map.put("cancelSpending", "Cancel");
        return map;
    }

    @Scheduled(cron = "0 10 22 * * ?")
    public void reportFinanceEveryDay(){
        // Run at 22h10 every day
        log.info("*****reportFinanceEveryDay******");
        buildReportFinance();
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 12 ? * SUN")
    public void reportFinanceEveryWeek(){
        // Run at 12h every sunday
        log.info("*****reportFinanceEveryDay******");
       buildReportFinanceOneWeek();
    }


    @Scheduled(cron = "0 0 14 * * ?")
    public void remindInsertSpending14h(){
        remindInsertSpend();
    }

    @Scheduled(cron = "0 0 20 * * ?")
    public void remindInsertSpending20h(){
        remindInsertSpend();
    }


    @BotCallBack(name = "personalFinance")
    public void handlerManagerPeronalFinance(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Functions to get balance, statistics for your personal financial management:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "wallets")
    public void getCurrBalance(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setText("List Your Wallet Existing, Choose wallet want to manage");
        Map<String, String> mapManaWallet = new HashMap<>();
        for (var entry : BotNotificationServiceCommon.mapSourceSpending().entrySet()) {
            mapManaWallet.put("manaWallet:" + entry.getKey(), entry.getValue());
        }
        inlineKeyboardMarkup.setKeyboard(notiServiceCommon.createInlineKeyboard(mapManaWallet, null));
        inlineKeyboardMarkup.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "statistic")
    public void statisticTotalBalance(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        Map<String, String> mapDataResult = getDataFinancialFromSheet(notiServiceCommon);
        String buildTable = buildStatisticFinancial(mapDataResult);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText(buildTable);
        sendMessage.setParseMode("html");
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "insertspending")
    public void insertNewSpending(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String cuurDate = currentDate.format(formatter);
        sendMessage.setText("Bắt đầu nhập khoản chi tiêu, hôm nay là ngày " + cuurDate);

        listDataSheet.clear();
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "addNote")
    public void addNote(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) {
        List<Update> updates = notiServiceCommon.updates.getListElement();
        if (updates.get(updates.size() - 5).getCallbackQuery() != null && updates.get(updates.size() - 5).getCallbackQuery().getData().equals("insertspending")) {
            if (listDataSheet.size() == 6) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Hãy điền ghi chú cho khoản chi tiêu này:");
                notiServiceCommon.sendTranferMessage(sendMessage);
            }
        }

    }

    @BotCallBack(name = "submitSpending")
    public void submitSpending(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) throws GeneralSecurityException, IOException {
        if (listDataSheet.size() > 5) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            boolean isComplete = insertSpendingDataToSheet(notiServiceCommon, listDataSheet);
            String message = "Submit spending suscess!";
            if (!isComplete) {
                message = "Submit spending error!, please try again!";
            }
            sendMessage.setText(message);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
            inlineKeyboardMarkup.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            notiServiceCommon.sendTranferMessage(sendMessage);
        }
    }

    @BotCallBack(name = "cancelSpending")
    public void cancelSpending(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update updateParam) {
        String message = "Cancel spending suscess!";
        listDataSheet.clear();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "manaWallet")
    public void manageWallet(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update update) {
        String walletId = update.getCallbackQuery().getData().split(":")[1];
        String message = "You are choosing <b>" + BotNotificationServiceCommon.mapSourceSpending().get(walletId) + "</b> please select actions with your wallet";
        SendMessage sendMessage = new SendMessage();
        Map<String, String> mapWalletAction = new HashMap<>();
        for (var entry : BotNotificationServiceCommon.mapSourceSpendingAction().entrySet()) {
            mapWalletAction.put(entry.getKey() + ":" + walletId, entry.getValue());
        }
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(notiServiceCommon.createInlineKeyboard(mapWalletAction, null));
        inline.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "deposit")
    public void deppositToWallet(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update update) {
        String walletId = update.getCallbackQuery().getData().split(":")[1];
        String message = "Enter amout you want deposit to <b>" + BotNotificationServiceCommon.mapSourceSpending().get(walletId) + "</b>";
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(BotNotificationServiceCommon.buildCommonButton());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }


    @BotCallBack(name = "withdraw")
    public void withdrawFromWallet(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update update) {
        String walletId = update.getCallbackQuery().getData().split(":")[1];
        String message = "Enter amout you want withdraw from <b>" + notiServiceCommon.mapSourceSpending().get(walletId) + "</b>";
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(notiServiceCommon.buildCommonButton());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    public boolean handleActionSourceSpendingProcess(List<Update> updateList, String amount, BotNotificationServiceCommon notiServiceCommon, String chatId) throws GeneralSecurityException, IOException, ScriptException {
        if (updateList.get(updateList.size() - 2).getCallbackQuery() != null && StringUtils.isNumberic(amount)) {
            String callBackData = updateList.get(updateList.size() - 2).getCallbackQuery().getData();
            String action = callBackData.split(":")[0];
            String rangeSheet = "COMMON!D75:D82";
            String range = rangeSheet.split("!")[1];
            ValueRange response = notiServiceCommon.getSheetsService().getDataSheetWithFormula(rangeSheet);
            String walletId = callBackData.split(":")[1];
            String formula = SheetUtils.getCellValue(response, range, mapSourceSpendingAction().get(walletId));
            String newFormula = formula;
            String message = "";
            if (action.equals("deposit")) {
                newFormula += "+" + amount.trim();
                message = "Deposit <i>" + StringUtils.formatCuurencyVnd(amount) + "</i> to <b>" + BotNotificationServiceCommon.mapSourceSpending().get(walletId) + "</b> suscess!";
            } else if (action.equals("withdraw")) {
                message = "Withdraw <i> " + StringUtils.formatCuurencyVnd(amount) + "</i> from <b>" + BotNotificationServiceCommon.mapSourceSpending().get(walletId) + "</b> suscess!";
                newFormula += "-" + amount.trim();
            }
            List<List<Object>> values = Arrays.asList(Arrays.asList(newFormula));
            ValueRange body = new ValueRange().setValues(values);
            String cellUpdate = "COMMON!" + mapSourceSpendingAction().get(walletId) + ":" + mapSourceSpendingAction().get(walletId);
            UpdateValuesResponse sheetsUpdate = notiServiceCommon.getSheetsService().inserData(cellUpdate, body);
            if (sheetsUpdate == null || sheetsUpdate.getUpdatedRows() < 1) {
                message = "Process Error!";
            }
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(message);
            sendMessage.setChatId(chatId);
            sendMessage.setParseMode(ParseMode.HTML);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
            inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            notiServiceCommon.sendTranferMessage(sendMessage);
            return true;
        }
        return false;
    }


    @BotCallBack(name = "listSpending")
    public void listSpending(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update update) {
        String message = "Choose a option to get list spending!";
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(notiServiceCommon.createInlineKeyboard(notiServiceCommon.mapOptionsListSpend(), null));
        inline.getKeyboard().addAll(notiServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "todaySpendings")
    public void getListSpendingToday(BotNotificationServiceCommon notiServiceCommon, Long chatId, Update update) throws GeneralSecurityException, IOException {
        LocalDate currentDate = LocalDate.now();
        int currentDay = currentDate.getDayOfMonth();
        List<List<Object>> listSpendingToday = getListSpending(currentDay,currentDay, notiServiceCommon);
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
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        notiServiceCommon.sendTranferMessage(sendMessage);
    }

    @SneakyThrows
    private void buildReportFinance(){
        Date date = new Date();
        int currentDay =  LocalDate.now().getDayOfMonth();
        List<List<Object>> listSpendingToday = getListSpending(currentDay,currentDay, serviceCommon);
        StringBuilder buidler = new StringBuilder();
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        buidler.append("|----REPORT_SPENDING_").append(DateUtils.formatDate(date, DateUtils.FORMAT_DD_MM_YY)).append("----\n");
        for (List<Object> row : listSpendingToday) {
            buidler.append("| Amount:     <b>").append(row.get(1)).append("</b>\n");
            buidler.append("| Type:          <b>").append(row.get(3)).append("</b>\n");
            buidler.append("| Soure:        <b>").append(row.get(5)).append("</b>\n");
            if (row.size() > 6 && row.get(6) != null){
                buidler.append("| Note:           <b>").append(row.get(6)).append("</b>\n");
            }
            buidler.append("|------------------------------------------\n");

            String subAmount = row.get(1).toString();
            subAmount = subAmount.replaceAll("[^\\d.,]", "");
            subAmount = subAmount.replace(".", "");
            BigDecimal bigDecimal = new BigDecimal(subAmount);
            total = total.add(bigDecimal);
        }
        buidler.append("| Total spending today: ").append("<i>").append(StringUtils.formatCuurencyVnd(String.valueOf(total))).append("</i>").append("      |");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(buidler.toString());
        sendMessage.setChatId(Constant.CHAT_ID_BOSS);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        serviceCommon.sendTranferMessage(sendMessage);
    }

    @SneakyThrows
    private void buildReportFinanceOneWeek(){
        Date date = new Date();
         LocalDate now = LocalDate.now();
        int currentDay = now.getDayOfMonth();
        LocalDate oneWeekAgo = now.minusWeeks(1);
        int dayOneWeekAgo = oneWeekAgo.getDayOfMonth();
        List<List<Object>> listSpendingThisWeek  = getListSpending(dayOneWeekAgo,currentDay,serviceCommon);
        StringBuilder buidler = new StringBuilder();
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        buidler.append("|----REPORT_SPENDING_THIS_WEEK").append("----\n");
        for (int i = 0; i < listSpendingThisWeek.size(); i++) {
            List<Object> row  = listSpendingThisWeek.get(i);
            if (i > 0){
                if (!row.get(0).toString().equals(listSpendingThisWeek.get(i-1))){
                    buidler.append("==Day: ").append(row.get(0)).append("==\n");
                }
            } else{
                buidler.append("==Day: ").append(row.get(0)).append("==\n");
            }
            buidler.append("| Amount:     <b>").append(row.get(1)).append("</b>\n");
            buidler.append("| Type:          <b>").append(row.get(3)).append("</b>\n");
            buidler.append("| Soure:        <b>").append(row.get(5)).append("</b>\n");
            if (row.size() > 6 && row.get(6) != null){
                buidler.append("| Note:           <b>").append(row.get(6)).append("</b>\n");
            }
            buidler.append("|------------------------------------------\n");

            String subAmount = row.get(1).toString();
            subAmount = subAmount.replaceAll("[^\\d.,]", "");
            subAmount = subAmount.replace(".", "");
            BigDecimal bigDecimal = new BigDecimal(subAmount);
            total = total.add(bigDecimal);
        }
        buidler.append("| Week spending today: ").append("<i>").append(StringUtils.formatCuurencyVnd(String.valueOf(total))).append("</i>").append("      |");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(buidler.toString());
        sendMessage.setChatId(Constant.CHAT_ID_BOSS);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buildFunctionPersonalFinance());
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        serviceCommon.sendTranferMessage(sendMessage);
    }


    private List<List<Object>> getListSpending(Integer fromDate,Integer toDate, BotNotificationServiceCommon notiServiceCommon) throws GeneralSecurityException, IOException {
        String rangeShet = "3/2023!A109:G222";
        ValueRange response = notiServiceCommon.getSheetsService().getDataSheet(rangeShet);
        List<List<Object>> resultData = new ArrayList<>();
        List<List<Object>> values = response.getValues();
        if (toDate == null){
            toDate = fromDate;
        }
            for (List<Object> rowData : values) {
            if (rowData.size() > 3) {
                if (StringUtils.isNumberic((String) rowData.get(0))){
                    Integer rowDate = Integer.valueOf(rowData.get(0).toString()) ;
                    if (fromDate <= rowDate && rowDate <= toDate) {
                        resultData.add(rowData);
                    }
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


    private boolean insertSpendingDataToSheet(BotNotificationServiceCommon notiServiceCommon, List<Object> rowData) throws GeneralSecurityException, IOException {
        int indexRowNew = findRowEmpty(notiServiceCommon);
        String range = "3/2023!A" + indexRowNew + ":G" + indexRowNew;
        List<List<Object>> values = Arrays.asList(Arrays.asList(rowData.toArray()));
        ValueRange body = new ValueRange().setValues(values);
        UpdateValuesResponse sheetsUpdate = notiServiceCommon.getSheetsService().inserData(range, body);
        if (sheetsUpdate == null || sheetsUpdate.getUpdatedRows() < 1) {
            return false;
        }
        return true;
    }


    private int findRowEmpty(BotNotificationServiceCommon notiServiceCommon) throws GeneralSecurityException, IOException {
        String cell = "3/2023!A1:A500";
        ValueRange response = notiServiceCommon.getSheetsService().getDataSheet(cell);
        List<List<Object>> values = response.getValues();
        if (values == null) return 1;
        return values.size() + 1;
    }

    private Map<String, String> getDataFinancialFromSheet(BotNotificationServiceCommon notiServiceCommon) throws GeneralSecurityException, IOException {
        Map<String, String> cellAndValueMap = new HashMap<>();
        String rangeShet = "COMMON!E2:E16";
        ValueRange response = notiServiceCommon.getSheetsService().getDataSheet(rangeShet);
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
        Map<String, String> callBackMap = BotNotificationServiceCommon.mapCallBackFinance;
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


   


    @SneakyThrows
    public boolean handleSpendingProcess(List<Update> updates, String value, BotNotificationServiceCommon notiServiceCommon, String chatId) {
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
                inlineKeyboardMarkup.setKeyboard(notiServiceCommon.createInlineKeyboard(BotNotificationServiceCommon.mapSpendingType(), optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                notiServiceCommon.sendTranferMessage(sendMessage);
                return true;
            }
        } else if (updates.get(updates.size() - 3).getCallbackQuery() != null && updates.get(updates.size() - 3).getCallbackQuery().getData().equals("insertspending")) {
            String dataCallBack = updates.get(updates.size() - 1).getCallbackQuery().getData();
            if (listDataSheet.size() == 3 && BotNotificationServiceCommon.mapSpendingType().containsKey(dataCallBack)) {
                listDataSheet.add(BotNotificationServiceCommon.mapSpendingType().get(dataCallBack));
                listDataSheet.add("");
                String message = buildTextForSpendingProcess(listDataSheet);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                SendMessage sendMessage = new SendMessage();
                inlineKeyboardMarkup.setKeyboard(notiServiceCommon.createInlineKeyboard(BotNotificationServiceCommon.mapSourceSpending(), optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                notiServiceCommon.sendTranferMessage(sendMessage);
                return true;
            }
        } else if (updates.get(updates.size() - 4).getCallbackQuery() != null && updates.get(updates.size() - 4).getCallbackQuery().getData().equals("insertspending")) {
            String dataCallBack = updates.get(updates.size() - 1).getCallbackQuery().getData();
            if (listDataSheet.size() == 5 && BotNotificationServiceCommon.mapSourceSpending().containsKey(dataCallBack)) {
                listDataSheet.add(BotNotificationServiceCommon.mapSourceSpending().get(dataCallBack));
                String message = buildTextForSpendingProcess(listDataSheet);
                Map<String, String> preSubmitButton = new HashMap<>();
                preSubmitButton.put("addNote", "Ghi chú");
                preSubmitButton.put("submitSpending", "Submit");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                SendMessage sendMessage = new SendMessage();
                inlineKeyboardMarkup.setKeyboard(notiServiceCommon.createInlineKeyboard(preSubmitButton, optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                notiServiceCommon.sendTranferMessage(sendMessage);
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
                inlineKeyboardMarkup.setKeyboard(notiServiceCommon.createInlineKeyboard(preSubmitButton, optionMapButtonFinance()));
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setChatId(chatId);
                sendMessage.setText(message);
                sendMessage.setParseMode(ParseMode.HTML);
                notiServiceCommon.sendTranferMessage(sendMessage);
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

    private void remindInsertSpend(){
        Map<String, String> preSubmitButton = new HashMap<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        SendMessage sendMessage = new SendMessage();
        Map<String, String> callBackMap = BotNotificationServiceCommon.mapCallBackFinance;
        preSubmitButton.put("insertspending",callBackMap.get("insertspending"));
        inlineKeyboardMarkup.setKeyboard(serviceCommon.createInlineKeyboard(preSubmitButton, null));
        inlineKeyboardMarkup.getKeyboard().addAll(serviceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(Constant.CHAT_ID_BOSS);
        sendMessage.setText("<b>Thời gian qua bạn có chi tiêu gì không?, hãy thêm chi tiêu bạn bạn</b>");
        sendMessage.setParseMode(ParseMode.HTML);
        serviceCommon.sendTranferMessage(sendMessage);
    }

}
