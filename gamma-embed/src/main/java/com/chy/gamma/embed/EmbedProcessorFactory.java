package com.chy.gamma.embed;


import com.chy.gamma.common.processor.Processor;
import com.chy.gamma.processor.IProcessorFactory;

import static com.chy.gamma.embed.Constant.DEFAULT_NAMESPACE;

public class EmbedProcessorFactory implements IProcessorFactory {

    Processor processor;

    public EmbedProcessorFactory(Processor processor) {
        this.processor = processor;
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public ClassLoader getClassLoader(ClassLoader jarClassLoader) {
        return jarClassLoader;
    }

    @Override
    public String namespace() {
        return DEFAULT_NAMESPACE;
    }
}
