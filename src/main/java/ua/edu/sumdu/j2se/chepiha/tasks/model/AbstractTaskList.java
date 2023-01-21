/**
 *
 * @autor Valentyn Chepiha
 *
 */
package ua.edu.sumdu.j2se.chepiha.tasks.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;
import ua.edu.sumdu.j2se.chepiha.tasks.model.types.ListType;

public abstract class AbstractTaskList implements Iterable<Task>, Cloneable, Serializable {
    public abstract void add(Task task);
    public abstract boolean remove(Task task);
    public abstract int size();
    public abstract Task getTask(int index);
    public abstract void clear();

    private static final long SerialVersionUID = 1;

    public abstract Iterator<Task> iterator();

    private AbstractTaskList getCopyInstance(AbstractTaskList original){
        ListType.types typeLists = TaskListFactory.getTypeTaskList(original);
        return TaskListFactory.createTaskList( typeLists );
    }

    /**
     *
     * @return string line with text info about list of tasks
     */
    @Override
    public String toString() {
        if(this.size() == 0)
            return "List is empty";

        StringBuilder result = new StringBuilder();

        String[] classInfoArray = this.getClass().toString().split(" ")[1].split("\\.");

        result.append("ListTask type of ")
                .append(classInfoArray[classInfoArray.length-1])
                .append("\n")
                .append("Elements:\n");

        this.forEach(task -> result.append(task).append("\n"));

        return result.toString();
    };

    /**
     *
     * @param o object to compare
     * @return if current list equals o return true else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTaskList taskList = (AbstractTaskList) o;

        if(this.size() != taskList.size()) return false;

        boolean result = true;
        for(int i = 0; i<this.size(); i++){
            result = this.getTask(i).equals(taskList.getTask(i));
            if(!result)
                break;
        }
        return result;
    };

    /**
     *
     * @return hash code current list of tasks
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (Task task: this) {
            result = result + task.hashCode();
        }
        return result;
    }

    /**
     *
     * @return clone current list of tasks
     */
    @Override
    public AbstractTaskList clone() {
        AbstractTaskList cloneTaskList = getCopyInstance(this);

        for (Task task: this) {
            cloneTaskList.add(task.clone());
        }
        return cloneTaskList;
    }

    public abstract Stream<Task> getStream();
}
