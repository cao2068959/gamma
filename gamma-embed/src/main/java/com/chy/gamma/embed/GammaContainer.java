package com.chy.gamma.embed;


import com.chy.gamma.common.profile.Profile;
import com.chy.gamma.common.processor.Processor;
import com.chy.gamma.scan.JarScanContext;

import java.util.Map;

import static com.chy.gamma.embed.Constant.DEFAULT_NAMESPACE;

public class GammaContainer {

    String source;

    public GammaContainer(String jarSource, Map<String, String> config) {
        this.source = jarSource;
        Profile.setValue("source", jarSource);
        if (config != null) {
            Profile.setProfile(config, DEFAULT_NAMESPACE);
            Profile.setValue(config);
        }
    }

    public void start(Processor processor) {
        JarScanContext jarScanContext = new JarScanContext(source);
        jarScanContext.exec(new EmbedProcessorFactory(processor));
    }
}
