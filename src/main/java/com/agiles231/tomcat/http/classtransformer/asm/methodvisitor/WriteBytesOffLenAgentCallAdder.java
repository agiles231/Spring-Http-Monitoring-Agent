package com.agiles231.tomcat.http.classtransformer.asm.methodvisitor;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Requirements:
 * 1) visited class must implement AgentIdContainer
 * 2) local 3 must int (semantically, should represent the number of bytes being written)
 *
 * Adds writeNumBytes call with id on this object and length from local 3
 * Intended to be used on OutputStream method "void write(byte[] b, int off, int len)"
 */
public class WriteBytesOffLenAgentCallAdder extends MethodVisitor implements Opcodes {
    public WriteBytesOffLenAgentCallAdder(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitVarInsn(ALOAD, 0);
        super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "getAgentId", "()Ljava/lang/Long;", true);
        super.visitVarInsn(ILOAD, 3);
        super.visitMethodInsn(INVOKESTATIC, "com/agiles231/tomcat/http/agent/TomcatHttpMonitoringAgent", "writeNumBytes", "(Ljava/lang/Long;I)V", false);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 3), maxLocals);
    }
}
