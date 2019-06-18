package com.agiles231.tomcat.http.transformer.asm.classvisitor;


import org.objectweb.asm.ClassVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public String getSuperClass() {
        return superClass;
    }
}
