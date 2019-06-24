package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;

import com.agiles231.tomcat.http.classtransformer.asm.methodvisitor.WriteByteAgentCallAdder;
import com.agiles231.tomcat.http.classtransformer.asm.methodvisitor.WriteBytesAgentCallAdder;
import com.agiles231.tomcat.http.classtransformer.asm.methodvisitor.WriteBytesOffLenAgentCallAdder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Visited class must meet criteria:
 * 1) Extend OutputStream
 * 2) Implement AgentIdContainer
 */
public class ServletOutputStreamWriteAgentCallAdder extends ClassVisitor {
    public ServletOutputStreamWriteAgentCallAdder(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("write") && descriptor.equals("([B)V")) {
            mv = new WriteBytesAgentCallAdder(this.api, mv);
        } else if (name.equals("write") && descriptor.equals("([BII)V")) {
            mv = new WriteBytesOffLenAgentCallAdder(this.api, mv);
        } else if (name.equals("write") && descriptor.equals("(I)V")) {
            mv = new WriteByteAgentCallAdder(this.api, mv);
        }
        return mv;
    }
}
