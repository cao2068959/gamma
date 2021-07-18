package com.chy.gamma.scan;


import com.chy.gamma.common.utils.StringUtils;
import com.chy.gamma.processor.IProcessorFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NormalJarScanEngine extends AbstractScanEngine {


    private final String path;
    private final JarFile scanTargetJarFile;
    private ClassLoader jarClassLoard;
    private final String classSuffix = ".class";

    public NormalJarScanEngine(String path, JarFile jarFile) {
        this.path = path;
        this.scanTargetJarFile = jarFile;
        initJarClassLoard();
        collectAllClassPath();
    }

    private void initJarClassLoard() {
        try {
            doInitJarClassLoard();
        } catch (Exception e) {
            throw new RuntimeException("初始化 initJarClassLoard 失败", e);
        }
    }

    private void collectAllClassPath() {
        Enumeration<JarEntry> entries = scanTargetJarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String realName = jarEntry.getName();
            String classPath = StringUtils.toClassPath("", classSuffix, realName);
            if (classPath != null && isScan(classPath)) {
                getAllClassPath().add(classPath);
            }
        }
    }


    private void doInitJarClassLoard() throws MalformedURLException {
        URL url = new URL("file://" + path);
        this.jarClassLoard = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public ClassLoader execProcessorClassLoader(IProcessorFactory processorHolder) {
        return processorHolder.getClassLoader(jarClassLoard);
    }
}
