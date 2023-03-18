package net.etaservice.comon.utilservice.telegram.route;


import com.google.api.services.tasks.model.Task;
import net.etaservice.comon.Constant;
import net.etaservice.comon.DateUtils;
import net.etaservice.comon.domain.task.TaskListInfo;
import net.etaservice.comon.googletask.TaskService;
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

import java.text.SimpleDateFormat;
import java.util.*;

@BotRoute
@Component
public class TaskRoute {

    @Autowired
    private TaskService taskService;


    @Autowired
    private BotNotificationServiceCommon botServiceCommon;


//    @Scheduled(cron = "0 30 8 * * ?")
     @Scheduled(initialDelay = 1000, fixedDelay=Long.MAX_VALUE)
    public void scheduleRemindTaskCommon(){
        Map<String,String> map = new HashMap<>();
        List<TaskListInfo> listTaskListInfo = taskService.getListTaskInfo();
        SendMessage sendMessage = new SendMessage();
        StringBuilder str = new StringBuilder();
        listTaskListInfo.forEach(tl -> {
                    if (tl.getIsCommon() == 1) {
                        List<Task> taskList = taskService.getListTaskByTaskListId(tl.getTaskListId());
                        str.append("|-------<b>").append(tl.getTaskListName()).append("</b>-------\n");
                        for (Task t : taskList) {
                            str.append("|------------------------------------------\n");
                            str.append("|  Title:     <b>").append(t.getTitle()).append("</b>").append("\n");
                            if (t.getNotes() != null && !t.getNotes().equals("")) {
                                str.append("|  Notes:   <i>").append(t.getNotes()).append("</i>\n");
                            }
                            if (t.getUpdated() != null && !t.getUpdated().equals("")) {
                                str.append("|  Created: <i>").append(DateUtils.formatDateForTask(t.getUpdated())).append("</i>\n");
                            }
                        }
                        str.append("|------------------------------------------\n");
                    }
                });
            sendMessage.setText(str.toString());
            sendMessage.setChatId(Constant.CHAT_ID_BOSS);
            sendMessage.setParseMode(ParseMode.HTML);
            botServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "newtask")
    public void newTaskGoogle(BotNotificationServiceCommon serviceCommon, Long chatId, Update update) {
        List<TaskListInfo> listTaskListInfo = taskService.getListTaskInfo();
        String message = "Insert Failed!";
        String conntentTask = "";
        String taskListIdInsert = "";
        String taskListName = "";
        boolean ishandlerComand = true;
        if (update.hasMessage()) {
            String mesage = update.getMessage().getText();
            if (mesage.contains("newtask")) {
                String command = mesage.trim().split(" ")[0].replace("/","");
                String taskListCode = command.trim().split("-").length > 1 ? command.trim().split("-")[1] : "";
                Optional<TaskListInfo> taskListInfoSelect = listTaskListInfo.stream().filter(t ->  t.getTaskListCode() != null && t.getTaskListCode().equalsIgnoreCase(taskListCode)).findFirst();
                if (taskListInfoSelect.isPresent()) {
                    taskListIdInsert = taskListInfoSelect.get().getTaskListId();
                    taskListName = taskListInfoSelect.get().getTaskListName();
                } else {
                    Optional<TaskListInfo> taskListInfoCommon = listTaskListInfo.stream().filter(t -> t.getIsCommon() == 1).findFirst();
                    if (taskListInfoCommon.isPresent()) {
                        taskListIdInsert = taskListInfoCommon.get().getTaskListId();
                        taskListName = taskListInfoCommon.get().getTaskListName();
                    } else {
                        message = "Not found task list Common, please insert common task list";
                    }
                }
               conntentTask = mesage.trim().split(" ",2)[1];
            } else {
                ishandlerComand = false;
                List<Update> updateListHistory =  BotNotificationServiceCommon.updates.getListElement();
                Update updateCall =updateListHistory.get(updateListHistory.size() - 2);
                String callBack = updateCall.getCallbackQuery().getData();
                taskListIdInsert = callBack.trim().split(":")[1];
                String finalTaskListIdInsert = taskListIdInsert;
                TaskListInfo taskListInfo = listTaskListInfo.parallelStream().filter(t -> t.getTaskListId() != null && t.getTaskListId().equals(finalTaskListIdInsert)).findFirst().orElse(null);
                assert taskListInfo != null;
                taskListName = taskListInfo.getTaskListName();
                conntentTask = mesage;
            }
            Task task = new Task();
            if (conntentTask.split(":").length > 1) {
                task.setNotes(conntentTask.split(":")[1]);
                task.setTitle(conntentTask.split(":")[0]);
            } else {
                task.setTitle(conntentTask);
            }
            String dueTask = DateUtils.initDueTask(1,8);
            task.setDue(dueTask);
            Task taskInserted = taskService.insert(task,taskListIdInsert);
            if (taskInserted != null){
                message = "Insert new Task to <b> "+ taskListName +" </b>"  + "success!";
            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        if (!ishandlerComand){
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            Map<String,String> stringMap = new HashMap<>(BotNotificationServiceCommon.mapActionTaskCallBack);
            stringMap.remove("newTaskStep2");
            inlineKeyboardMarkup.setKeyboard(serviceCommon.createInlineKeyboard(stringMap,null));
            inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        botServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "workTask")
    public void handleWorkTask(BotNotificationServiceCommon serviceCommon, Long chatId, Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose action task: ");
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        Map<String,String> stringMap = new HashMap<>(BotNotificationServiceCommon.mapActionTaskCallBack);
        stringMap.remove("newTaskStep2");
        inlineKeyboardMarkup.setKeyboard(serviceCommon.createInlineKeyboard(stringMap,null));
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        serviceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "newTaskStep1")
    public void newTaskStep1(BotNotificationServiceCommon serviceCommon, Long chatId, Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose Task List Want to insert task: ");
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        List<TaskListInfo> listTaskListInfo = taskService.getListTaskInfo();
        Map<String,String> taskListMap = new HashMap<>();
        listTaskListInfo.forEach(t -> {
            taskListMap.put("newTaskStep2:"+t.getTaskListId(),t.getTaskListName());
        });
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(botServiceCommon.createInlineKeyboard(taskListMap,null));
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "newTaskStep2")
    public void newTaskStep2(BotNotificationServiceCommon serviceCommon, Long chatId, Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Enter task title like command: <task title> : <note>");
        sendMessage.setChatId(chatId);
        botServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "listTaskList")
    public void getListTaskList(BotNotificationServiceCommon serviceCommon, Long chatId, Update update){
        Map<String,String> map = new HashMap<>();
        List<TaskListInfo> listTaskListInfo = taskService.getListTaskInfo();
        listTaskListInfo.forEach(t ->{
            map.put("getTaskinList:" + t.getTaskListId(), t.getTaskListName());
        });
        SendMessage sendMessage = new SendMessage();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(botServiceCommon.createInlineKeyboard(map,null));
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Choose TaskList want to get Task in list");
        botServiceCommon.sendTranferMessage(sendMessage);
    }


    @BotCallBack(name = "getTaskinList")
    public void getTaskInListTaskList(BotNotificationServiceCommon serviceCommon, Long chatId, Update update){
        List<Update> updateListHistory =  BotNotificationServiceCommon.updates.getListElement();
        String callBack = update.getCallbackQuery().getData();
        String taskListId = callBack.trim().split(":")[1];
        List<Task> taskList = taskService.getListTaskByTaskListId(taskListId);
        SendMessage sendMessage = new SendMessage();
        StringBuilder str = new StringBuilder();
        for (Task t : taskList){
            str.append("|------------------------------------------\n");
            str.append("|  Title:     <b>").append(t.getTitle()).append("</b>").append("\n");
            if (t.getNotes() != null && !t.getNotes().equals("")){
            str.append("|  Notes:   <i>").append(t.getNotes()).append("</i>\n");
            }
            if (t.getUpdated() != null && !t.getUpdated().equals("")){
                str.append("|  Created: <i>").append(DateUtils.formatDateForTask(t.getUpdated())).append("</i>\n");
            }
        }
        str.append("|------------------------------------------\n");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
         Map<String,String> stringMap = new HashMap<>(BotNotificationServiceCommon.mapActionTaskCallBack);
        stringMap.remove("newTaskStep2");
        inlineKeyboardMarkup.setKeyboard(serviceCommon.createInlineKeyboard(stringMap,null));
        inlineKeyboardMarkup.getKeyboard().addAll(BotNotificationServiceCommon.buildCommonButton());
        sendMessage.setText(str.toString());
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        botServiceCommon.sendTranferMessage(sendMessage);
    }

    @BotCallBack(name = "tasklistmap")
    public void getTaskListMapcode(BotNotificationServiceCommon serviceCommon, Long chatId, Update update){
        Map<String,String> map = new HashMap<>();
        List<TaskListInfo> listTaskListInfo = taskService.getListTaskInfo();
        StringBuilder str = new StringBuilder();
        listTaskListInfo.forEach(t ->{
                str.append("|------------------------------------------\n");
                str.append("|    <b>").append(t.getTaskListName()).append("</b>")
                        .append("    >>>>>   <b>").append(t.getTaskListCode() != null ? t.getTaskListCode() : "").append("</b>\n");
        });
        str.append("|------------------------------------------\n");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(str.toString());
        botServiceCommon.sendTranferMessage(sendMessage);
    }



}
