package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;

import com.agiles231.tomcat.http.classtransformer.asm.methodvisitor.GetServletOutputStreamSetAgentIdAdder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Class visited must meet criteria:
 * 1) be a HttpServletResponse object
 * 2) have AgentIdContainer implemented
 *
 * ServletOutputStream must meet criteria:
 * 1) have AgentIdContainer implemented
 *
 * Class visited will transfer id on object to ServletOutputStream object returned by getOutputStream method
 */
public class HttpServletResponseGetServletOutputStreamAgentIdSetAdder extends AgentIdContainerImplementer  {
    public HttpServletResponseGetServletOutputStreamAgentIdSetAdder(int i, ClassVisitor classVisitor, String className, String fieldName) {
        super(i, classVisitor, className, fieldName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("getOutputStream") && descriptor.equals("()Ljavax/servlet/ServletOutputStream;")) {
            mv = new GetServletOutputStreamSetAgentIdAdder(this.api, mv);
        }
        return mv;
    }
}
