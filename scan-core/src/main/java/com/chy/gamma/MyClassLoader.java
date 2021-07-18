package com.chy.gamma;


import java.net.URL;
import java.net.URLClassLoader;

public class MyClassLoader extends URLClassLoader {

    public MyClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

}
