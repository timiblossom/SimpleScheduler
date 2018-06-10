package com.minh.scheduler.server;

import java.util.concurrent.atomic.AtomicLong;

public class Job {
    public String clientId;
    public AtomicLong clientLastPing;
    public JobContext context;

    public Job(String clientId) {
        this.clientId = clientId;
        this.clientLastPing = new AtomicLong(0);
        this.context = new JobContext();
    }

    public void resetPingTime() {
        clientLastPing.set(System.currentTimeMillis());
    }
}
