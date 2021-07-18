package com.chy.gamma.common.profile;


import com.chy.gamma.common.utils.LogUtils;
import com.chy.gamma.common.utils.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;


public class Profile {

    static Logger logger = LogUtils.getLogger("profile");


    private static Properties data = new Properties();
    private static Map<String, Properties> namespaceData = new HashMap<>();


    static {
        init();
    }

    private static void init() {
        File profileFile = getCoreProfileFile();
        if (!profileFile.exists()) {
            System.out.println("配置文件 : [" + profileFile.getAbsolutePath() + "] 不存在");
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(profileFile));
        } catch (IOException e) {
            throw new RuntimeException("配置文件读取失败", e);
        }

        data.putAll(properties);
    }

    public static void setProfile(File profileFile, String namespace) {
        if (!profileFile.exists()) {
            System.out.println("配置文件 : [" + profileFile.getAbsolutePath() + "] 不存在");
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(profileFile));
        } catch (IOException e) {
            throw new RuntimeException("配置文件读取失败", e);
        }
        setProfile(properties, namespace);
    }

    public static void setProfile(Map<?, ?> datas, String namespace) {
        Properties properties = namespaceData.get(namespace);
        if (properties == null) {
            properties = new Properties();
            namespaceData.put(namespace, properties);
        }
        for (Map.Entry<?, ?> stringStringEntry : datas.entrySet()) {
            Object value = stringStringEntry.getValue();
            String valueStr = value == null ? null : value.toString();
            properties.setProperty(stringStringEntry.getKey().toString(), valueStr);
        }
    }

    public static void setProfile(String key, String value, String namespace) {
        Properties properties = namespaceData.get(namespace);
        if (properties == null) {
            properties = new Properties();
            namespaceData.put(namespace, properties);
        }
        properties.setProperty(key, value);
    }


    private static File getCoreProfileFile() {
        ProtectionDomain protectionDomain = Profile.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = null;
        try {
            location = codeSource != null ? codeSource.getLocation().toURI() : null;
        } catch (URISyntaxException e) {
            throw new RuntimeException("无法获取项目的相对位置", e);
        }
        String path = location != null ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new RuntimeException("项目运行路径获取失败");
        }

        if (path.endsWith(".jar")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
        }
        path = path + "config.properties";

        File result = new File(path);
        return result;
    }


    public static Optional<String> getValue(String key) {
        return Optional.ofNullable((String) data.get(key));
    }

    public static Optional<String> getValueFromNamespace(String key, String namespace) {

        Properties properties = namespaceData.get(namespace);
        if (properties == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((String) properties.get(key));
    }


    public static String getValue(String key, String defaultValue) {
        String value = (String) data.get(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    public static Optional<String> getValueIfDefaultNon(String key, String defaultValue) {
        if (!StringUtils.isEmpty(defaultValue)) {
            return Optional.of(defaultValue);
        }
        return Optional.ofNullable((String) data.get(key));
    }

    public static void setValue(Map<String, String> profile) {
        data.putAll(profile);
    }


    public static void setValue(String key, String value) {
        data.put(key, value);
    }


    public static void prinfAll() {
        data.forEach((k, v) -> {
            logger.info(k + " = " + v);
        });
    }


    public static Map<String, String> getAllValueFromNamespace(String namespace) {
        Properties properties = namespaceData.get(namespace);
        if (properties == null) {
            return new HashMap<>(0);
        }
        return properties.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(),
                e -> {
                    if (e.getValue() == null) {
                        return null;
                    }
                    return e.getValue().toString();
                }));
    }

    /**
     * 设置表达式 及对应值 这里的表达式是 namespaces:key ， 如果没有传入 namespaces 则不放入 namespaces中
     *
     * @param expression namespaces:key 的格式
     * @param value
     */
    public static void setExpressionProfile(String expression, String value) {
        String[] splitResult = expression.split(":", 2);
        if (splitResult.length != 2) {
            throw new RuntimeException("参数格式错误 key:[" + expression + "]");
        }
        setProfile(splitResult[1], value, splitResult[0]);
    }

    public static void setExpressionProfile(Map<String, String> hasNamespacesProfile) {
        if (hasNamespacesProfile == null) {
            return;
        }
        hasNamespacesProfile.entrySet().stream().forEach(kv -> {
            setExpressionProfile(kv.getKey(), kv.getValue());
        });
    }
}
