/**
 *
 * @autor Valentyn Chepiha
 *
 * Returns a LinkedTaskList object.
 * The object has methods to adding, removing element of List.
 * You can get the size of the list, information about the task in the list
 * and the List of tasks it performs between two current moments.
 */
package ua.edu.sumdu.j2se.chepiha.tasks.model;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class LinkedTaskList extends AbstractTaskList {

    private static class Node {
        Task value;
        Node prev;
        Node next;

        public Node(Node prev, Task value, Node next){
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private int sizeList = 0;
    private Node first;
    private Node last;

    /**
     * constructor
     */
    public LinkedTaskList() {
    }

    /**
     *
     * @param task new task to the list
     * @throws IllegalArgumentException generated exception if 'task' is null
     */
    @Override
    public void add(Task task) throws IllegalArgumentException {

        if(task == null) throw new IllegalArgumentException("Task must not equal null");

        switch (sizeList){
            case 0:
                first = new Node(null, task,null);
                break;
            case 1:
                last = new Node(first, task, null);
                first.next = last;
                break;
            default:
                Node oldLast = last;
                last = new Node(oldLast, task, null);
                oldLast.next = last;
        }
        sizeList++;
    }

    private void removeCurrentNode(Node currentNode){
        if(currentNode.prev != null){
            currentNode.prev.next = currentNode.next;
        } else {
            first = currentNode.next;
        }

        if(currentNode.next != null) {
            currentNode.next.prev = currentNode.prev;
        } else {
            last = currentNode.prev;
        }

        sizeList--;
        if(sizeList == 1){
            last = null;
        }
    }

    /**
     *
     * @param task the task that need remove
     * @return if is done return true else return false
     * @throws IllegalArgumentException generated exception if 'task' is null
     */
    @Override
    public boolean remove(Task task) throws IllegalArgumentException {

        if(task == null) throw new IllegalArgumentException("Task must not equal null");

        if(sizeList==0) return false;

        Node currentNode = first;
        do {
            if(currentNode.value.equals(task)){
                removeCurrentNode(currentNode);
                return true;
            }
            currentNode = currentNode.next;
        } while (currentNode != null);

        return false;
    }

    /**
     *
     * @return amount tasks in the list
     */
    @Override
    public int size(){
        return sizeList;
    }

    private Task searchUp(int index){
        int maxI = (int)sizeList/2 + 1;
        Node currentNode = first;
        for(int i=0; i<maxI; i++){
            if(i == index)
                return currentNode.value;
            currentNode = currentNode.next;
        }
        return null;
    }

    private Task searchDown(int index){
        int minI = (int)sizeList/2 - 1;
        Node currentNode = last;
        for(int i=sizeList-1; i>minI; i--){
            if(i == index)
                return currentNode.value;
            currentNode = currentNode.prev;
        }
        return null;
    }

    /**
     *
     * @param index the index of the task in the list, starting with 0
     * @return return the task or null
     * @throws IndexOutOfBoundsException generated exception if 'index' is wrong
     */
    @Override
    public Task getTask(int index) throws IndexOutOfBoundsException {

        if(index<0 || index>=sizeList) throw new IndexOutOfBoundsException("Index out of list");

        return ( index <= sizeList / 2) ? this.searchUp(index) : this.searchDown(index);
    }

    /**
     *
     * @return iterator for list of task
     */
    @Override
    public Iterator<Task> iterator() {
        return new IteratorList();
    }

    private class IteratorList implements Iterator<Task> {

        private int currentIndex = -1;
        private Node currentNode = first;

        @Override
        public boolean hasNext() {
            return currentIndex < sizeList;
        }

        @Override
        public Task next() {
            if(currentIndex >= sizeList)
                throw new NoSuchElementException();
            if(currentIndex<0)
                currentIndex++;
            currentIndex++;
            Task task = currentNode.value;
            currentNode = currentNode.next;
            return task;
        }

        @Override
        public void remove() {
            if(currentIndex < 0 || currentIndex >= sizeList)
                throw new IllegalStateException();
            currentIndex--;
            removeCurrentNode(currentNode.prev);
        }
    }

    /**
     *
     * @return stream for list of task
     * @throws NoSuchElementException if list is empty
     */
    @Override
    public Stream<Task> getStream() throws NoSuchElementException {
        if (sizeList == 0)
            throw new NoSuchElementException();

        Stream.Builder<Task> collection = Stream.builder();
        for (Task task: this) {
            collection.add(task);
        }
        return collection.build();
    }

    public void clear(){
        first.value = null;
        first.next = null;

        last.value = null;
        last.prev = null;

        sizeList = 0;
    }
}
