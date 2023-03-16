package net.etaservice.comon.utilservice.telegram.route;


import com.google.api.services.tasks.model.Task;
import net.etaservice.comon.domain.task.TaskListInfo;
import net.etaservice.comon.googletask.TaskService;
import net.etaservice.comon.utilservice.telegram.BotNotificationServiceCommon;
import net.etaservice.comon.utilservice.telegram.customanotation.BotCallBack;
import net.etaservice.comon.utilservice.telegram.customanotation.BotRoute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@BotRoute
@Component
public class TaskRoute {

    @Autowired
    private TaskService taskService;


    @Autowired
    private BotNotificationServiceCommon botServiceCommon;

    @BotCallBack(name = "newtask")
    public void newTaskGoogle(BotNotificationServiceCommon serviceCommon, Long chatId, Update update) {
        List<TaskListInfo> listTaskListInfo = taskService.getListTaskInfo();
        String message = "Insert Failed!";
        if (update.hasMessage()) {
            String mesage = update.getMessage().getText();
            if (mesage.contains("newtask")) {
                String taskListIdInsert = "";
                String taskListName = "";
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
                String conntentTask = mesage.trim().split(" ",2)[1];
                Task task = new Task();
                if (conntentTask.split(":").length > 1) {
                    task.setNotes(conntentTask.split(":")[1]);
                    task.setTitle(conntentTask.split(":")[0]);
                } else {
                    task.setTitle(conntentTask);
                }
                Task taskInserted = taskService.insert(task,taskListIdInsert);
                if (taskInserted != null){
                    message = "Insert new Task to <b>" + taskListName + " </b>"  + "success!";
                }
            }
        } else if (update.hasCallbackQuery()) {

        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        botServiceCommon.sendTranferMessage(sendMessage);
    }

}
