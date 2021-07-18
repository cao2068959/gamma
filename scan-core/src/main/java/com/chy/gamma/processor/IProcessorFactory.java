package com.chy.gamma.processor;


import com.chy.gamma.common.processor.Processor;

public interface IProcessorFactory {


    Processor getProcessor();


    ClassLoader getClassLoader(ClassLoader jarClassLoader);

    String namespace();
}
