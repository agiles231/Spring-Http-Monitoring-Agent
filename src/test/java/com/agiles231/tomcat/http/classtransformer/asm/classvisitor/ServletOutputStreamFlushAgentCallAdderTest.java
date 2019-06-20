package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;

import com.agiles231.tomcat.http.agent.TomcatHttpMonitoringAgent;
import com.agiles231.tomcat.http.classtransformer.asm.classload.ByteClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ServletOutputStreamFlushAgentCallAdderTest {

    @Test
    public void testFlushNotifiesAgent() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, IOException, InterruptedException {
        TomcatHttpMonitoringAgent.init();
        String testClassPath = "test-resources/MockServletOutputStreamAgentIdContainer.class";
        String oldClassName = "mock.MockServletOutputStreamAgentIdContainer";
        String className = oldClassName + "2";
        String oldInternalClassName = oldClassName.replace(".", "/");
        String internalClassName = className.replace(".", "/");
        byte[] inputBytes = Files.readAllBytes(new File(testClassPath).toPath());
        ClassReader reader = new ClassReader(inputBytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new ServletOutputStreamFlushAgentCallAdder(Opcodes.ASM7, writer);
        SimpleRemapper remapper = new SimpleRemapper(oldInternalClassName, internalClassName);
        ClassRemapper classRemapper = new ClassRemapper(visitor, remapper);
        ClassVisitor abstractClassRemover = new AbstractClassModifierRemover(Opcodes.ASM7, classRemapper);
        reader.accept(abstractClassRemover, 0);
        byte[] outputBytes = writer.toByteArray();
        Map<String, byte[]> classes = new HashMap<>();
        classes.put(className, outputBytes);
        ClassLoader loader = new ByteClassLoader(Thread.currentThread().getContextClassLoader(), classes);
        Class<?> servletOutputStreamClazz = loader.loadClass(className);
        Object servletOutputStream = servletOutputStreamClazz.getDeclaredConstructors()[0].newInstance();
        Long id = TomcatHttpMonitoringAgent.notifyRequestStart();
        Method setId = servletOutputStreamClazz.getMethod("setAgentId", Long.class);
        setId.invoke(servletOutputStream, id);


        Method flush = servletOutputStreamClazz.getMethod("flush");
        Thread.sleep(100l); // add some time so value will definitely be non-zero
        flush.invoke(servletOutputStream);
        Long time = TomcatHttpMonitoringAgent.getHttpRequestTime(id);
        Assert.assertNotNull(time);
        Assert.assertNotEquals(0, time.longValue());
    }
}
