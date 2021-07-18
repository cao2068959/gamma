package com.chy.gamma.common;


import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.util.concurrent.Callable;

public abstract
class CustCLCallable<T> implements Callable<T> {

    private final ClassLoader classLoader;

    public CustCLCallable(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public abstract T run();

    @Override
    public T call() throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        return run();
    }
}
