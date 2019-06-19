package com.agiles231.tomcat.http.agent;

import java.lang.instrument.Instrumentation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.agiles231.tomcat.http.transformer.ControllerTransformer;
import com.agiles231.tomcat.http.transformer.HttpServletResponseTransformer;
import com.agiles231.tomcat.http.transformer.HttpServletTransformer;
import com.agiles231.tomcat.http.transformer.ServletOutputStreamTransformer;

public class TomcatHttpMonitoringAgent {

    private static Long currentId;
    private static Map<Long, Long> requestStarts;
    private static Map<Long, Long> requestEnds;
    private static Map<Long, Long> responseSizes;
    private static Lock requestStartsLock;
    private static Lock requestEndsLock;
    private static Lock responseSizesLock;

    public synchronized static void init() {
        if (currentId == null) {
            currentId = 0l;
        }
        if (requestStarts == null) {
            requestStarts = new HashMap<>();
            requestStartsLock = new ReentrantLock(true);
        }
        if (requestEnds == null) {
            requestEnds  = new HashMap<>();
            requestEndsLock = new ReentrantLock(true);
        }
        if (responseSizes == null) {
            responseSizes = new HashMap<>();
            responseSizesLock = new ReentrantLock(true);
        }
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent successfully loaded");
        init();
        inst.addTransformer(new HttpServletTransformer());
        inst.addTransformer(new ServletOutputStreamTransformer());
        inst.addTransformer(new HttpServletResponseTransformer());
        inst.addTransformer(new ControllerTransformer());
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        throw new UnsupportedOperationException("This method is not yet supported. Attach to process statically.");
    }

    public static Long notifyRequestStart() {
        Long timeStart = System.currentTimeMillis();
        requestStartsLock.lock();
        Long id = currentId++;
        requestStarts.put(id, timeStart);
        requestStartsLock.unlock();
        return id;
    }
    public static void notifyRequestEnd(Long id) {
        Long timeEnd = System.currentTimeMillis();
        requestEndsLock.lock();
        requestEnds.merge(id, timeEnd, (v1, v2) -> Math.max(v1, v2));
        requestEndsLock.unlock();
        //System.out.println("Total time for " + id + " request: " + getHttpRequestTime(id));
        //System.out.println("Total size for " + id + " response so far: " + getHttpResponseSize(id));
    }

    public static void writeNumBytes(Long id, int num) {
        responseSizesLock.lock();
        responseSizes.merge(id, (long)num, (v1, v2) -> v1 + v2);
        responseSizesLock.unlock();
    }
    public static void writeBytes(Long id, byte[] b) {
        responseSizesLock.lock();
        responseSizes.merge(id, Long.valueOf(b.length), (v1, v2) -> v1 + v2);
        responseSizesLock.unlock();
    }

    public static Map<Long, Long> getHttpRequestStarts() {
        requestStartsLock.lock();
        Map<Long, Long> ret = new HashMap<>(requestStarts);
        requestStartsLock.unlock();
        return ret;
    }
    public static Map<Long, Long> getHttpRequestEnds() {
        requestEndsLock.lock();
        Map<Long, Long> ret = new HashMap<>(requestEnds);
        requestEndsLock.unlock();
        return ret;
    }
    public static Map<Long, Long> getRequestTimes() {
        requestStartsLock.lock();
        requestEndsLock.lock();
        Map<Long, Long> ret = requestStarts.entrySet().stream()
        		.filter(e -> requestEnds.get(e) != null)
        		.collect(Collectors.toMap(Map.Entry::getKey, e -> {
				   return requestEnds.get(e.getKey()) - e.getValue() ;
				}));
        requestEndsLock.unlock();
        requestStartsLock.unlock();
        return ret;
    }

    public static Map<Long, Long> getHttpResponseSizes() {
        responseSizesLock.lock();
        Map<Long, Long> ret = new HashMap<>(responseSizes);
        responseSizesLock.unlock();
        return ret;
    }
    public static Long getHttpRequestStart(Long id) {
        requestStartsLock.lock();
        Long start = requestStarts.get(id);
        requestStartsLock.unlock();
        return start;
    }
    public static Long getHttpRequestEnd(Long id) {
        requestEndsLock.lock();
        Long end = requestEnds.get(id);
        requestEndsLock.unlock();
        return end;
    }
    public static Long getHttpRequestTime(Long id) {
        Long time = null;
        if (getHttpRequestStart(id) != null && getHttpRequestEnd(id) != null) {
			time = getHttpRequestEnd(id) - getHttpRequestStart(id);
        }
        return time;
    }
    public static Long getHttpResponseSize(Long id) {
        responseSizesLock.lock();
        Long size = responseSizes.get(id);
        responseSizesLock.unlock();
        return size;
    }
}
