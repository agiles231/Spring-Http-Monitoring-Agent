package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;


import org.objectweb.asm.ClassVisitor;

/**
 * Extracts super class of visited class
 */
public class SuperClassExtractor extends ClassVisitor {
    String superClass;
    public SuperClassExtractor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.superClass = superName;
    }

    /**
     *
     * @return superclass name extracted during visit
     */
    public String getSuperClass() {
        return superClass;
    }
}
