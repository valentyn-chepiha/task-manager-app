/**
 *
 * @autor Valentyn Chepiha
 *
 */
package ua.edu.sumdu.j2se.chepiha.tasks.model;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Arrays;
import ua.edu.sumdu.j2se.chepiha.tasks.model.interfaces.LocalDateTimeFromString;
import ua.edu.sumdu.j2se.chepiha.tasks.model.interfaces.LocalDateTimeToString;

public class TaskIO {
    /**
     *
     * @param tasks list of tasks
     * @param out output stream, it is a result, binary format
     * @throws IOException some error with "out"
     */
    public static void write(AbstractTaskList tasks, OutputStream out) throws IOException {

        LocalDateTimeToString localDateTimeToString = (arg)-> arg.getYear() + ":" +
                arg.getMonthValue() + ":" + arg.getDayOfMonth() + ":" + arg.getHour() + ":" +
                arg.getMinute() + ":" + arg.getSecond() + ":" + arg.getNano();

        DataOutputStream output = new DataOutputStream(out);
        output.writeInt(tasks.size());
        if(tasks.size()==0){
            return;
        }
        for (Task task: tasks) {
            if(task != null){
                output.writeUTF( task.getTitle() );
                output.writeInt( task.isActive() ? 1 : 0 );
                output.writeInt( task.getRepeatInterval() );
                if(task.isRepeated()){
                    output.writeUTF( localDateTimeToString.convertToString(task.getStartTime()) );
                    output.writeUTF( localDateTimeToString.convertToString(task.getEndTime()) );
                } else {
                    output.writeUTF( localDateTimeToString.convertToString(task.getTime()) );
                }
            }
        }
    }

    /**
     *
     * @param tasks list of tasks, it is a result
     * @param in input stream, binary format
     * @throws IOException some error with "in"
     */
    public static void read(AbstractTaskList tasks, InputStream in)  throws IOException {

        LocalDateTimeFromString localDateTimeFromString = (arg)-> {
            int[] arrayInt = Arrays.stream(arg.split(":")).mapToInt(Integer::parseInt).toArray();
            return LocalDateTime.of(arrayInt[0], arrayInt[1], arrayInt[2], arrayInt[3], arrayInt[4], arrayInt[5], arrayInt[6]);
        };

        DataInputStream input = new DataInputStream(in);
        int countTask = input.readInt();
        for(int i=0; i<countTask; i++){
            String titleTask = input.readUTF();
            int activeTask = input.readInt();
            int intervalTask = input.readInt();
            Task task;
            if(intervalTask == 0){
                String timeTaskString = input.readUTF();
                LocalDateTime timeTask = localDateTimeFromString.parseToDateTime(timeTaskString);
                task = new Task(titleTask, timeTask);
            } else {
                String startTimeTaskString = input.readUTF();
                LocalDateTime startTimeTask = localDateTimeFromString.parseToDateTime(startTimeTaskString);
                String endTimeTaskString = input.readUTF();
                LocalDateTime endTimeTask = localDateTimeFromString.parseToDateTime(endTimeTaskString);
                task = new Task(titleTask, startTimeTask, endTimeTask, intervalTask);
            }
            if(activeTask == 1) task.setActive(true);
            tasks.add(task);
        }
    }

    /**
     *
     * @param tasks list of tasks
     * @param file it is a result, binary format
     */
    public static void writeBinary(AbstractTaskList tasks, File file) throws IOException, FileNotFoundException {
        FileOutputStream fileOut = new FileOutputStream(file);
        TaskIO.write(tasks, fileOut);
    }

    /**
     *
     * @param tasks list of tasks, it is a result
     * @param file with data, binary format
     */
    public static void readBinary(AbstractTaskList tasks, File file)  throws IOException, FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        TaskIO.read(tasks, fileIn);
    }

    /**
     *
     * @param tasks list of tasks
     * @param out output stream, it is a result, JSON format
     */
    public static void write(AbstractTaskList tasks, Writer out) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        try {
            out.write(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param tasks  list of tasks, it is a result
     * @param in output stream, JSON format
     */
    public static void read(AbstractTaskList tasks, Reader in)  throws IOException, FileNotFoundException {
        Gson gson = new Gson();
        AbstractTaskList tasksFromJSON = gson.fromJson(in, tasks.getClass());
        tasksFromJSON.forEach(tasks::add);
    }

    /**
     *
     * @param tasks list of tasks
     * @param file it is a result, JSON format
     */
    public static void writeText(AbstractTaskList tasks, File file)  throws IOException, FileNotFoundException {
        Writer fileOut = new FileWriter(file);
        TaskIO.write(tasks, fileOut);
    }

    /**
     *
     * @param tasks list of tasks, it is a result
     * @param file JSON format
     */
    public static void readText(AbstractTaskList tasks, File file) throws IOException, FileNotFoundException {
        Reader fileIn = new FileReader(file);
        TaskIO.read(tasks, fileIn);
    }
}
