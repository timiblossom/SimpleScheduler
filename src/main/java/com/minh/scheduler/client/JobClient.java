package com.minh.scheduler.client;

import com.minh.scheduler.grpc.JobRequest;
import com.minh.scheduler.grpc.JobResponse;
import com.minh.scheduler.grpc.JobServiceGrpc;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JobClient {
    private static final Logger logger = LoggerFactory.getLogger(JobClient.class);

    private Configuration configuration;
    private Channel channel;
    private JobServiceGrpc.JobServiceBlockingStub blockingStub;
    private String clientId;

    public JobClient(String clientId, Channel channel, Configuration conf) {
        this.clientId = clientId;
        this.channel = channel;
        this.configuration = conf;
        this.blockingStub = JobServiceGrpc.newBlockingStub(channel);
    }

    public JobClient(String clientId, Configuration conf) {
        this.clientId = clientId;
        this.configuration = conf;
        this.channel = ManagedChannelBuilder.forAddress(conf.getServerAddress(), conf.getListenPort())
                .usePlaintext()
                .build();

        this.blockingStub = JobServiceGrpc.newBlockingStub(channel);
    }

    public String sendRequest() {
        JobResponse jobResponse = blockingStub.submit(JobRequest.newBuilder()
                                              .setClientId(clientId)
                                              .build());

        return jobResponse.getJobStatus();
    }

    public void shutdown() {
        ((ManagedChannel) channel).shutdown();
    }

}