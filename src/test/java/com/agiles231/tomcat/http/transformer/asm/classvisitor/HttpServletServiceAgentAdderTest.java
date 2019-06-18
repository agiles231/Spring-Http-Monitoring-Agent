package com.agiles231.tomcat.http.transformer.asm.classvisitor;

import com.agiles231.tomcat.http.agent.TomcatHttpMonitoringAgent;
import com.agiles231.tomcat.http.agent.interfaces.AgentIdContainer;
import com.agiles231.tomcat.http.transformer.asm.classload.ByteClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpServletServiceAgentAdderTest {

    @Test
    public void testAddsAgentCallAndHeaderAddAndIdAdd() throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        TomcatHttpMonitoringAgent.init();
        String testClassPath = "test-resources/HttpServlet.class";
        String oldClassName = "javax.servlet.http.HttpServlet";
        String className = oldClassName + "2";
        String oldInternalClassName = oldClassName.replace(".", "/");
        String internalClassName = className.replace(".", "/");
        byte[] inputBytes = Files.readAllBytes(new File(testClassPath).toPath());
        ClassReader reader = new ClassReader(inputBytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new HttpServletServiceAgentAdder(Opcodes.ASM5, writer);
        SimpleRemapper remapper = new SimpleRemapper(oldInternalClassName, internalClassName);
        ClassRemapper classRemapper = new ClassRemapper(visitor, remapper);
        ClassVisitor abstractClassRemover = new AbstractClassModifierRemover(Opcodes.ASM5, classRemapper);
        reader.accept(abstractClassRemover, 0);
        byte[] outputBytes = writer.toByteArray();
        Map<String, byte[]> classes = new HashMap<>();
        classes.put(className, outputBytes);
        ClassLoader loader = new ByteClassLoader(Thread.currentThread().getContextClassLoader(), classes);
        loader.loadClass(className);
        Class<?> httpServletClazz = loader.loadClass(className);
        Method service = httpServletClazz.getMethod("service", loader.loadClass("javax.servlet.ServletRequest"), loader.loadClass("javax.servlet.ServletResponse"));
        Class<?> httpServletRequestClazz = loader.loadClass("mock.MockHttpServletRequest");
        Class<?> httpServletResponseClazz = loader.loadClass("mock.MockHttpServletResponseAgentIdContainer");
        Object request = httpServletRequestClazz.getDeclaredConstructors()[0].newInstance();
        Object response = httpServletResponseClazz.getDeclaredConstructors()[0].newInstance();
        service.invoke(httpServletClazz.getDeclaredConstructors()[0].newInstance(), request, response);
        Map<Long, Long> times = TomcatHttpMonitoringAgent.getHttpRequestStarts();
        Assert.assertFalse(times.isEmpty());

        Method containsHeader =httpServletResponseClazz.getMethod("containsHeader", String.class);
        Assert.assertTrue((boolean)containsHeader.invoke(response, "Monitoring-Agent-Id"));

        AgentIdContainer container = (AgentIdContainer )response;
        Long id = container.getAgentId();
        Assert.assertNotNull(id);
    }
}


