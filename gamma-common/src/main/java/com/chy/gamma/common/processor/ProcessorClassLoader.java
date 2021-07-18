package com.chy.gamma.common.processor;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class ProcessorClassLoader extends URLClassLoader {


    private ClassLoader orphanClassLoader;
    private LoggerContext loggerContext;

    public ProcessorClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        orphanClassLoader = new URLClassLoader(urls, null);
    }

    public void init(){
        genLoggerContext();
    }

    private void genLoggerContext() {
        URI log4jUri = getLog4jXml();
        if (log4jUri == null) {
            //没用自定义日志配置文件，那么直接用core的
            loggerContext = (LoggerContext) LogManager.getContext(this, false);
            return;
        }
        loggerContext = (LoggerContext) LogManager.getContext(orphanClassLoader, false);
        loggerContext.setConfigLocation(log4jUri);
    }



    private URI getLog4jXml() {
        String processorClassPath = getProcessorClassPath();
        File file = new File(processorClassPath + "/log4j2.xml");
        if (file.exists()) {
            //提供了外置文件，那么用外置的
            return file.toURI();
        }
        URL log4jUrl = orphanClassLoader.getResource("log4j2.xml");
        if (log4jUrl == null) {
            return null;
        }
        try {
            return log4jUrl.toURI();
        } catch (URISyntaxException e) {
            System.out.println(("读取url[" + log4jUrl + "]失败"));
        }
        return null;
    }


    public abstract String getProcessorClassPath();


    public LoggerContext getLoggerContext() {
        return loggerContext;
    }
}
