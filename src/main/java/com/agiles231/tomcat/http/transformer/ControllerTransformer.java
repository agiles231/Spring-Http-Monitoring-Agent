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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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


    public String getForm() {
        StringBuilder sb = new StringBuilder();
        Map<Long, Long> requestStarts = TomcatHttpMonitoringAgent.getHttpRequestStarts();
        sb.append("<html>");
        sb.append("<head>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<form>");
        sb.append("<span>");
        sb.append("Select Id: ");
        sb.append("<select name=\"id\">");
        Set<Long> ids = requestStarts.keySet();
        for(Long id : ids) {
            sb.append("<option value=\"" + id.toString() + "\">" + id.toString() + "</option>");
        }
        sb.append("</select>");
        sb.append("<button type=\"submit\" value=\"Submit\"/>");
        sb.append("</span>");
        sb.append("</form>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    /*public String getViewAll() {
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
        for (Long id : ids) {
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(id.toString());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(requestStarts.get(id));
            sb.append("</td>");
            sb.append("<td>");
            Long end = requestEnds.get(id);
            sb.append(end == null ? "" : end);
            sb.append("</td>");
            sb.append("<td>");
            sb.append("Size");
            Long size = responseSizes.get(id);
            sb.append(size == null ? "" : size);
            sb.append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }*/
    public String getViewSingle(long id) {
        StringBuilder sb = new StringBuilder();
        Long start = TomcatHttpMonitoringAgent.getHttpRequestStart(id);
        Long end = TomcatHttpMonitoringAgent.getHttpRequestEnd(id);
        Long size = TomcatHttpMonitoringAgent.getHttpResponseSize(id);
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
        sb.append("<tr>");
        sb.append("<td>");
        sb.append(id);
        sb.append("</td>");
        sb.append("<td>");
        sb.append(start);
        sb.append("</td>");
        sb.append("<td>");
        sb.append(end);
        sb.append("</td>");
        sb.append("<td>");
        sb.append((end == null ? start : end) - start);
        sb.append("</td>");
        sb.append("<td>");
        sb.append(size);
        sb.append("</td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
}
