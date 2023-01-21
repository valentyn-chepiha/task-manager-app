/**
 *
 * @autor Valentyn Chepiha
 *
 */
package ua.edu.sumdu.j2se.chepiha.tasks.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

public class Tasks {
    /**
     *
     * @param tasks list of tasks
     * @param start the start period time
     * @param end the end period time
     * @return ArrayList of tasks in the current period time
     * @throws NullPointerException if start or end is null
     * @throws IllegalArgumentException if start after end
     */
    public static Iterable<Task> incoming(Iterable<Task> tasks, LocalDateTime start, LocalDateTime end)
            throws NullPointerException, IllegalArgumentException {

        if(start == null) throw new NullPointerException("Time 'start' must be not null");
        if(end == null) throw new NullPointerException("Time 'end' must be not null");
        if(start.isAfter(end)) throw new IllegalArgumentException("Time 'end' must be above time 'start'");

        Stream.Builder<Task> streamTasks = Stream.builder();
        tasks.forEach(streamTasks::add);
        ArrayList<Task> subTaskList = new ArrayList<>();

        streamTasks
                .build()
                .filter(Objects::nonNull)
                .filter(task -> task.nextTimeAfter(start)!=null)
                .filter(task -> !task.nextTimeAfter(start).isAfter(end))
                .forEach(subTaskList::add);
        return subTaskList;
    }

    /**
     *
     * @param tasks list of Tasks implemented interface Iterable
     * @param start start period of time
     * @param end end period of time
     * @return map type of TreeMap with keys type of LocalDateTime and value type of HashSet
     */
    public static SortedMap<LocalDateTime, Set<Task>> calendar(Iterable<Task> tasks, LocalDateTime start, LocalDateTime end){
        SortedMap<LocalDateTime, Set<Task>> calendarTasks = new TreeMap<>();

        for (Task task: tasks) {
            LocalDateTime nextStart = task.nextTimeAfter(start);
            while (nextStart != null) {
                if(nextStart.isAfter(end)){
                    break;
                }
                Set<Task> currentSet;
                currentSet = calendarTasks.containsKey(nextStart) ? calendarTasks.get(nextStart) : new HashSet<>();
                currentSet.add(task);
                calendarTasks.put(nextStart, currentSet);

                nextStart = task.nextTimeAfter(nextStart);
            }
        }

        return calendarTasks;
    }
}
