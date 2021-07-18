package com.chy.gamma.common.utils;


import cn.hutool.core.convert.Convert;
import com.chy.gamma.common.CustCLCallable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ReflectUtils {

    public static Object newInstanceByClassLoader(ClassLoader classLoader, String classPath, Object[] agrs, Class[] agrsType) {
        Class<?> aClass = null;
        try {
            aClass = classLoader.loadClass(classPath);
            Constructor<?> constructor = aClass.getDeclaredConstructor(agrsType);
            return constructor.newInstance(agrs);
        } catch (Exception e) {
            throw new RuntimeException("生成实例失败: classPath:[" + classPath + "]", e);
        }
    }


    public static Object newInstanceByClassLoader(Class aClass, Object[] agrs, Class[] agrsType) {
        try {
            Constructor<?> constructor = aClass.getDeclaredConstructor(agrsType);
            return constructor.newInstance(agrs);
        } catch (Exception e) {
            throw new RuntimeException("生成实例失败: classPath:[" + aClass.getName() + "]", e);
        }
    }


    public static Object newInstanceByClassLoaderAsyn(ClassLoader classLoader, String classPath, Object[] agrs, Class[] agrsType) {
        Callable callable = () -> {
            Thread.currentThread().setContextClassLoader(classLoader);
            return newInstanceByClassLoader(classLoader, classPath, agrs, agrsType);
        };

        FutureTask futureTask = new FutureTask(callable);
        futureTask.run();
        try {
            return futureTask.get();
        } catch (Exception e) {
            throw new RuntimeException("生成实例失败：", e);
        }

    }


    public static Object newInstanceByClassLoader(ClassLoader classLoader, String classPath) {
        return newInstanceByClassLoader(classLoader, classPath, new Object[0], new Class[0]);
    }


    public static Object invokeMethod(Object instance, Class type, String mName, Object[] agrs, Class[] agrsType) {
        try {
            Method method = type.getDeclaredMethod(mName, agrsType);
            method.setAccessible(true);
            return method.invoke(instance, agrs);
        } catch (Exception e) {
            throw new RuntimeException("反射执行方法失败: classType:[" + type.getName() + "] mName : [" + mName + "] ", e);
        }
    }

    public static Class getClass(ClassLoader classLoader, String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("反射获取class失败: className:[" + className + "]", e);
        }

    }


    public static void execAllSetMethod(Class aclass, SetMethodExec setMethodExec) {
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(aclass);
        } catch (IntrospectionException e) {
            throw new RuntimeException("获取BeanInfo失败: className:[" + aclass + "]", e);
        }
        Arrays.stream(beanInfo.getPropertyDescriptors()).forEach(propertyDescriptor -> {
            String shortDescription = propertyDescriptor.getShortDescription();
            setMethodExec.exec(shortDescription, propertyDescriptor.getPropertyType(), propertyDescriptor.getWriteMethod());
        });
    }

    public static void setFieldValue(Field fieldValue, Object instance, String value) {
        fieldValue.setAccessible(true);
        Object realValue = typeConverFromStr(value, fieldValue.getType());
        try {
            fieldValue.set(instance, realValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("反射设置值失败 Field:[" + fieldValue.getName() + "] value: [" + value + "]", e);
        }


    }

    private static <T> T typeConverFromStr(String target, Class<T> type) {
        if (type == String.class) {
            return (T) target;
        }
        return Convert.convert(type, target);
    }


    public interface SetMethodExec {
        public void exec(String name, Class type, Method setMethod);
    }


}
