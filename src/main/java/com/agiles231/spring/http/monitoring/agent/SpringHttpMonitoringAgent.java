package com.agiles231.spring.http.monitoring.agent;

import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.Instrumentation;

public class SpringHttpMonitoringAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent successfully loaded");
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        throw new UnsupportedOperationException("This method is not yet supported. Attach to process statically.");
    }
}
