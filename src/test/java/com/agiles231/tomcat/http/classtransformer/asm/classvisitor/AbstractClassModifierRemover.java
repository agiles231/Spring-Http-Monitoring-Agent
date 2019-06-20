package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;


import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class AbstractClassModifierRemover extends ClassVisitor {
    public AbstractClassModifierRemover(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access & ~Opcodes.ACC_ABSTRACT, name, signature, superName, interfaces);
    }
}
