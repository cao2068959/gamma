package com.chy.gamma.common.profile;


import com.chy.gamma.common.exception.PluginProfileLackException;
import com.chy.gamma.common.processor.Processor;
import com.chy.gamma.common.utils.ReflectUtils;
import com.chy.gamma.common.utils.StringUtils;
import com.chy.gamma.common.utils.TypeUtils;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ProcessorProfileInjection {

    public static Object injection(Processor processor, String namespaces) {

        Class<? extends Processor> processorClass = processor.getClass();
        Type[] genericInterfaces = processorClass.getGenericInterfaces();
        Type genericType = TypeUtils.getGenericType(genericInterfaces, Processor.class);
        Class genericClass = TypeUtils.typeToClass(genericType);
        Object profileInstance = null;
        //没有填写 泛型或者 object的那么 就直接给他注入一个 Map
        if (genericClass == null || genericClass.getName().equals(Object.class.getName())
                || genericClass.isAssignableFrom(Map.class)) {
            profileInstance = profileToMap(genericClass, namespaces);
        } else {
            profileInstance = profileToInstance(genericClass, namespaces);
        }
        return profileInstance;
    }

    private static Map profileToMap(Class mapClass, String namespaces) {
        Map result = null;

        //如果传入的直接就是 HashMap 或者是 Map 接口 那么就直接 new了
        if (mapClass == null || mapClass == HashMap.class || mapClass == Map.class) {
            result = new HashMap();
        } else {
            result = (Map) ReflectUtils.newInstanceByClassLoader(mapClass, new Object[0], new Class[0]);
        }
        Map<String, String> namespaceAll = Profile.getAllValueFromNamespace(namespaces);


        for (Map.Entry<String, String> stringStringEntry : namespaceAll.entrySet()) {
            result.put(stringStringEntry.getKey(), stringStringEntry.getValue());
        }
        return result;
    }

    public static Object profileToInstance(Class genClass, String namespaces) {
        Map<String, String> namespaceAll = Profile.getAllValueFromNamespace(namespaces);
        Object result = ReflectUtils.newInstanceByClassLoader(genClass, new Object[0], new Class[0]);
        //执行里面所有的set方法
        scanProfileField(genClass, (field, param) -> {
            String paramName = param.value();
            if (StringUtils.isEmpty(paramName)) {
                paramName = field.getName();
            }
            String value = namespaceAll.get(paramName);
            if (value == null) {
                //如果该值不能为null，
                if (!param.nullable()){
                    throw new PluginProfileLackException(namespaces, paramName);
                }
                return;
            }
            ReflectUtils.setFieldValue(field, result, value);
        });
        return result;
    }


    private static void scanProfileField(Class targetClass, ProfileField profileField) {
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Param param = declaredField.getAnnotation(Param.class);
            if (param == null) {
                continue;
            }

            profileField.exec(declaredField, param);
        }
    }

    interface ProfileField {
        void exec(Field field, Param param);
    }

}
