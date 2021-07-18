package com.chy.gamma.scan;


import java.net.URL;
import java.net.URLClassLoader;

public class ProcessorClassLoaderWapper extends URLClassLoader {


    ClassLoader gammaCoreClassLoader;


    public ProcessorClassLoaderWapper(ClassLoader topParent, URL[] urls) {
        super(new URL[0], topParent);
    }

    //@Override
    public Class<?> loadClass2(String name, boolean a) throws ClassNotFoundException {
        if ("org.apache.logging.log4j.Logger".equals(name)) {
            return gammaCoreClassLoader.loadClass(name);
        }
        //gamma-common 包下面的所有 class都使用 gamma的类加载器
        if (name.startsWith("com.chy.gamma.common")) {
            return gammaCoreClassLoader.loadClass(name);
        }
        return super.loadClass(name, a);
    }

    public void setScanClassLoader(ClassLoader gammaCoreClassLoader) {
        this.gammaCoreClassLoader = gammaCoreClassLoader;
    }
}
