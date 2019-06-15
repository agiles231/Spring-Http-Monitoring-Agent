package com.agiles.tomcat.http.agent;

import com.agiles231.tomcat.http.agent.TomcatHttpMonitoringAgent;
import org.junit.Assert;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
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
        Assert.assertTrue(Math.abs(recordedTime - actualTime) < 100); // within 2 milliseconds
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
