package com.minh.scheduler.client;

import com.minh.scheduler.grpc.JobRequest;
import com.minh.scheduler.grpc.JobResponse;
import com.minh.scheduler.grpc.JobServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    public static void main(String[] args) throws InterruptedException {
        JobClient jobClient = new JobClient(args[0], new Configuration());
        logger.info(jobClient.sendRequest());
        jobClient.shutdown();
    }
}
