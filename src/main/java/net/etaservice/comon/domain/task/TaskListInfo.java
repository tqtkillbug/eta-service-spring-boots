package net.etaservice.comon.domain.task;

import lombok.Data;

@Data
public class TaskListInfo {
    private String taskListName;
    private String taskListId;
    private String taskListCode;
    private int isCommon;
}
