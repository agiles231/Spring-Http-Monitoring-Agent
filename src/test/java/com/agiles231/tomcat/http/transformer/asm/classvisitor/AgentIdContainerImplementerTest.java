package com.agiles231.tomcat.http.transformer.asm.classvisitor;

import com.agiles231.tomcat.http.agent.interfaces.AgentIdContainer;
import com.agiles231.tomcat.http.transformer.asm.classload.ByteClassLoader;
import com.agiles231.tomcat.http.transformer.asm.classvisitor.AgentIdContainerImplementer;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AgentIdContainerImplementerTest {

    @Test
    public void checkClassImplementsInterface() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        String testClassPath = "test-resources/Test.class";
        String oldClassName = "mock.Test";
        String className = oldClassName + "2";
        String oldInternalClassName = oldClassName.replace(".", "/");
        String internalClassName = className.replace(".", "/");
        byte[] inputBytes = Files.readAllBytes(new File(testClassPath).toPath());
        ClassReader reader = new ClassReader(inputBytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new AgentIdContainerImplementer(Opcodes.ASM5, writer, internalClassName, "agentId");
        SimpleRemapper remapper = new SimpleRemapper(oldInternalClassName, internalClassName);
        ClassRemapper classRemapper = new ClassRemapper(visitor, remapper);
        reader.accept(classRemapper, 0);
        byte[] outputBytes = writer.toByteArray();
        reader = new ClassReader(outputBytes);
        writer = new ClassWriter(reader, 0);
        CheckClassAdapter checker = new CheckClassAdapter(writer);
        reader.accept(checker, 0);

        Map<String, byte[]> classes = new HashMap<>();
        classes.put(className, outputBytes);
        ClassLoader loader = new ByteClassLoader(this.getClass().getClassLoader(), classes);
        Class clazz = loader.loadClass(className);
        AgentIdContainer container = (AgentIdContainer)clazz.getDeclaredConstructors()[0].newInstance();
        Random r = new Random();
        Long l = r.nextLong();
        container.setAgentId(l);
        Assert.assertEquals(l, container.getAgentId());
    }


}
