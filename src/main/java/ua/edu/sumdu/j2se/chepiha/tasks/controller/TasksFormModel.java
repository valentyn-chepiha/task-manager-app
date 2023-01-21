package ua.edu.sumdu.j2se.chepiha.tasks.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.chepiha.tasks.view.Observer;
import ua.edu.sumdu.j2se.chepiha.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.chepiha.tasks.model.ArrayTaskList;
import ua.edu.sumdu.j2se.chepiha.tasks.model.Task;
import ua.edu.sumdu.j2se.chepiha.tasks.model.Tasks;
import ua.edu.sumdu.j2se.chepiha.tasks.service.TaskListIO;
import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListState;

public class TasksFormModel implements Observable {

    Logger logger = LoggerFactory.getLogger(TasksFormModel.class.getName());
    private List<Observer> observers;

    private AbstractTaskList tasks = null;
    private AbstractTaskList workTasks = null;
    private int selectedIndex = -1;

    public TasksFormModel() {
        this.tasks = new ArrayTaskList();
        this.workTasks = new ArrayTaskList();
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        if(observer != null){
            observers.add(observer);
        }
    }

    @Override
    public void notifyObservers(ListState state) {
        switch (state){
            case START:
                initTaskList();
                break;
            case CALENDAR_SHOW:
            case CALENDAR_HIDE:
                insertDataToWorkList();
                break;
        }

        for (Observer observer : observers) {
            observer.notification(state);
        }
    }

    public AbstractTaskList getTasks() {
        return tasks;
    }

    public AbstractTaskList getWorkTasks() {
        return workTasks;
    }

    public void setSelectedIndex(int selectedIndex) {
        if(selectedIndex >= 0 && this.selectedIndex != selectedIndex){
            this.selectedIndex = selectedIndex;
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Task getSelectedTask() {
        return workTasks.getTask(selectedIndex);
    }

    public void removeTask(Task task){
        if(task != null){
            logger.info("Deleted task: " + task.toString());
            tasks.remove(task);
        }
    }

    public void insertDataToWorkList(){
        workTasks.clear();
        if(tasks.size()>0){
            tasks.forEach(workTasks::add);
        }
    }

    public void initTaskList() {
        try {
            TaskListIO.loadTaskList(getFullFileName(), tasks);
            insertDataToWorkList();
            logger.info("Loaded {} rows", tasks.size());
        } catch (Exception e){
            logger.error("Error load: ",  e);
        }
    }

    public void getWorkTaskList(LocalDateTime start, LocalDateTime end){
        Iterable<Task> calendarTasks = Tasks.incoming(tasks, start, end);
        workTasks.clear();
        calendarTasks.forEach(workTasks::add);
    }

    public void saveTaskListToFile(){
        try {
            TaskListIO.saveTaskList(getFullFileName(), tasks);
            logger.info("File updated, now {} rows", tasks.size());
        } catch (Exception e) {
            logger.error("Save task list: ", e);
        }

    }

    public void creteTask(String name, LocalDateTime time, boolean isActive,
                          boolean isRepeated, LocalDateTime endTime, int interval){
        Task task;
        if(isRepeated){
            task = new Task(name, time, endTime, interval);
        } else {
            task = new Task(name, time);
        }
        if(isActive){
            task.setActive(true);
        }
        tasks.add(task);
        logger.info("Added task: " + task.toString());
    }

    public void updateTask(String name, LocalDateTime time, boolean isActive,
                           boolean isRepeated, LocalDateTime endTime, int interval){
        Task task = workTasks.getTask(selectedIndex);
        task.setTitle(name);
        if(isRepeated){
            task.setTime(time, endTime, interval);
        } else {
            task.setTime(time);
        }
        if(isActive){
            task.setActive(true);
        }
        logger.info("Updated task: " + task.toString());
    }


    private String getFullFileName() {
        String rootPath = Thread.currentThread().getContextClassLoader()
                .getResource("application.properties").getPath();
        try (InputStream inputStream = new FileInputStream(rootPath)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String dirName = properties.getProperty("app.data.dirname");
            File dataDir = new File(dirName);
            if (!dataDir.exists()){
                dataDir.mkdirs();
            }
            String filename = properties.getProperty("app.data.filename");
            return dirName + File.separator + filename;
        } catch (IOException e) {
            throw new RuntimeException("File application.properties not found", e);
        }
    }

}
