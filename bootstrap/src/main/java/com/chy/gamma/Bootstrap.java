package com.chy.gamma;

import cn.hutool.core.lang.Pair;
import com.chy.gamma.common.profile.Profile;
import com.chy.gamma.common.utils.LogUtils;
import com.chy.gamma.common.utils.StringUtils;
import com.chy.gamma.plugins.GammaPluginsContext;
import com.chy.gamma.plugins.PluginsProcessorFactory;
import com.chy.gamma.scan.JarScanContext;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Bootstrap {


    public static void main(String[] args) {
        LogUtils.classLoader = Thread.currentThread().getContextClassLoader();
        Logger logger = LogUtils.getLogger(Bootstrap.class.getName());

        Pair<Map<String, String>, Map<String, String>> argsProduct = separateArgs(args);
        //先把没有设置了namespace的参数给放入到全局配置中
        Profile.setValue(argsProduct.getKey());


        Optional<File> pluginsFile = GammaJarFile.getPluginsFile();
        if (!pluginsFile.isPresent()) {
            logger.warn("缺少plugins目录");
            return;
        }
        GammaPluginsContext gammaPluginsContext = new GammaPluginsContext(pluginsFile.get());
        if (gammaPluginsContext.isEmpty()) {
            logger.warn("在plugins目录下没有加载到任何的插");
            return;
        }

        String source = Profile.getValue("source").orElseThrow(() -> new RuntimeException("没有传入有效的source， 请传入需要校验的jar包地址"));

        //开始加载存在了 namespaces的配置
        Profile.setExpressionProfile(argsProduct.getValue());

        //初始化扫描器
        JarScanContext jarScanContext = new JarScanContext(source);
        gammaPluginsContext.getGammaPlugins().stream()
                .map(gammaPlugin -> new PluginsProcessorFactory(gammaPlugin.getProcessorPath(),
                        gammaPlugin.getJarFile(), gammaPlugin.getPluginName())).forEach(jarScanContext::exec);

        logger.info("执行结束------------------->");
    }

    /**
     * 处理 通过java jvm参数传入进来的参数， 转换成map 然后分类
     * <p>
     * left: 没有带namespaces的参数
     * right: 带有namespaces的参数
     *
     * @param args
     * @return
     */
    private static Pair<Map<String, String>, Map<String, String>> separateArgs(String[] args) {
        Map<String, String> hasNamespaces = new HashMap<>();
        Map<String, String> noNamespaces = new HashMap<>();


        for (String arg : args) {
            String[] argSplit = arg.split("=");
            if (argSplit.length == 2) {
                String key = argSplit[0];
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                if (key.contains(":")) {
                    hasNamespaces.put(key, argSplit[1]);
                } else {
                    noNamespaces.put(key, argSplit[1]);
                }
            }
        }
        return Pair.of(noNamespaces, hasNamespaces);
    }


}
