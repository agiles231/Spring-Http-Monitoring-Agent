package com.agiles231.tomcat.http.transformer.asm.classvisitor;

import com.agiles231.tomcat.http.transformer.asm.methodvisitor.GetServletOutputStreamSetAgentIdAdder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

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
