package com.chy.gamma.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ClassLoaderUtils {

    public static ArrayList<URL> getSystemClassLoaderUrl() {
        String cp = System.getProperty("java.class.path");
        if (cp == null || cp.isEmpty()) {
            String initialModuleName = System.getProperty("jdk.module.main");
            cp = (initialModuleName == null) ? "" : null;
        }

        ArrayList<URL> path = new ArrayList<>();
        if (cp != null) {
            // map each element of class path to a file URL
            int off = 0, next;
            do {
                next = cp.indexOf(File.pathSeparator, off);
                String element = (next == -1)
                        ? cp.substring(off)
                        : cp.substring(off, next);
                URL url = toFileURL(element);
                if (url != null) {
                    path.add(url);
                }
                off = next + 1;
            } while (next != -1);
        }
        return path;
    }

    private static URL toFileURL(String s) {
        try {
            return new URL(s);
        } catch (InvalidPathException | IOException ignore) {
            return null;
        }
    }

}
