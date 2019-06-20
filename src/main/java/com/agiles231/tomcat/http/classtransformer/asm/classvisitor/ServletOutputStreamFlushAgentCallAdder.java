package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;


import com.agiles231.tomcat.http.classtransformer.asm.methodvisitor.FlushNotifyAgentEndAdder;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class ServletOutputStreamFlushAgentCallAdder extends ClassVisitor implements Opcodes {
    boolean foundFlush;
    public ServletOutputStreamFlushAgentCallAdder(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.foundFlush = false;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals("flush") && descriptor.equals("()V")) {
            foundFlush = true;
            mv = new FlushNotifyAgentEndAdder(this.api, mv);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!foundFlush) {

            MethodNode flushNode = new MethodNode(this.api, ACC_PUBLIC, "flush", "()V", null, new String[] {"java/io/IOException"});
            flushNode.visitAnnotationDefault();
            flushNode.visitCode();
            flushNode.visitVarInsn(ALOAD, 0);
            flushNode.visitMethodInsn(INVOKESPECIAL, "java/io/OutputStream", "flush", "()V", false);
            flushNode.visitInsn(RETURN);
            flushNode.visitMaxs(1, 1);
            flushNode.visitEnd();
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "flush", "()V", null, new String[] {"java/io/IOException"});
            mv = new FlushNotifyAgentEndAdder(this.api, mv);
            flushNode.accept(mv);
        }
        super.visitEnd();
    }
}
