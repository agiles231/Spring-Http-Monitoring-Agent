package com.agiles231.tomcat.http.transformer.asm.classvisitor;


import com.agiles231.tomcat.http.transformer.asm.methodvisitor.HttpServletServiceMethodAgentAdder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class HttpServletServiceAgentAdder extends ClassVisitor {
    public HttpServletServiceAgentAdder(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && name.equals("service") && desc.equals("(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V")) {
            mv = new HttpServletServiceMethodAgentAdder(this.api, mv);
        }
        return mv;

    }

}
