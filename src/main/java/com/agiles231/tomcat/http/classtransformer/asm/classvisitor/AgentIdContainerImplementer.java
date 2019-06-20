package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;

import com.agiles231.tomcat.http.agent.interfaces.AgentIdContainer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class AgentIdContainerImplementer extends ClassVisitor implements Opcodes {
    private final String fieldName;
    private final String interfaceName;
    private final String className;
    public AgentIdContainerImplementer(int i, ClassVisitor classVisitor, String className, String fieldName) {
        super(i, classVisitor);
        this.fieldName = fieldName;
        this.className = className;
        this.interfaceName = AgentIdContainer.class.getName().replace(".", "/");
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        int interfacesLength = interfaces.length;
        String[] modifiedInterfaces = new String[interfacesLength + 1];
        for (int i = 0; i < interfaces.length; i++) {
            modifiedInterfaces[i] = interfaces[i];
        }
        modifiedInterfaces[interfacesLength] = getInterfaceName();
        super.visit(version, access, name, signature, superName, modifiedInterfaces);
        super.visitField(ACC_PRIVATE, getFieldName(), "Ljava/lang/Long;", null, null).visitEnd();
    }

    public String getFieldName() {
        return fieldName;
    }
    public String getInterfaceName() {
        return interfaceName;
    }
    public String getGetterName() {
        return "getAgentId";
    }
    public String getSetterName() {
        return "setAgentId";
    }
    public void visitEnd() {
        MethodNode getterNode = new MethodNode(this.api, ACC_PUBLIC, getGetterName(), "()Ljava/lang/Long;", null, null);
        getterNode.visitAnnotationDefault();
        getterNode.visitCode();
        getterNode.visitVarInsn(ALOAD, 0);
        getterNode.visitFieldInsn(GETFIELD, className, getFieldName(), "Ljava/lang/Long;");
        getterNode.visitInsn(ARETURN);
        getterNode.visitMaxs(1, 1);
        getterNode.visitEnd();
        MethodVisitor mv = super.visitMethod(ACC_PUBLIC, getGetterName(), "()Ljava/lang/Long;", null, null);
        getterNode.accept(mv);
        MethodNode setterNode = new MethodNode(this.api, ACC_PUBLIC, getSetterName(), "(Ljava/lang/Long;)V", null, null);
        setterNode.visitParameter(null, 0);
        setterNode.visitAnnotationDefault();
        setterNode.visitCode();
        setterNode.visitVarInsn(ALOAD, 0);
        setterNode.visitVarInsn(ALOAD, 1);
        setterNode.visitFieldInsn(PUTFIELD, className, getFieldName(), "Ljava/lang/Long;");
        setterNode.visitInsn(RETURN);
        setterNode.visitMaxs(2, 2);
        setterNode.visitEnd();
        mv = super.visitMethod(ACC_PUBLIC, getSetterName(), "(Ljava/lang/Long;)V", null, null);
        setterNode.accept(mv);
        super.visitEnd();
    }
}
