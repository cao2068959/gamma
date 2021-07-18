package com.chy.gamma.plugins;


import com.chy.gamma.common.processor.Processor;
import com.chy.gamma.common.utils.ReflectUtils;
import com.chy.gamma.processor.IProcessorFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;

public class PluginsProcessorFactory implements IProcessorFactory {

    String processorPath;
    JarFile pluginJarFile;
    JarProcessorClassLoader jarClassLoader;
    String namespace;


    public PluginsProcessorFactory(String processorPath, JarFile jarFile, String namespace) {
        this.processorPath = processorPath;
        this.pluginJarFile = jarFile;
        this.namespace = namespace;
    }

    @Override
    public Processor getProcessor() {
        return (Processor) ReflectUtils.newInstanceByClassLoaderAsyn(jarClassLoader, processorPath, new Object[0], new Class[0]);
    }

    @Override
    public ClassLoader getClassLoader(ClassLoader parents) {
        if (jarClassLoader != null) {
            return jarClassLoader;
        }
        URL url = null;
        try {
            url = new URL("file://" + pluginJarFile.getName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JarProcessorClassLoader jarClassLoader =
                new JarProcessorClassLoader(new URL[]{url}, parents, pluginJarFile);
        jarClassLoader.init();
        this.jarClassLoader = jarClassLoader;
        return jarClassLoader;
    }

    @Override
    public String namespace() {
        return namespace;
    }
}
