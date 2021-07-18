package com.chy.gamma.common.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtils {

    /**
     * 吧 type类型转成 class类型
     *
     * @param type
     * @return
     */
    public static Class typeToClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            return typeToClass(rawType);
        }
        return null;
    }

    /**
     * 获取泛型的值,只能获取只有一个泛型的情况
     *
     * @param genericType
     * @return
     */
    public static Type getGenericType(Type genericType) {
        if (genericType == null || !(genericType instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments == null || actualTypeArguments.length != 1) {
            return null;
        }

        Type actualTypeArgument = actualTypeArguments[0];
        return actualTypeArgument;
    }


    /**
     * 获取 指定类型上面的泛型, 同样也是只能获取一个泛型
     *
     * @param genericTypes
     * @param aclass
     * @return
     */
    public static Type getGenericType(Type[] genericTypes, Class aclass) {
        if (genericTypes == null || genericTypes.length == 0) {
            return null;
        }
        for (Type genericType : genericTypes) {
            if (!(genericType instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            if (!parameterizedType.getRawType().getTypeName().equals(aclass.getName())) {
                continue;
            }
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments == null || actualTypeArguments.length != 1) {
                continue;
            }

            return actualTypeArguments[0];
        }

        return null;

    }


}
