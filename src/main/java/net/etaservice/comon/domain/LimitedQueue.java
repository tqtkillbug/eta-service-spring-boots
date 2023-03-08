package net.etaservice.comon.domain;

import java.util.LinkedList;
import java.util.Queue;

public class LimitedQueue<T> {

    private Queue<T> queue = new LinkedList<T>();
    private int limit;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    public void add(T item) {
        if (queue.size() >= limit) {
            queue.poll();
        }
        queue.add(item);
    }

    public Queue<T> getQueue() {
        return queue;
    }
}
