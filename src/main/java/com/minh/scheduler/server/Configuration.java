package com.minh.scheduler.server;

public class Configuration {
    private long heartBeatTimeoutInMilliSecs = 30000;
    private int listenPort = 8090;

    public void setHeartBeatTimeoutInMilliSecs(long timeoutInMilliSecs) {
        this.heartBeatTimeoutInMilliSecs = timeoutInMilliSecs;
    }

    public long getHeartBeatTimeoutInMilliSecs() {
        return this.heartBeatTimeoutInMilliSecs;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

}
