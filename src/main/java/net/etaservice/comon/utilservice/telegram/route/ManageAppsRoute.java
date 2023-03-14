package net.etaservice.comon.utilservice.telegram.route;

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
import java.util.Map;

@BotRoute
@Component
public class ManageAppsRoute {

    @Autowired
    private BotNotificationServiceCommon notifiCommon;

    @Autowired
    private IApiMetricsService apiMetricsService;

    @Autowired
    private IApiMetrics apiMetrics;

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
}
