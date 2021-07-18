package com.chy.gamma.scan;


import com.chy.gamma.processor.IProcessorFactory;

public interface ScanEngine {


    void exec(IProcessorFactory processorClass);
}
