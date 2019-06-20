package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;


import org.objectweb.asm.ClassVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InterfaceExtractor extends ClassVisitor {
    List<String> interfaces;
    public InterfaceExtractor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.interfaces = Arrays.asList(interfaces);
    }

    public List<String> getInterfaces() {
        return Collections.unmodifiableList(interfaces);
    }
}
