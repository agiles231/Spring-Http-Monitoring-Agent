package com.agiles231.tomcat.http.classtransformer.asm.methodvisitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Requirements:
 * 1) visited class must implement AgentIdContainer
 * 2) returned object of method must implement AgentIdContainer
 *
 * Transfers agentId from this object to returned object from method
 */
public class GetServletOutputStreamSetAgentIdAdder extends MethodVisitor implements Opcodes {
    public GetServletOutputStreamSetAgentIdAdder(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == ARETURN) {
            super.visitInsn(DUP);
            super.visitVarInsn(ALOAD, 0);
            super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "getAgentId", "()Ljava/lang/Long;", true);
            super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "setAgentId", "(Ljava/lang/Long;)V", true);
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 2, maxLocals);
    }
}
