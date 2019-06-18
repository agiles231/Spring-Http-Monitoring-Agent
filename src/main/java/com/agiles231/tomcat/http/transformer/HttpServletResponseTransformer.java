package com.agiles231.tomcat.http.transformer;

import com.agiles231.tomcat.http.transformer.asm.classvisitor.HttpServletResponseGetServletOutputStreamAgentIdSetAdder;
import com.agiles231.tomcat.http.transformer.asm.classvisitor.InterfaceExtractor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class HttpServletResponseTransformer implements ClassFileTransformer {
    public static String className;
    public String getHttpServletResponseClassName() {
        return "javax/servlet/http/HttpServletResponse";
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, 0);
        InterfaceExtractor extractor = new InterfaceExtractor(Opcodes.ASM7, writer);
        reader.accept(extractor, 0);
        boolean containsHttpServlet = extractor.getInterfaces().contains(getHttpServletResponseClassName());
        if (containsHttpServlet) {
            this.className = className;
            writer = new ClassWriter(reader, 0);
            ClassVisitor httpServletResponseModifier = new HttpServletResponseGetServletOutputStreamAgentIdSetAdder(Opcodes.ASM7, writer, className, "agent_id");
            reader.accept(httpServletResponseModifier, 0);
            classfileBuffer = writer.toByteArray();
        }
        return classfileBuffer;
    }
}
