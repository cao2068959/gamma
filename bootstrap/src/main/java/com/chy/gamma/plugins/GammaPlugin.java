package com.chy.gamma.plugins;


import com.chy.gamma.common.profile.Profile;
import com.chy.gamma.common.utils.LogUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GammaPlugin {

    private final Logger log;
    private JarFile jarFile;
    private String processorPath;
    private String pluginName;

    public GammaPlugin(File pluginDir) {
        this.log = LogUtils.getLogger("GammaPlugin");
        pluginName = pluginDir.getName();
        init(pluginDir);
    }

    private void init(File pluginDir) {
        scanDir(pluginDir);
    }

    private void scanDir(File pluginDir) {
        //不是文件夹就不处理了
        if (!pluginDir.isDirectory()) {
            return;
        }
        File[] files = pluginDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            String name = file.getName();
            if (this.jarFile == null && isSuffix(name, "jar")) {
                pluginJarFileHandle(file);
            }
            if ("config.properties".equals(name)) {
                //去写入配置文件
                Profile.setProfile(file, pluginName);
            }
        }
    }

    private boolean isSuffix(String fileName, String suffix) {
        return fileName.endsWith("." + suffix);
    }

    private void pluginJarFileHandle(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry gammaPluginDef = jarFile.getJarEntry("META-INF/gamma.plugin");
            //没有定义gamma.plugin 文件
            if (gammaPluginDef == null) {
                return;
            }
            InputStream gammaPluginDefInputStream = jarFile.getInputStream(gammaPluginDef);
            String gammaPluginDefClassPath = IOUtils.toString(gammaPluginDefInputStream, "UTF-8");
            this.processorPath = gammaPluginDefClassPath;
            this.jarFile = jarFile;
        } catch (IOException e) {
            log.error("文件[" + file.getName() + "] 不是一个合法的jar", e);
            return;
        }
    }

    public boolean isEmpty() {
        return this.jarFile == null;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public String getProcessorPath() {
        return processorPath;
    }

    public String getPluginName() {
        return pluginName;
    }
}
