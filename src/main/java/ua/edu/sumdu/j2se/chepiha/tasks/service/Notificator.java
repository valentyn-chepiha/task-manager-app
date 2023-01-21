package ua.edu.sumdu.j2se.chepiha.tasks.service;

import javafx.application.Platform;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import ua.edu.sumdu.j2se.chepiha.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.chepiha.tasks.model.Task;
import ua.edu.sumdu.j2se.chepiha.tasks.view.ModalWindow;

public class Notificator {

    public static void run(AbstractTaskList taskList){
        Timer timer = new Timer();

        TimerTask cb = new TimerTask() {
            @Override
            public void run() {
                DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                StringBuilder taskNow = new StringBuilder("Task: ");
                AtomicBoolean flag = new AtomicBoolean(false);
                if(taskList.size()>0){
                    for (Task task: taskList) {
                        LocalDateTime nextTime = task.nextTimeAfter(now);
                        if (nextTime!= null){
                            nextTime = nextTime.truncatedTo(ChronoUnit.MINUTES);
                            if( nextTime.format(DATE_FORMAT).equals(now.format(DATE_FORMAT)) ){
                                flag.set(true);
                                taskNow.append(task.getTitle()).append("; ");
                            }
                        }
                    }
                }
                if(flag.get()){
                    Platform.runLater(()-> ModalWindow.showAlertInformation(taskNow.toString()));
                }
            }
        };

        timer.scheduleAtFixedRate(
                cb,
                Timestamp.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)),
                60000);
    }

}
