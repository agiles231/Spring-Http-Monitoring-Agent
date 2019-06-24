package com.agiles231.tomcat.http.classtransformer.asm.methodvisitor;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Requirements:
 * 1) visited class must implement AgentIdContainer
 * 2) local 1 of method must of type byte[]
 *
 * Calls writeBytes on agent with id on this object and bytes arg from local 1
 */
public class WriteBytesAgentCallAdder extends MethodVisitor implements Opcodes {
    public WriteBytesAgentCallAdder(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitVarInsn(ALOAD, 0);
        super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "getAgentId", "()Ljava/lang/Long;", true);
        super.visitVarInsn(ALOAD, 1);
        super.visitMethodInsn(INVOKESTATIC, "com/agiles231/tomcat/http/agent/TomcatHttpMonitoringAgent", "writeBytes", "(Ljava/lang/Long;[B)V", false);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 2), maxLocals);
    }
}
