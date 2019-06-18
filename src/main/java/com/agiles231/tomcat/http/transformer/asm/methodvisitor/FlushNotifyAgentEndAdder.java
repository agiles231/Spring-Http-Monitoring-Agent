package com.agiles231.tomcat.http.transformer.asm.methodvisitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FlushNotifyAgentEndAdder extends MethodVisitor implements Opcodes {
    public FlushNotifyAgentEndAdder(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == RETURN) {
            super.visitVarInsn(ALOAD, 0);
            super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "getAgentId", "()Ljava/lang/Long;", true);
            super.visitMethodInsn(INVOKESTATIC, "com/agiles231/tomcat/http/agent/TomcatHttpMonitoringAgent", "notifyRequestEnd", "(Ljava/lang/Long;)V", false);
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 2), maxLocals);
    }
}
