package com.agiles231.tomcat.http.classtransformer;

import com.agiles231.tomcat.http.classtransformer.asm.classvisitor.HttpServletServiceAgentAdder;
import com.agiles231.tomcat.http.classtransformer.asm.classvisitor.InterfaceExtractor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


/**
 * Find HttpServlet object and modify by changing service method in 2 ways:
 * 1) Call notifyRequestStart on the agent
 * 2) Using id returned by above method call, tuck in HttpServletResponse (parameter 2 of service method)
 */
public class HttpServletTransformer implements ClassFileTransformer {

    public String getHttpServletClassName() {
        return "javax/servlet/http/HttpServlet";
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, 0);
        InterfaceExtractor extractor = new InterfaceExtractor(Opcodes.ASM7, writer);
        reader.accept(extractor, 0);
        boolean containsHttpServlet = extractor.getInterfaces().contains(getHttpServletClassName());
        if (className.equals(getHttpServletClassName()) || containsHttpServlet) {
            writer = new ClassWriter(reader, 0);
            ClassVisitor serviceAgentAdder = new HttpServletServiceAgentAdder(Opcodes.ASM7, writer);
            reader.accept(serviceAgentAdder, 0);
            classfileBuffer = writer.toByteArray();
        }
        return classfileBuffer;
    }
}
