package com.chy.gamma;


import com.chy.gamma.scan.ProcessorClassLoaderWapper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class GammaJarFile {


    public static File data;

    public static File getJarPath() {
        if (data != null) {
            return data;
        }
        data = findJarPath();
        return data;
    }

    public static Optional<File> getPluginsFile() {
        File jarPath = getJarPath();
        File[] files = jarPath.listFiles((fileName) -> "plugins".equals(fileName.getName()));
        if (files == null || files.length == 0) {
            return Optional.empty();
        }
        return Optional.of(files[0]);
    }

    private static File findJarPath() {
        String classResourcePath = GammaJarFile.class.getName().replaceAll("\\.", "/") + ".class";



        URL resource = ClassLoader.getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlString = resource.toString();
            System.out.println("开始解析 gamma-bootstrp.jar路径:" + urlString);
            int insidePathIndex = urlString.indexOf('!');
            boolean isInJar = insidePathIndex > -1;

            if (isInJar) {
                urlString = urlString.substring(urlString.indexOf("file:"), insidePathIndex);
                File agentJarFile = null;
                try {
                    agentJarFile = new File(new URL(urlString).toURI());
                } catch (MalformedURLException | URISyntaxException e) {
                    System.out.println("不能够根据url解析:" + urlString);
                }
                if (agentJarFile.exists()) {
                    return agentJarFile.getParentFile();
                }
            } else {
                int prefixLength = "file:".length();
                String classLocation = urlString.substring(
                        prefixLength, urlString.length() - classResourcePath.length());
                return new File(classLocation);
            }
        }

        throw new RuntimeException("获取gamma-bootstrp.jar路径失败");
    }

}
