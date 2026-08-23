package com.external_api.service;


import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class RebootQueueService {

    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(2000);

    public boolean addToQueue(int id) {
        return queue.offer(id);
    }

    public Integer takeFromQueue() throws InterruptedException {
        return queue.take();
    }

    public int getSize() {
        return queue.size();
    }
    
}
