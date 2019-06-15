package com.agiles.tomcat.http.agent.transformer;

import com.agiles231.tomcat.http.transformer.HttpServletTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;

import static org.objectweb.asm.Opcodes.ASM5;

public class HttpServletTransformerTest {

    @Test
    public void testTransform() throws IOException, IllegalClassFormatException {
        HttpServletTransformer transformer = new HttpServletTransformer();
        System.out.println(System.getProperty("user.dir"));
        String httpServletTestClassFile = "src/test/resources/HttpServlet.class";
        byte[] inputBytes = Files.readAllBytes(new File(httpServletTestClassFile).toPath());
        ClassFileTransformer httpServletTransformer = new HttpServletTransformer();
        byte[] outputBytes = httpServletTransformer.transform(null, "javax/servlet/http/HttpServlet", null
                , null, inputBytes);
        Assert.assertArrayEquals(inputBytes, outputBytes); // for now, assert no change, because we have made no change
    }
}
