package com.agiles231.tomcat.http.transformer;

import com.agiles231.tomcat.http.agent.TomcatHttpMonitoringAgent;
import com.agiles231.tomcat.http.transformer.asm.classvisitor.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

public class ControllerTransformer implements ClassFileTransformer {
    boolean transformedOne;

    public ControllerTransformer() {
        this.transformedOne = false;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!transformedOne) {
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader, 0);
            ControllerRequestMappingAnnotationExtractor extractor = new ControllerRequestMappingAnnotationExtractor(Opcodes.ASM7, writer);
            reader.accept(extractor, 0);
            boolean isController = extractor.isController();
            if (isController) {
                transformedOne = true;
                Optional<List<String>> paths = extractor.getPaths();
                String path = paths.map(p -> p.stream().findFirst().orElse("")).orElse("");
                writer = new ClassWriter(reader, 0);
                ClassVisitor visitor = new ControllerAgentViewMethodAdder(Opcodes.ASM7, writer, path);
                reader.accept(visitor, 0);
                classfileBuffer = writer.toByteArray();
            }
        }
        return classfileBuffer;
    }


    public String getViewAll() {
        StringBuilder sb = new StringBuilder();
        Map<Long, Long> requestStarts = TomcatHttpMonitoringAgent.getHttpRequestStarts();
        Map<Long, Long> requestEnds = TomcatHttpMonitoringAgent.getHttpRequestEnds();
        Map<Long, Long> responseSizes = TomcatHttpMonitoringAgent.getHttpResponseSizes ();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append("Id");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("Start (epoch)");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("End (epoch)");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("Time taken (epoch)");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("Size");
        sb.append("</th>");
        sb.append("</tr>");
        Set<Long> ids = requestStarts.keySet();
        Set<Long> times = new HashSet<>();
        Set<Long> sizes = new HashSet<>();
        for (Long id : ids) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(id.toString());
            sb.append("</td>");
            sb.append("<td>");
            Long start = requestStarts.get(id);
            sb.append(start);
            sb.append("</td>");
            sb.append("<td>");
            Long end = requestEnds.get(id);
            sb.append(end == null ? "" : end);
            sb.append("</td>");
            sb.append("<td>");
            Long time = (end == null ? null : end - start);
            if (time != null) times.add(time);
            sb.append(time == null ? "" : time);
            sb.append("</td>");
            sb.append("<td>");
            Long size = responseSizes.get(id);
            if (size != null) sizes.add(size);
            sb.append(size == null ? "" : size);
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>");
        sb.append("Analytic Stat");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("Time");
        sb.append("</th>");
        sb.append("<th>");
        sb.append("Size");
        sb.append("</th>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>");
        sb.append("Average");
        sb.append("</td>");
        sb.append("<td>");
        sb.append(times.stream().mapToLong(Long::longValue).average().orElse(0));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(sizes.stream().mapToLong(Long::longValue).average().orElse(0));
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>");
        sb.append("Max");
        sb.append("</td>");
        sb.append("<td>");
        sb.append(Collections.max(times));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(Collections.max(sizes));
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>");
        sb.append("Min");
        sb.append("</td>");
        sb.append("<td>");
        sb.append(Collections.min(times));
        sb.append("</td>");
        sb.append("<td>");
        sb.append(Collections.min(sizes));
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
}
