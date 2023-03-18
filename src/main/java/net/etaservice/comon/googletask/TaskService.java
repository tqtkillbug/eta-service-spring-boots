package net.etaservice.comon.googletask;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.comon.domain.task.TaskListInfo;
import net.etaservice.comon.google.GoogleCendentials;
import net.etaservice.comon.googlesheet.ISheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class TaskService {
    @Autowired
    private GoogleCendentials googleCendentials;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    @SneakyThrows
    public Tasks taskSerive(){
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = googleCendentials.getCredentials();
        return new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName("TASKMANAGER")
                .build();
    }

    public List<TaskList> getListTaskList(){
        try {
            TaskLists result = taskSerive().tasklists().list()
                    .setMaxResults(10)
                    .execute();
            List<TaskList> taskLists = result.getItems();
            return taskLists;
        } catch (IOException e){
            e.printStackTrace();
            log.error("GG_TASK-ERROR: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<TaskListInfo> getListTaskInfo(){
        List<TaskListInfo> taskListInfos = new ArrayList<>();
        try {

            TaskLists result = taskSerive().tasklists().list()
                    .setMaxResults(10)
                    .execute();
            List<TaskList> taskLists = result.getItems();
            for (TaskList t: taskLists){
                if (t != null){
                        TaskListInfo taskListInfo = new TaskListInfo();
                        taskListInfo.setTaskListId(t.getId());
                        taskListInfo.setTaskListName(t.getTitle());
                    if (t.getTitle().split("_").length > 1){
                        taskListInfo.setTaskListCode(t.getTitle().split("_")[1]);
                        taskListInfo.setTaskListName(t.getTitle().split("_")[0]);
                    }else{
                        taskListInfo.setIsCommon(1);
                    }
                    taskListInfos.add(taskListInfo);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            log.error("GG_TASK-ERROR: " + e.getMessage());
            return Collections.emptyList();
        }
        return taskListInfos;
    }

    public Task insert(Task task, String taskListId){
        try {
            Task taskIs = taskSerive().tasks().insert(taskListId, task).execute();
            return taskIs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Task> getListTaskByTaskListId(String taskListTestId){
        Tasks.TasksOperations.List request = null;
        try {
            request = taskSerive().tasks().list(taskListTestId);
            return request.execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
