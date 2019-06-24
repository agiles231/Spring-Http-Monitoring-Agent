package com.agiles231.tomcat.http.classtransformer.asm.annotationvisitor;


import org.objectweb.asm.AnnotationVisitor;

import java.util.LinkedList;
import java.util.List;

/**
 * Extracts the value of @RequestMapping annotation if present
 */
public class RequestMappingAnnotationVisitor extends AnnotationVisitor {
    RequestMappingArrayAnnotationVisitor av;
    public RequestMappingAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor av = super.visitArray(name);
        if (name.equals("value") || name.equals("path")) {
            this.av = new RequestMappingArrayAnnotationVisitor(this.api, av);
            av = this.av;
        }
        return av;
    }


    /**
     *
     * @return list of all paths from annotation ("value" or "path")
     */
    public List<String> getPaths() {
        if (av != null) {
            System.out.println("Found request mapping: " + av.getPaths().toString());
            return av.getPaths();
        }
        return new LinkedList<>();
    }
}

/**
 * Helps extract from the array
 */
class RequestMappingArrayAnnotationVisitor extends AnnotationVisitor {
    List<String> paths;
    public RequestMappingArrayAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor);
        this.paths = new LinkedList<>();
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
        paths.add(value.toString());
    }
    public List<String> getPaths() {
        return paths;
    }
}
