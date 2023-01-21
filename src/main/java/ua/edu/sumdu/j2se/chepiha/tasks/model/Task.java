/**
 *
 * @autor Valentyn Chepiha
 *
 * Returns a Task object.
 * The object has methods to adding, enabling, disabling the task.
 * You can change parameters the task: name, start, end or interval.
 */
package ua.edu.sumdu.j2se.chepiha.tasks.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Serializable {
    private static final LocalDateTime DEFAULT_TIME_VALUE = LocalDateTime.MIN;
    private static final int DEFAULT_INTERVAL_VALUE = -1;
    private static final int RESULT_ZERO = 0;

    private String title;
    private LocalDateTime time;
    private LocalDateTime start;
    private LocalDateTime end;
    private int interval;
    private boolean activeTask = false;

    /**
     *
     * @param title     task name
     * @param time      the time for a one-time task
     */
    public Task(String title, LocalDateTime time) throws IllegalArgumentException {
        if(time == null) throw new IllegalArgumentException("Time must be not null");
        if(title == null || title.trim().equals("")) throw new IllegalArgumentException("Title must be not null");

        setDefaultRepeatedTime();

        this.title = title;
        this.time = time;
    }

    /**
     *
     * @param title task name
     * @param start the start time of the recurring task
     * @param end the end time of the recurring task
     * @param interval a period of time to repeat the task
     */
    public Task(String title, LocalDateTime start, LocalDateTime end, int interval) throws IllegalArgumentException {
        if(start == null) throw new IllegalArgumentException("Time start must be not null");
        if(end == null) throw new IllegalArgumentException("Time end must be not null");
        if(end.isBefore(start)) throw new IllegalArgumentException("Time end must be above time start");

        if(title == null || title.trim().equals("")) throw new IllegalArgumentException("Title must be not null");
        if(interval<0) throw new IllegalArgumentException("Interval must be above zero");

        setDefaultNotRepeatedTime();

        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    private void setDefaultNotRepeatedTime() {
        time = DEFAULT_TIME_VALUE;
    }

    private void setDefaultRepeatedTime() {
        start = DEFAULT_TIME_VALUE;
        end = DEFAULT_TIME_VALUE;
        interval = DEFAULT_INTERVAL_VALUE;
    }

    /**
     *
     * @return returning name of task
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title task name
     */
    public void setTitle(String title) throws IllegalArgumentException {

        if(title.trim().length()<=0) throw new IllegalArgumentException("Name task must not equal null");

        this.title = title;
    }

    /**
     *
     * @return returning true if task is enabled, else returning false
     */
    public boolean isActive() {
        return activeTask;
    }

    /**
     *
     * @param activeTask flag to enable the task: true - enabled, false - disabled
     */
    public void setActive(boolean activeTask) {
        this.activeTask = activeTask;
    }

    /**
     *
     * @return time to start a one-time task
     */
    public LocalDateTime getTime() {
        return start.isAfter(DEFAULT_TIME_VALUE) ? start : time;
    }

    /**
     *
     * @param time the time for a one-time task
     */
    public void setTime(LocalDateTime time) throws IllegalArgumentException {
        if(time == null) throw new IllegalArgumentException("Time must be not null");

        setDefaultRepeatedTime();
        this.time = time;
    }

    /**
     *
     * @return return time to start the task
     */
    public LocalDateTime getStartTime() {
        return start.isAfter(DEFAULT_TIME_VALUE) ? start : time;
    }

    /**
     *
     * @return return time to end the task
     */
    public LocalDateTime getEndTime() {
        return end.isAfter(DEFAULT_TIME_VALUE) ? end : time;
    }

    /**
     *
     * @return return the interval to repeat the task, if task runs once return 0
     */
    public int getRepeatInterval() {
        return (interval > DEFAULT_INTERVAL_VALUE) ? interval : RESULT_ZERO ;
    }

    /**
     *
     * @param start the start time of the recurring task
     * @param end the end time of the recurring task
     * @param interval seconds to repeat the task
     */
    public void setTime(LocalDateTime start, LocalDateTime end, int interval) throws IllegalArgumentException {
        if(start == null) throw new IllegalArgumentException("Time start must be not null");
        if(end == null) throw new IllegalArgumentException("Time end must be not null");
        if(end.isBefore(start)) throw new IllegalArgumentException("Time end must be above time start");

        if(interval<0) throw new IllegalArgumentException("Interval must be above zero");

        setDefaultNotRepeatedTime();

        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    /**
     *
     * @return return true if the task is recurring else return false
     */
    public boolean isRepeated() {
        return start.isAfter(DEFAULT_TIME_VALUE);
    }

    private LocalDateTime calculateNextStart (LocalDateTime current) {
        long deltaTime = Duration.between(start, current).getSeconds();
        long wholePart = (long)deltaTime / interval;

        if(wholePart * interval <= deltaTime )
            wholePart++;

        return start.plusSeconds(wholePart * interval);
    }

    private LocalDateTime getNextRepeatedTime(LocalDateTime current) {
        if(!current.isAfter(start))
            return start;
        if(current.isAfter(end))
            return null;
        LocalDateTime nextTime = calculateNextStart(current);

        return nextTime.isAfter(end) ? null : nextTime;
    }

    private LocalDateTime getNextNotRepeatedTime(LocalDateTime current) {
        return !time.isBefore(current) ? time : null;
    }

    /**
     *
     * @param current time to next start the task
     * @return return the time to next start the task, or null if the task never will be starting
     */
    public LocalDateTime nextTimeAfter (LocalDateTime current) throws IllegalArgumentException {
        if(current == null) throw new IllegalArgumentException("Current time must be not null");

        if(!activeTask)
            return null;

        return time.isAfter(DEFAULT_TIME_VALUE) ? getNextNotRepeatedTime(current) : getNextRepeatedTime(current);
    }

    /**
     *
     * @return string line with text info about task
     */
    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                (time.isAfter(DEFAULT_TIME_VALUE) ? ", time=" + time : "") +
                (start.isAfter(DEFAULT_TIME_VALUE) ? ", start=" + start : "") +
                (end.isAfter(DEFAULT_TIME_VALUE) ? ", end=" + end : "") +
                (interval > DEFAULT_INTERVAL_VALUE ? ", interval=" + interval : "") +
                ", activeTask=" + activeTask +
                '}';
    }

    /**
     *
     * @param o object to compare
     * @return if current task equals o return true else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return time.equals(task.time)
                && start.equals(task.start)
                && end.equals(task.end)
                && interval == task.interval
                && activeTask == task.activeTask
                && title.equals(task.title);
    }

    /**
     *
     * @return hash code current task
     */
    @Override
    public int hashCode() {
        int salt = 31;
        int result = 7;
        result = salt * result + title.hashCode();
        result = salt * result + time.getSecond();
        result = salt * result + start.getSecond();
        result = salt * result + end.getSecond();
        result = salt * result + interval;
        result = salt * result + (activeTask ? 1 : 0);
        return result;
    }

    /**
     *
     * @return clone current task
     */
    @Override
    public Task clone(){
        Task cloneTask = new Task(title, time);

        if(isRepeated()){
            cloneTask.setTime(start, end, interval);
        }
        cloneTask.setActive(isActive());

        return cloneTask;
    };
}
