package com.chy.gamma.plugins;


import com.chy.gamma.common.processor.ProcessorClassLoader;

import java.io.File;
import java.net.URL;
import java.util.jar.JarFile;

public class JarProcessorClassLoader extends ProcessorClassLoader {

    private final JarFile jarFile;

    public JarProcessorClassLoader(URL[] urls, ClassLoader parent, JarFile jarFile) {
        super(urls, parent);
        this.jarFile = jarFile;
    }


    @Override
    public String getProcessorClassPath() {
        return new File(jarFile.getName()).getParent();
    }
}
