package net.etaservice.comon.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LimitedQueue<T> {

    private Queue<T> queue = new LinkedList<T>();
    private int limit;
    private T lastElement;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    public void add(T item) {
        if (queue.size() >= limit) {
            queue.poll();
        }
        lastElement = item;
        queue.add(item);
    }

    public List<T> getListElement(){
        return new ArrayList<>(queue);
    }

    public Queue<T> getQueue() {
        return queue;
    }
}
