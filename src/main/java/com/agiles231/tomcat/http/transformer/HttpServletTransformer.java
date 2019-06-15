package com.agiles231.tomcat.http.transformer;

import org.objectweb.asm.ClassVisitor;

import javax.servlet.http.HttpServletResponse;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class HttpServletTransformer implements ClassFileTransformer {

    public String getHttpServletClassName() {
        return "javax/servlet/http/HttpServlet";
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // TODO: implement meaningful code!
        return classfileBuffer;
    }
}
