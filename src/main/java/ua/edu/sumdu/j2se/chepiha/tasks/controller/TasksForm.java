package ua.edu.sumdu.j2se.chepiha.tasks.controller;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.chepiha.tasks.service.Notificator;
import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListState;
import ua.edu.sumdu.j2se.chepiha.tasks.view.TasksFormView;

public class TasksForm extends TasksFormView {

    Logger logger = LoggerFactory.getLogger(TasksForm.class.getName());

    @FXML
    public void initialize() {
        try {
            logger.info("{} Start...", LocalDateTime.now());
            logger.info("Start load data from file");
            tasksModel.notifyObservers(ListState.START);
            Notificator.run(tasksModel.getTasks());

            chkCalendar.setOnAction(event -> {
                tasksModel.notifyObservers(ListState.CALENDAR_CHECK);
            });

            btnShowCalendar.setOnAction(event -> {
                if(verifyCalendar()) {
                    tasksModel.notifyObservers(ListState.CALENDAR_DO);
                }
            });

            lvTasks.setOnMouseClicked(event -> {
                tasksModel.notifyObservers(ListState.TASK_GET_INDEX);
                if(tasksModel.getSelectedIndex()>=0){
                    tasksModel.notifyObservers(ListState.TASK_SELECTED);
                }
            });

            btnEdit.setOnAction(event -> {
                tasksModel.notifyObservers(ListState.TASK_EDIT);
            });

            tRepeat.setOnAction(event -> {
                tasksModel.notifyObservers(ListState.TASK_CHANGE_REPEAT);

            });

            btnCreate.setOnAction(event -> {
                tasksModel.notifyObservers(ListState.TASK_CREATE);
            });

            btnCancel.setOnAction(event -> {
                tasksModel.notifyObservers(ListState.TASK_CANCEL);
            });

            btnSave.setOnAction (event -> {
                tasksModel.notifyObservers(ListState.TASK_SAVE);
            });

            btnDelete.setOnAction(event -> {
                tasksModel.notifyObservers(ListState.TASK_DELETE);
            });

        }
        catch (Exception e) {
            logger.error("Global error: ",  e);
        }
    }
}
