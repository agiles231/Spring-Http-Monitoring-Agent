package com.agiles231.tomcat.http.agent;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TomcatHttpMonitoringAgentTest {

    @Test
    public void testAgentTiming() throws InterruptedException {
        TomcatHttpMonitoringAgent.init(); // static init must be called before class is used
        long startTime = System.currentTimeMillis();
        Long id = TomcatHttpMonitoringAgent.notifyRequestStart();
        Thread.sleep(450l); // so time is non-zero
        long endTime = System.currentTimeMillis();
        TomcatHttpMonitoringAgent.notifyRequestEnd(id);
        long recordedTime = TomcatHttpMonitoringAgent.getHttpRequestTime(id);
        long actualTime = (endTime - startTime);
        System.out.println("Recorded time" + recordedTime);
        System.out.println("Actual time" + actualTime);
        Assert.assertTrue(Math.abs(recordedTime - actualTime) < 50); // within 2 milliseconds
    }

    @Test
    public void testAgentSizeComputation() {
        TomcatHttpMonitoringAgent.init();
        Long id = 1l;
        Random r = new Random();
        byte[] bytes;
        long sum = 0l;
        int maxIterations = r.nextInt(50);
        for (int i = 0; i < maxIterations; i++) {
            bytes = new byte[Math.abs(r.nextInt()) % 1024];
            System.out.println("bytes.length: " + bytes.length); // output this in case a specific number ends up causing test failures
            sum += bytes.length;
            TomcatHttpMonitoringAgent.writeBytes(id, bytes);
            long size = TomcatHttpMonitoringAgent.getHttpResponseSize(id);
            Assert.assertEquals(sum, size);
        }
    }

}
