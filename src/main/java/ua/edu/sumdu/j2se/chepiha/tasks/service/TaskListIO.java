package ua.edu.sumdu.j2se.chepiha.tasks.service;

import java.io.File;
import ua.edu.sumdu.j2se.chepiha.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.chepiha.tasks.model.TaskIO;

public class TaskListIO {

    public static void loadTaskList(String filePath, AbstractTaskList tasks) throws Exception {
        System.out.println("File path: " + filePath);
        File dataFile = new File(filePath);
        if(dataFile.exists() && dataFile.isFile()){
            TaskIO.readBinary(tasks, new File(filePath));
        }
    }

    public static void saveTaskList(String filePath, AbstractTaskList tasks) throws Exception {
        System.out.println("File path: " + filePath);
        TaskIO.writeBinary(tasks, new File(filePath));
    }

}
