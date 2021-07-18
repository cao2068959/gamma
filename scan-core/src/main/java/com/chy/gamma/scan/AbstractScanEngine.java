package com.chy.gamma.scan;


import com.chy.gamma.common.processor.Processor;
import com.chy.gamma.common.profile.Profile;
import com.chy.gamma.processor.IProcessorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class AbstractScanEngine implements ScanEngine {


    private List<String> allClassPath = new ArrayList<>();
    private List<String> scanPackages = new ArrayList<>();


    public AbstractScanEngine() {
        initScanPackagesData();
    }

    private void initScanPackagesData() {
        Optional<String> profileValueOptional = Profile.getValue("scan.package");
        if (!profileValueOptional.isPresent()) {
            return;
        }
        String profileValue = profileValueOptional.get().trim();
        if ("".equals(profileValue) || "*".equals(profileValue)) {
            return;
        }

        for (String v : profileValue.split(",")) {
            v = v.trim();
            if ("".equals(v)) {
                continue;
            }

            if ("*".equals(v)) {
                scanPackages = new ArrayList<>();
                return;
            }

            scanPackages.add(v);
        }
    }

    protected boolean isScan(String path) {
        if (scanPackages.size() == 0) {
            return true;
        }
        for (String scanPackage : scanPackages) {
            boolean result = path.startsWith(scanPackage);
            if (result) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void exec(IProcessorFactory processorHolder) {
        ClassLoader classLoader = execProcessorClassLoader(processorHolder);
        Processor processor = processorHolder.getProcessor();
        ProcessorExecRunnable processorExecRunnable = new ProcessorExecRunnable(processor, allClassPath,
                processorHolder.namespace());
        Thread thread = new Thread(processorExecRunnable);
        thread.setContextClassLoader(classLoader);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract ClassLoader execProcessorClassLoader(IProcessorFactory processorHolder);


    public List<String> getAllClassPath() {
        return allClassPath;
    }
}
