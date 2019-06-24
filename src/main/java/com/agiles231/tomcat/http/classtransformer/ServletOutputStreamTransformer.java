package com.agiles231.tomcat.http.classtransformer;

import com.agiles231.tomcat.http.classtransformer.asm.classvisitor.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

/**
 * Modify all objects extending ServletOutputStream in 3 ways:
 * 1) implement AgentIdContainer
 * 2) during call of flush, call notifyRequestEnd on agent using agentId attached to object
 * 3) all calls to write method (all 3 overloads) call agent's corresponding write methods to track bytes written
 */
public class ServletOutputStreamTransformer implements ClassFileTransformer {
    Set<String> subClasses;

    public ServletOutputStreamTransformer() {
        this.subClasses = new HashSet<>();
        subClasses.add(getServletOutputStreamName()); // start seed
    }

    public String getServletOutputStreamName() {
        return "javax/servlet/ServletOutputStream";
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, 0);
        SuperClassExtractor extractor = new SuperClassExtractor(Opcodes.ASM7, writer);
        reader.accept(extractor, 0);
        boolean containsServletOutputStream = false;
        String superClass = extractor.getSuperClass();
        for (String subClass : subClasses) {
            containsServletOutputStream |= superClass.equals(subClass);
        }
        if (className.equals(getServletOutputStreamName()) || containsServletOutputStream) {
            subClasses.add(className);
            writer = new ClassWriter(reader, 0);
            ClassVisitor agentIdContainerImplementer = new AgentIdContainerImplementer(Opcodes.ASM7, writer, className, "agent_id");
            ClassVisitor servletOutputStreamWritePrintModifier = new ServletOutputStreamWriteAgentCallAdder(Opcodes.ASM7, agentIdContainerImplementer);
            ClassVisitor servletOutputStreamFlushModifier = new ServletOutputStreamFlushAgentCallAdder(Opcodes.ASM7, servletOutputStreamWritePrintModifier);
            reader.accept(servletOutputStreamFlushModifier, 0);
            classfileBuffer = writer.toByteArray();
        }
        return classfileBuffer;
    }
}
