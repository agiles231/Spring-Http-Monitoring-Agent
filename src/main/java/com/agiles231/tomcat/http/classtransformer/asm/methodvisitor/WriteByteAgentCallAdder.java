package com.agiles231.tomcat.http.classtransformer.asm.methodvisitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class WriteByteAgentCallAdder extends MethodVisitor implements Opcodes {
    public WriteByteAgentCallAdder(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitVarInsn(ALOAD, 0);
        super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "getAgentId", "()Ljava/lang/Long;", true);
        super.visitInsn(ICONST_1);
        super.visitMethodInsn(INVOKESTATIC, "com/agiles231/tomcat/http/agent/TomcatHttpMonitoringAgent", "writeNumBytes", "(Ljava/lang/Long;I)V", false);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 2), maxLocals);
    }
}
