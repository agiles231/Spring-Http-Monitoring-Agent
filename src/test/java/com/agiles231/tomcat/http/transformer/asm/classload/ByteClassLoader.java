package com.agiles231.tomcat.http.transformer.asm.classload;

import java.util.HashMap;
import java.util.Map;

// pulled this from https://stackoverflow.com/questions/1781091/java-how-to-load-class-stored-as-byte-into-the-jvm
public class ByteClassLoader extends ClassLoader {
    private final Map<String, byte[]> extraClassDefs;

    public ByteClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
        super(parent);
        this.extraClassDefs = new HashMap<String, byte[]>(extraClassDefs);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        byte[] classBytes = this.extraClassDefs.remove(name);
        if (classBytes != null) {
            return defineClass(name, classBytes, 0, classBytes.length);
        }
        return super.findClass(name);
    }

}

