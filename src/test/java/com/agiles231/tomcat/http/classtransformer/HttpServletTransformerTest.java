package com.agiles231.tomcat.http.classtransformer;

import org.junit.Test;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;

public class HttpServletTransformerTest {

    @Test
    public void testTransform() throws IOException, IllegalClassFormatException {
        HttpServletTransformer transformer = new HttpServletTransformer();
        String httpServletTestClassFile = "test-resources/HttpServlet.class";
        byte[] inputBytes = Files.readAllBytes(new File(httpServletTestClassFile).toPath());
        ClassFileTransformer httpServletTransformer = new HttpServletTransformer();
        byte[] outputBytes = httpServletTransformer.transform(null, "javax/servlet/http/HttpServlet", null
                , null, inputBytes);
    }
}
