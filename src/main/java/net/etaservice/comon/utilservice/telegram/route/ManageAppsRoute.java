package net.etaservice.comon.utilservice.telegram.route;

import net.etaservice.airdrop.AirdropService;
import net.etaservice.airdrop.model.Wallet;
import net.etaservice.appmanager.AppInfoService;
import net.etaservice.appmanager.model.AppInfo;
import net.etaservice.comon.utilservice.telegram.BotNotificationServiceCommon;
import net.etaservice.comon.utilservice.telegram.customanotation.BotCallBack;
import net.etaservice.comon.utilservice.telegram.customanotation.BotRoute;
import net.etaservice.configapp.metric.IApiMetrics;
import net.etaservice.configapp.metric.service.IApiMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@BotRoute
@Component
public class ManageAppsRoute {

    @Autowired
    private BotNotificationServiceCommon notifiCommon;

    @Autowired
    private IApiMetricsService apiMetricsService;

    @Autowired
    private IApiMetrics apiMetrics;

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private AirdropService airdropService;

    @BotCallBack(name = "appList")
    public void handlerAppList(BotNotificationServiceCommon botNotificationServiceCommon, Long chatId, Update updateParam)  {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Functions to manager Apps:");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        Map<String,String> map = new HashMap<>();
        for (var entry : BotNotificationServiceCommon.mapCallBackManaApps.entrySet()){
            for (var en : BotNotificationServiceCommon.mapCallBackManaAppsAction.entrySet()){
                map.put(en.getKey() + ":" + entry.getKey(), entry.getValue());
            }
        }
        inlineKeyboardMarkup.setKeyboard(notifiCommon.createInlineKeyboard(map, null));
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        notifiCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "countRequest")
    public void hanldeCoutRequestApp(BotNotificationServiceCommon botNotificationServiceCommon, Long chatId, Update updateParam){
        String appId = updateParam.getCallbackQuery().getData().split(":")[1];
        Map<String,String> mapApp = BotNotificationServiceCommon.mapCallBackManaApps;
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        String apiName = "";
        if (appId.equals("mapparam")){
            apiName = "api/v1/free/app/ping/MAP";
            configAppMaparam(chatId);
            return;
        } else if (appId.equals("newsDay")){
            apiName = "api/v1/free/app/news/last";
        } else if (appId.equals("newsDay")){

        }
        String message = "Total request count of <b>" + mapApp.get(appId) + " </b> : <b> " +  apiMetrics.countApiMertrics(apiName) + "</b>";
        inline.setKeyboard(BotNotificationServiceCommon.buildCommonButton());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        notifiCommon.sendTranferMessage(sendMessage);
    }

    public void configAppMaparam(Long chatId){
        Map<String,String> mapApp = BotNotificationServiceCommon.mapCallBackManaApps;
        InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
        inline.setKeyboard(notifiCommon.createInlineKeyboard(BotNotificationServiceCommon.mapFunctionAppMapparam, null));
        inline.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(inline);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Choose function config App Mappram");
        sendMessage.setParseMode(ParseMode.HTML);
        notifiCommon.sendTranferMessage(sendMessage);
    }



    @BotCallBack(name = "changeNotifyMaparam")
    public void changeNotifyMaparam(BotNotificationServiceCommon botNotificationServiceCommon, Long chatId, Update updateParam){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Enter new notify for app Mappram:");
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        notifiCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "wallet")
    public void getWalletinfo(BotNotificationServiceCommon botNotificationServiceCommon, Long chatId, Update updateParam){
        Long walletId = handleMessageGetWalletId(updateParam);
        Wallet wallet = airdropService.getWalletInfoFromAirmon(walletId);
        String messResponse;
        if (Objects.isNull(wallet)){
            messResponse = "<i>Wallet Not Found!</i>";
        } else {
            messResponse = buildMessageWalletInfo(wallet);
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messResponse);
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        notifiCommon.sendTranferMessage(sendMessage);
    }

    private String buildMessageWalletInfo(Wallet wallet) {
        StringBuilder builder = new StringBuilder();
        builder.append("<b>|------------Wallet Info ID[").append(wallet.getId()).append("] ----------|</b>\n");
        builder.append("<b>Name:  </b>").append(wallet.getAccountName()).append("\n");
        builder.append("<b>Profile:  </b>").append(wallet.getProfile().getName()).append("\n");
        builder.append("<b>Address:  </b>[").append("<code>").append(wallet.getPublicKey()).append("</code>").append("]\n");
        builder.append("<b>Key:  </b>[").append("<code>").append(wallet.getPrivateKey()).append("</code>").append("]\n");
        builder.append("<b>Chain:  </b>").append(wallet.getChain()).append("\n");
        builder.append("<b>Type:  </b>").append(wallet.getType()).append("\n");
        builder.append("<b>Note:  </b>").append(wallet.getNote()).append("\n");
        return builder.toString();
    }


    public boolean handleUpdateNotifyMappram(List<Update> updates, String value, BotNotificationServiceCommon notiServiceCommon, String chatId){
        if (updates.get(updates.size() - 2).getCallbackQuery() != null && updates.get(updates.size() - 2).getCallbackQuery().getData().equals("changeNotifyMaparam")){
            AppInfo appInfo = appInfoService.getAppInfoByCode("MAP");
            appInfo.setLastNotify(value);
            appInfoService.saveAppInfo(appInfo);
            InlineKeyboardMarkup inline = new InlineKeyboardMarkup();
            inline.setKeyboard(BotNotificationServiceCommon.buildCommonButton());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Updated Notify for app Mapparam!");
            sendMessage.setChatId(chatId);
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(inline);
            notifiCommon.sendTranferMessage(sendMessage);
            return true;
        }
        return false;
    }

    private Long handleMessageGetWalletId(Update update){
      String mess = update.getMessage().getText();
      mess = mess.replace("/wallet","").trim();
      return Long.valueOf(mess);
    }


}
