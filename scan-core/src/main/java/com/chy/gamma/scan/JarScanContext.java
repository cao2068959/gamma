package com.chy.gamma.scan;


import com.chy.gamma.processor.IProcessorFactory;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class JarScanContext {

    private final JarFile jarFile;
    private final String path;
    private final JarType jarType;
    private final ScanEngine scanEngine;


    public JarScanContext(String path) {
        try {
            File file = new File(path);
            this.path = file.getAbsolutePath();
            this.jarFile = getJarFile(file);
            this.jarType = checkJarType(jarFile);
            this.scanEngine = chooseScanEngine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("地址 : [" + path + "] 不是jar文件或文件不存在");
        }
    }

    public void exec(IProcessorFactory processor) {
        scanEngine.exec(processor);
    }

    private ScanEngine chooseScanEngine() {
        if (jarType == JarType.SPRING_BOOT) {
            return new SpringBootJarScanEngine(path, jarFile);
        }
        return new NormalJarScanEngine(path, jarFile);
    }

    private JarType checkJarType(JarFile jarFile) throws IOException {
        String springBootVersion = jarFile.getManifest().getMainAttributes().getValue("Spring-Boot-Version");
        if (springBootVersion == null) {
            return JarType.NORMAL;
        }
        return JarType.SPRING_BOOT;
    }

    private JarFile getJarFile(File file) throws IOException {
        return new JarFile(file);
    }
}
