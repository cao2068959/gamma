package com.chy.gamma.common.utils;

import com.chy.gamma.common.processor.ProcessorClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class LogUtils {

    public static ClassLoader classLoader;

    public static Logger getLogger(String classPath) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

        if (!(contextClassLoader instanceof ProcessorClassLoader)) {
            LoggerContext loggerContext = (LoggerContext) LogManager.getContext(contextClassLoader, false);
            return loggerContext.getLogger(classPath);
        }
        ProcessorClassLoader processorClassLoader = (ProcessorClassLoader) contextClassLoader;
        LoggerContext loggerContext = processorClassLoader.getLoggerContext();
        return loggerContext.getLogger(classPath);
    }

    public static void printStackTrace(Logger logger, Throwable e, String namespace) {
        logger.error(e);
        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            logger.error(stackTraceElement);
        }


    }

}
