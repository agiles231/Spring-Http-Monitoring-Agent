package com.agiles231.tomcat.http.transformer.asm;

import com.agiles231.tomcat.http.agent.interfaces.AgentIdContainer;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AgentIdContainerImplementerTest {

    @Test
    public void checkClassImplementsInterface() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String testClassPath = "test-resources/TestClass.class";
        String className = "test.TestClass";
        String internalClassName = className.replace(".", "/");
        byte[] inputBytes = Files.readAllBytes(new File(testClassPath).toPath());
        ClassReader reader = new ClassReader(inputBytes);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new AgentIdContainerImplementer(Opcodes.ASM5, writer, internalClassName, "agentId");
        reader.accept(visitor, 0);
        byte[] outputBytes = writer.toByteArray();
        FileOutputStream out = new FileOutputStream("test-resources/TestClassOut.class");
        out.write(outputBytes);
        out.close();
        reader = new ClassReader(outputBytes);
        writer = new ClassWriter(reader, 0);
        CheckClassAdapter checker = new CheckClassAdapter(writer);
        reader.accept(checker, 0);

        Map<String, byte[]> classes = new HashMap<>();
        classes.put(className, outputBytes);
        ClassLoader loader = new ByteClassLoader(this.getClass().getClassLoader(), classes);
        Class clazz = loader.loadClass(className);
        AgentIdContainer container = (AgentIdContainer)clazz.newInstance();
        Random r = new Random();
        Long l = r.nextLong();
        container.setAgentId(l);
        Assert.assertEquals(l, container.getAgentId());
    }


}
// pulled this from https://stackoverflow.com/questions/1781091/java-how-to-load-class-stored-as-byte-into-the-jvm
class ByteClassLoader extends ClassLoader {
    private final Map<String, byte[]> extraClassDefs;

    public ByteClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
        super(parent);
        this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        byte[] classBytes = this.extraClassDefs.remove(name);
        if (classBytes != null) {
            return defineClass(name, classBytes, 0, classBytes.length);
        }
        return super.findClass(name);
    }

}
