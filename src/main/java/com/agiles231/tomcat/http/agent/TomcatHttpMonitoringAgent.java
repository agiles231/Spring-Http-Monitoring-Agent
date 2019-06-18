package com.agiles231.tomcat.http.agent;

import com.agiles231.tomcat.http.transformer.HttpServletResponseTransformer;
import com.agiles231.tomcat.http.transformer.HttpServletTransformer;
import com.agiles231.tomcat.http.transformer.ServletOutputStreamTransformer;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TomcatHttpMonitoringAgent {

    private static Long currentId;
    private static Map<Long, Long> requestStarts;
    private static Map<Long, Long> requestEnds;
    private static Map<Long, Long> responseSizes;

    public synchronized static void init() {
        if (currentId == null) {
            currentId = 0l;
        }
        if (requestStarts == null) {
            requestStarts = new HashMap<>();
        }
        if (requestEnds == null) {
            requestEnds  = new HashMap<>();
        }
        if (responseSizes == null) {
            responseSizes = new HashMap<>();
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent successfully loaded");
        init();
        inst.addTransformer(new HttpServletTransformer());
        inst.addTransformer(new ServletOutputStreamTransformer());
        inst.addTransformer(new HttpServletResponseTransformer());
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        throw new UnsupportedOperationException("This method is not yet supported. Attach to process statically.");
    }

    public synchronized static Long notifyRequestStart() {
        Long timeStart = System.currentTimeMillis();
        Long id = currentId++;
        requestStarts.put(id, timeStart);
        return id;
    }
    public synchronized static void notifyRequestEnd(Long id) {
        Long timeEnd = System.currentTimeMillis();
        requestEnds.merge(id, timeEnd, (v1, v2) -> v1);
        System.out.println("Total time for this request: " + getHttpRequestTime(id));
    }

    public synchronized static void setContentLength(Long id, int length) {
        responseSizes.put(id, Long.valueOf(length));
    }

    public synchronized static void writeByte(Long id) {
        responseSizes.merge(id, 1l, (v1, v2) -> v1 + v2);
    }
    public synchronized static void writeBytes(Long id, byte[] b) {
        responseSizes.merge(id, Long.valueOf(b.length), (v1, v2) -> v1 + v2);
    }

    public static synchronized Map<Long, Long> getHttpRequestStarts() {
        return requestStarts;
    }
    public static synchronized Map<Long, Long> getHttpRequestEnds() {
        return requestEnds;
    }
    public static synchronized Map<Long, Long> getRequestTimes() {
        return requestStarts.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
           return requestEnds.get(e.getKey()) - e.getValue() ;
        }));
    }

    public static synchronized Map<Long, Long> getHttpResponseSizes() {
        return responseSizes;
    }
    public static synchronized Long getHttpRequestTime(Long id) {
        return (requestEnds.get(id) - requestStarts.get(id));
    }
    public static synchronized Long getHttpResponseSize(Long id) {
        return responseSizes.get(id);
    }
}
