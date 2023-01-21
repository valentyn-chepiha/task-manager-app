package ua.edu.sumdu.j2se.chepiha.tasks.view;

import java.time.LocalDateTime;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import jfxtras.scene.control.LocalDateTimeTextField;
import ua.edu.sumdu.j2se.chepiha.tasks.controller.TasksFormModel;
import ua.edu.sumdu.j2se.chepiha.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.chepiha.tasks.model.Task;
import ua.edu.sumdu.j2se.chepiha.tasks.service.VerifyingData;
import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListOperationTask;
import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListState;

public class TasksFormView implements Observer {

    @FXML
    public CheckBox chkCalendar;

    @FXML
    public LocalDateTimeTextField dtStartCalendar;

    @FXML
    public LocalDateTimeTextField dtEndCalendar;

    @FXML
    public Button btnShowCalendar;

    @FXML
    public ListView<String> lvTasks;

    @FXML
    public Button btnCreate;

    @FXML
    public Button btnEdit;

    @FXML
    public Label tLabelName;

    @FXML
    public TextField tName;

    @FXML
    public CheckBox tRepeat;

    @FXML
    public CheckBox tActive;

    @FXML
    public Label tLabelStartTime;

    @FXML
    public Label tLabelEndTime;

    @FXML
    public LocalDateTimeTextField tStartTime;

    @FXML
    public LocalDateTimeTextField tEndTime;

    @FXML
    public Label tLabelInterval;

    @FXML
    public TextField tInterval;

    @FXML
    public Button btnDelete;

    @FXML
    public Button btnSave;

    @FXML
    public Button btnCancel;

    public TasksFormModel tasksModel = new TasksFormModel();

    private final Pattern INTERVAL_VALUE_FORMAT = Pattern.compile("^([1-9](\\d)*)?$");
    private static final String DEFAULT_INTERVAL = "3600";
    private static final int DEFAULT_SELECTED_ITEM = -1;

    private ListOperationTask.types typeOperation;

    public TasksFormView() {
        tasksModel.registerObserver(this);
    }

    @Override
    public void notification(ListState state) {
        Task task = null;
        switch (state){
            case START:
                startInit();
                startValueFormat();
                loadTasksListToListView(tasksModel.getWorkTasks());
                break;
            case CALENDAR_CHECK:
                tasksModel.notifyObservers(chkCalendar.isSelected() ?
                        ListState.CALENDAR_SHOW :
                        ListState.CALENDAR_HIDE);
                calendarSwitch();
                break;
            case CALENDAR_SHOW:
            case CALENDAR_HIDE:
                loadTasksListToListView(tasksModel.getWorkTasks());
                break;
            case CALENDAR_DO:
                tasksModel.getWorkTaskList(dtStartCalendar.getLocalDateTime(), dtEndCalendar.getLocalDateTime());
                loadTasksListToListView(tasksModel.getWorkTasks());
                break;
            case TASK_GET_INDEX:
                tasksModel.setSelectedIndex(getIndexSelectedTask());
                break;
            case TASK_SELECTED:
                task = tasksModel.getSelectedTask();
                setBtnTaskOn();
                setHideFormTasks();
                setDisableFormTasks();
                if(task.isRepeated()){
                    loadTaskRepeatEdit(task);
                } else {
                    loadTaskOnceEdit(task);
                }
                break;
            case TASK_EDIT:
                typeOperation = ListOperationTask.types.EDIT;
                setBtnTaskOff();
                setBtnCRUDOn();
                setDisableListView();
                setBtnTaskOff();
                changeEditTask();
                setBtnCRUDVisible();
                setBtnCRUDOn();
                break;
            case TASK_CHANGE_REPEAT:
                setHideFormTasks();
                setDisableFormTasks();
                changeEditTask();
                break;
            case TASK_CREATE:
                typeOperation = ListOperationTask.types.CREATE;
                setBtnTaskOff();
                setBtnCreatVisible();
                setBtnCreateOn();
                setHideFormTasks();
                setDisableFormTasks();
                setEnableTaskOnce();
                clearFieldsFormTasks();
                setDefaultValuesTask();
                break;
            case TASK_CANCEL:
                setBtnTaskStart();
                setBtnCRUDHide();
                setHideFormTasks();
                clearFieldsFormTasks();
                tasksModel.setSelectedIndex(DEFAULT_SELECTED_ITEM);
                loadTasksListToListView(tasksModel.getWorkTasks());
                break;
            case TASK_SAVE:
                if(verifyTaskBeforeSave()){
                    saveNewTask();
                    tasksModel.saveTaskListToFile();
                    tasksModel.insertDataToWorkList();
                    tasksModel.setSelectedIndex(DEFAULT_SELECTED_ITEM);
                    refreshFormAfterCRUD(tasksModel.getWorkTasks());
                }
                break;
            case TASK_DELETE:
                task = tasksModel.getSelectedTask();
                if(ModalWindow.showConfirmDelete(task.toString())){
                    tasksModel.removeTask(task);
                    tasksModel.saveTaskListToFile();
                    tasksModel.insertDataToWorkList();
                    tasksModel.setSelectedIndex(DEFAULT_SELECTED_ITEM);
                    refreshFormAfterCRUD(tasksModel.getWorkTasks());
                }
                break;
        }
    }

    private void startInit(){
        TaskFormInit.initFormCalendar(chkCalendar, dtStartCalendar,
                dtEndCalendar, btnShowCalendar);
        TaskFormInit.initFormTask(btnEdit, btnCreate, tStartTime, tEndTime,
                tLabelName, tName, tActive, tRepeat, tLabelStartTime,
                tLabelEndTime, tLabelInterval, tInterval, btnCancel,
                btnSave, btnDelete);
    }

    private void startValueFormat(){
        tInterval.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!INTERVAL_VALUE_FORMAT.matcher(newValue).matches())
                tInterval.setText(oldValue);
        });
    }

    private void calendarSwitch(){
        if(chkCalendar.isSelected()){
            TaskFormInit.setCalendarOn(dtStartCalendar, dtEndCalendar, btnShowCalendar);
            TaskFormInit.formSet.setDisable(btnCreate, btnEdit, lvTasks);
        } else {
            TaskFormInit.setCalendarOff(dtStartCalendar, dtEndCalendar, btnShowCalendar);
            TaskFormInit.formSet.setEnable(btnCreate);
            TaskFormInit.formSet.setDisable(btnEdit);
        }
        TaskFormInit.setHideTaskEditFields(btnEdit, btnCreate, tLabelName,
                tName, tActive, tRepeat, tLabelStartTime,
                tStartTime, tLabelEndTime, tEndTime, tLabelInterval,
                tInterval, btnCancel, btnSave, btnDelete);
    }

    private void loadTasksListToListView(AbstractTaskList taskList){
        lvTasks.getItems().clear();
        TaskFormInit.formSet.setDisable(lvTasks);
        if(taskList.size()>0){
            ObservableList<String> names = FXCollections.observableArrayList();
            taskList.forEach(task -> names.add(task.toString()));
            lvTasks.setItems(names);
            TaskFormInit.formSet.setEnable(lvTasks);
        }
    }

    private void setDisableListView(){
        TaskFormInit.formSet.setDisable(lvTasks);
    }

    public boolean verifyCalendar(){
        boolean result = true;
        String error = VerifyingData.verifyCalendar(dtStartCalendar.getLocalDateTime(),
                dtEndCalendar.getLocalDateTime());
        if(error.length()>0){
            result = false;
            ModalWindow.showAlertError(error);
        }
        return result;
    }

    private int getIndexSelectedTask(){
        return lvTasks.getSelectionModel().getSelectedIndex();
    }

    private void setBtnTaskStart(){
        TaskFormInit.formSet.setEnable(btnCreate);
        TaskFormInit.formSet.setDisable(btnEdit);
    }

    private void setBtnTaskOn(){
        TaskFormInit.formSet.setEnable(btnCreate, btnEdit);
    }

    public void setBtnTaskOff(){
        TaskFormInit.formSet.setDisable(btnCreate, btnEdit);
    }

    private void setBtnCRUDOn(){
        TaskFormInit.formSet.setEnable(btnDelete);
        setBtnCreateOn();
    }

    private void setBtnCreateOn(){
        TaskFormInit.formSet.setEnable(btnCancel, btnSave);
    }

    private void setBtnCRUDVisible(){
        TaskFormInit.formSet.setVisible(btnDelete);
        setBtnCreatVisible();
    }

    private void setBtnCreatVisible(){
        TaskFormInit.formSet.setVisible(btnCancel, btnSave);
    }

    private void setBtnCRUDHide(){
        TaskFormInit.formSet.setHide(btnCancel, btnSave, btnDelete);
    }

    private void setHideFormTasks(){
        TaskFormInit.setHideFormTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime, tLabelEndTime,
                tEndTime, tLabelInterval, tInterval);
    }

    private void setDisableFormTasks(){
        TaskFormInit.setDisableFormTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime, tLabelEndTime,
                tEndTime, tLabelInterval, tInterval);
    }

    private void loadTaskOnceEdit(Task task){
        tName.setText(task.getTitle());
        tActive.setSelected(task.isActive());
        tRepeat.setSelected(false);
        tStartTime.setLocalDateTime(task.getStartTime());

        TaskFormInit.setVisibleFormOnceTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime);
        TaskFormInit.formSet.setTextValue("Time:", tLabelStartTime);
    }

    private void loadTaskRepeatEdit(Task task){
        tName.setText(task.getTitle());
        tActive.setSelected(task.isActive());
        tRepeat.setSelected(true);
        tStartTime.setLocalDateTime(task.getStartTime());
        tEndTime.setLocalDateTime(task.getEndTime());
        tInterval.setText("" + task.getRepeatInterval());

        TaskFormInit.setVisibleFormRepeatTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime, tLabelEndTime,
                tEndTime, tLabelInterval, tInterval);
        TaskFormInit.formSet.setTextValue("Start time:", tLabelStartTime);
    }

    private void clearFieldsFormTasks(){
        tName.setText("");
        tStartTime.setLocalDateTime(null);
        tEndTime.setLocalDateTime(null);
        tActive.setSelected(false);
        tRepeat.setSelected(false);
        tInterval.setText(DEFAULT_INTERVAL);
    }

    private void setDefaultValuesTask(){
        LocalDateTime NOW = LocalDateTime.now();
        if(tStartTime.getLocalDateTime() == null){
            tStartTime.setLocalDateTime(NOW);
        }
        if(tEndTime.getLocalDateTime() == null){
            tEndTime.setLocalDateTime(NOW);
        }
        if(tInterval.getText() == null
                || tInterval.getText().length() == 0){
            tInterval.setText(DEFAULT_INTERVAL);
        }
    }

    private void setEnableTaskOnce(){
        TaskFormInit.setVisibleFormOnceTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime);

        TaskFormInit.setEnableFormOnceTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime);
    }

    private void setEnableTaskRepeat(){
        TaskFormInit.setVisibleFormRepeatTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime, tLabelEndTime,
                tEndTime, tLabelInterval, tInterval);

        TaskFormInit.setEnableFormRepeatTasksFields(tLabelName, tName, tActive,
                tRepeat, tLabelStartTime, tStartTime, tLabelEndTime,
                tEndTime, tLabelInterval, tInterval);
    }

    private void changeEditTask(){
        setDefaultValuesTask();
        if(tRepeat.isSelected()){
            setEnableTaskRepeat();
        } else {
            setEnableTaskOnce();
        }
    }

    private void refreshFormAfterCRUD(AbstractTaskList tasks){
        loadTasksListToListView(tasks);
        clearFieldsFormTasks();
        setHideFormTasks();
        setBtnCRUDHide();
        setBtnTaskStart();
    }

    public boolean verifyTaskBeforeSave(){
        String msgError = "";
        if(tRepeat.isSelected()){
            msgError = VerifyingData.isVerifyTask(tName.getText(),
                    tStartTime.getLocalDateTime(), tEndTime.getLocalDateTime(),
                    tInterval.getText());
        } else {
            msgError = VerifyingData.isVerifyTask(tName.getText(),
                    tStartTime.getLocalDateTime());
        }
        if(msgError.length()>0){
            ModalWindow.showAlertError(msgError);
            return false;
        }
        return true;
    }

    private void saveNewTask(){
        if(typeOperation == ListOperationTask.types.CREATE){
            tasksModel.creteTask(tName.getText(), tStartTime.getLocalDateTime(), tActive.isSelected(),
                    tRepeat.isSelected(), tEndTime.getLocalDateTime(),
                    Integer.parseInt(tInterval.getText()));
        } else {
            tasksModel.updateTask(tName.getText(), tStartTime.getLocalDateTime(), tActive.isSelected(),
                    tRepeat.isSelected(), tEndTime.getLocalDateTime(),
                    Integer.parseInt(tInterval.getText()));
        }
    }

}
