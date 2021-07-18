package com.chy.gamma.scan;


import com.chy.gamma.common.utils.ReflectUtils;
import com.chy.gamma.common.utils.StringUtils;
import com.chy.gamma.processor.IProcessorFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SpringBootJarScanEngine extends AbstractScanEngine {

    private final JarFile scanTargetJarFile;
    private final String path;
    private ClassLoader springClassLoader;


    private final String appClassesPrefix = "BOOT-INF/classes/";
    private final String classSuffix = ".class";

    public SpringBootJarScanEngine(String path, JarFile jarFile) {
        this.path = path;
        this.scanTargetJarFile = jarFile;
        initSpringClassLoader();
        collectAllClassPath();
    }

    private void initSpringClassLoader() {
        try {
            doInitSpringClassLoader();
        } catch (Exception e) {
            throw new RuntimeException("初始化 initSpringClassLoader 失败", e);
        }
    }

    private void doInitSpringClassLoader() throws MalformedURLException, ClassNotFoundException {
        URL url = new URL("file://" + path);
        URLClassLoader jarClassLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());

        Object JarLauncher = ReflectUtils.newInstanceByClassLoader(jarClassLoader, "org.springframework.boot.loader.JarLauncher");

        Class<?> launcher = jarClassLoader.loadClass("org.springframework.boot.loader.Launcher");
        Object pathArchives = ReflectUtils.invokeMethod(JarLauncher, launcher, "getClassPathArchives", null, null);
        ClassLoader springClassLoader = (ClassLoader) ReflectUtils.invokeMethod(JarLauncher, launcher, "createClassLoader",
                new Object[]{pathArchives}, new Class[]{List.class});
        this.springClassLoader = springClassLoader;
    }

    @Override
    public ClassLoader execProcessorClassLoader(IProcessorFactory processorHolder) {
        return processorHolder.getClassLoader(springClassLoader);

    }

    private void collectAllClassPath() {
        Enumeration<JarEntry> entries = scanTargetJarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String realName = jarEntry.getName();
            String classPath = StringUtils.toClassPath(appClassesPrefix, classSuffix, realName);
            if (classPath != null && isScan(classPath)) {
                getAllClassPath().add(classPath);
            }
        }
    }

    private String toClassPath(String data) {
        if (!data.endsWith(classSuffix) || !data.startsWith(appClassesPrefix)) {
            return null;
        }
        String result = data.substring(appClassesPrefix.length(), data.length() - classSuffix.length());
        return result.replace("/", ".");
    }


}
