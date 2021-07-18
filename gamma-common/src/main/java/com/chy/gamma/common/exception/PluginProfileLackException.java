package com.chy.gamma.common.exception;


public class PluginProfileLackException extends RuntimeException {

    public PluginProfileLackException(String pluginName, String profileName) {
        super("插件[" + pluginName + "] 缺乏参数 [" + profileName + "]");
    }
}
