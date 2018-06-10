package com.minh.scheduler;


import com.minh.scheduler.client.JobClient;
import com.minh.scheduler.server.Configuration;
import com.minh.scheduler.server.Job;
import com.minh.scheduler.server.JobScheduler;
import com.minh.scheduler.server.JobServerRunner;
import com.minh.scheduler.utils.Utils;
import io.grpc.testing.GrpcServerRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Duration;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class SimpleSchedulerTest {

    @Rule
    public final GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    /*
     * Submit two client jobs and verify that the scheduler on the server actually runs two jobs.
     */
    @Test
    public void TestJobRequestWithScheduler() throws Exception {
        //Need to start our scheduler first prior to any testing
        JobScheduler scheduler = new JobScheduler(new Configuration());
        scheduler.start();

        // Add the service to the in-process server.  This would implicitly start our server
        grpcServerRule.getServiceRegistry().addService(new JobServerRunner.JobServiceImpl(scheduler));

        //Simulating Client1 sending a request
        String clientId1 = "Client1";
        JobClient client1 = new JobClient(clientId1, grpcServerRule.getChannel(),
                                          new com.minh.scheduler.client.Configuration());

        assertEquals("Accepted " + clientId1, client1.sendRequest());

        //Simulating Client2 sending a request
        String clientId2 = "Client2";
        JobClient client2 = new JobClient(clientId2, grpcServerRule.getChannel(),
                                          new com.minh.scheduler.client.Configuration());
        assertEquals("Accepted " + clientId2, client2.sendRequest());

        //Sleep for 20s to make sure server has enough time to run both client jobs.
        Utils.sleep(Duration.ofMillis(20000));

        //Retrieve all jobs from our scheduler to do the assertions
        Map<String, Job> jobs = scheduler.jobStates();
        assertNotNull(jobs.get(clientId1));
        assertNotNull(jobs.get(clientId2));

        assertTrue(jobs.get(clientId1).context.numRuns > 0);
        assertTrue(jobs.get(clientId2).context.numRuns > 0);

        client1.shutdown();
        client2.shutdown();
        grpcServerRule.getServer().shutdown();
    }
}
