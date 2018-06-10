package com.minh.scheduler.server;

public class JobContext {
    public long lastId;
    public long lastRun;
    public int numRuns; //used for unit test purpose

    public JobContext() {
        this.lastId = 1;
        this.lastRun = -1;
        this.numRuns = 0;
    }
}
