package com.chy.gamma.scan;


import com.chy.gamma.common.processor.Processor;
import com.chy.gamma.common.profile.ProcessorProfileInjection;
import com.chy.gamma.common.utils.LogUtils;
import com.chy.gamma.common.utils.ReflectUtils;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ProcessorExecRunnable implements Runnable {

    private final Processor processor;
    List<String> allClassPath;
    private ClassLoader scanEngineClassLoader;
    private String namespace;
    Logger log;

    public ProcessorExecRunnable(Processor processor, List<String> allClassPath, String namespace) {
        this.processor = processor;
        this.allClassPath = allClassPath;
        this.namespace = namespace;

    }

    @Override
    public void run() {
        try {
            init();
            doRun();
        } catch (Throwable e) {
            LogUtils.printStackTrace(log, e, namespace);
            throw e;
        }
    }

    private void init() {
        log = LogUtils.getLogger(ProcessorExecRunnable.class.getName());
    }


    private void doRun() {
        //生成对应的配置类
        Object configIntance = ProcessorProfileInjection.injection(processor, namespace);
        processor.setProperty(configIntance);
        for (String classPath : allClassPath) {
            try {
                Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(classPath);
                processor.processor(aClass);
            } catch (ClassNotFoundException|NoClassDefFoundError e) {
                e.printStackTrace();
            }
        }
        processor.finishProcessor();
    }


    public void setScanEngineClassLoader(ClassLoader scanEngineClassLoader) {
        this.scanEngineClassLoader = scanEngineClassLoader;
    }
}
