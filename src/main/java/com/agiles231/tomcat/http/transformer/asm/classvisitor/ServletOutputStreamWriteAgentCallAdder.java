package com.agiles231.tomcat.http.transformer.asm.classvisitor;

import com.agiles231.tomcat.http.transformer.asm.methodvisitor.WriteByteAgentCallAdder;
import com.agiles231.tomcat.http.transformer.asm.methodvisitor.WriteBytesAgentCallAdder;
import com.agiles231.tomcat.http.transformer.asm.methodvisitor.WriteBytesOffLenAgentCallAdder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

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
