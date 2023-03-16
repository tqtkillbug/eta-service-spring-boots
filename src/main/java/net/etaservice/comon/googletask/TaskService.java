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
import net.etaservice.comon.googlesheet.ISheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskService {

    @Autowired
    private ISheetService service;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


    @SneakyThrows
    @Scheduled(initialDelay = 1000, fixedDelay=5000)
    public void testGetTask(){
        // Print the first 10 task lists.
        long startTime = System.currentTimeMillis();


        TaskLists result = taskSerive().tasklists().list()
                .setMaxResults(10)
                .execute();
        List<TaskList> taskLists = result.getItems();
        if (taskLists == null || taskLists.isEmpty()) {
            System.out.println("No task lists found.");
        } else {
            System.out.println("Task lists:");
            for (TaskList tasklist : taskLists) {
                System.out.printf("%s (%s)\n", tasklist.getTitle(), tasklist.getId());
            }
        }
        String taskListTestId= "bFhnOEdKRWpWeHVvWkFNWg";
        Tasks.TasksOperations.List request = taskSerive().tasks().list(taskListTestId);
        List<Task> tasksList = request.execute().getItems();
        for(Task t : tasksList){
            System.out.println(t.toString());
        }

        //test insert new task
        Task task = new Task();
        task.setTitle("Task insert by API" +System.currentTimeMillis());
        task.setNotes("Task notes insert by API");
        Task taskIs = taskSerive().tasks().insert(taskListTestId, task).execute();
        System.out.println(taskIs);
        long endTime = System.currentTimeMillis();
        double elapsedTime = (endTime - startTime) / 1000.0;
        System.out.println("Thời gian thực thi của hàm là " + elapsedTime + " giây.");


    }

    @SneakyThrows
    public Tasks taskSerive(){
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = service.getCredentials(HTTP_TRANSPORT);
        Tasks service =
                new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName("TASKMANAGER")
                        .build();
        return service;
    }
}
