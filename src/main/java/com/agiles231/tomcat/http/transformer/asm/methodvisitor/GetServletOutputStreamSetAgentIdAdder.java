package com.agiles231.tomcat.http.transformer.asm.methodvisitor;

import com.agiles231.tomcat.http.transformer.HttpServletResponseTransformer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
