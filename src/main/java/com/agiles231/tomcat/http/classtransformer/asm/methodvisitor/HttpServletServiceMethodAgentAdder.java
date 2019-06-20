package com.agiles231.tomcat.http.classtransformer.asm.methodvisitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HttpServletServiceMethodAgentAdder extends MethodVisitor implements Opcodes {
    public HttpServletServiceMethodAgentAdder(int api, MethodVisitor mv) {
        super(api, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitMethodInsn(INVOKESTATIC, "com/agiles231/tomcat/http/agent/TomcatHttpMonitoringAgent", "notifyRequestStart", "()Ljava/lang/Long;", false);
        super.visitInsn(DUP);
        super.visitVarInsn(ALOAD, 2);
        super.visitInsn(SWAP);
        super.visitLdcInsn("Monitoring-Agent-Id");
        super.visitInsn(SWAP);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "toString", "()Ljava/lang/String;", false);
        super.visitMethodInsn(INVOKEINTERFACE, "javax/servlet/http/HttpServletResponse", "addHeader", "(Ljava/lang/String;Ljava/lang/String;)V", true);
        super.visitVarInsn(ALOAD, 2);
        super.visitInsn(SWAP);
        super.visitMethodInsn(INVOKEINTERFACE, "com/agiles231/tomcat/http/agent/interfaces/AgentIdContainer", "setAgentId", "(Ljava/lang/Long;)V", true);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 4), maxLocals);
    }
}
