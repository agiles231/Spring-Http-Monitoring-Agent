package com.agiles231.tomcat.http.classtransformer.asm.classvisitor;


import com.agiles231.tomcat.http.classtransformer.asm.annotationvisitor.RequestMappingAnnotationVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Optional;


public class ControllerRequestMappingAnnotationExtractor extends ClassVisitor implements Opcodes {
    boolean isController;
    boolean isAnnotation;
    Optional<RequestMappingAnnotationVisitor> requestMappingAnnotationVisitor;
    public ControllerRequestMappingAnnotationExtractor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.requestMappingAnnotationVisitor = Optional.empty();
        this.isController = false;
        this.isAnnotation = false;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if ((access & ACC_ANNOTATION) == ACC_ANNOTATION) {
            isAnnotation = true;
        }
    }

    private String getRequestMappingDescriptor() {
        return "Lorg/springframework/web/bind/annotation/RequestMapping;";
    }
    private String getControllerDescriptor() {
        return "Lorg/springframework/stereotype/Controller;";
    }
    private String getRestControllerDescriptor() {
        return "Lorg/springframework/web/bind/annotation/RestController;";
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
        if (!isAnnotation) {
            if (descriptor.equals(getRequestMappingDescriptor())) {
                this.requestMappingAnnotationVisitor = Optional.of(new RequestMappingAnnotationVisitor(this.api, av));
                av = this.requestMappingAnnotationVisitor.get();
            } else if (descriptor.equals(getControllerDescriptor())) {
                this.isController = true;
            } else if (descriptor.equals(getRestControllerDescriptor())) {
                this.isController = true;
            }
        }
        return av;
    }

    public boolean isController() {
        return isController;
    }
    public Optional<List<String>> getPaths() {
        return requestMappingAnnotationVisitor.map(r -> r.getPaths());
    }
}
