package com.chy.gamma.plugins;


import com.chy.gamma.common.profile.Profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GammaPluginsContext {

    List<GammaPlugin> gammaPlugins = new ArrayList<>();

    /**
     * 传入插件所在的路径， 然后就开始解析所有的插件
     *
     * @param root
     */
    public GammaPluginsContext(File root) {
        init(root);

    }

    private void init(File root) {
        createdGammaPlugin(root);
    }

    private void createdGammaPlugin(File pluginDir) {
        //不是文件夹就不处理了
        if (!pluginDir.isDirectory()) {
            return;
        }
        File[] files = pluginDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            String name = file.getName();
            GammaPlugin gammaPlugin = new GammaPlugin(file);
            if (gammaPlugin.isEmpty()) {
                continue;
            }
            gammaPlugins.add(gammaPlugin);
        }
    }

    public boolean isEmpty() {
        return this.gammaPlugins.size() == 0;
    }


    public List<GammaPlugin> getGammaPlugins() {
        return gammaPlugins;
    }
}
