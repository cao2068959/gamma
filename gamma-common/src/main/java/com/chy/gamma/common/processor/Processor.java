package com.chy.gamma.common.processor;

/**
 * gamma插件的执行接口
 * 泛型T 为插件需要的配置文件类，如：本插件在配置文件config.properties 中设置了属性 commitId=1234 、 ref=master
 * 那么泛型T需要定义 一个实体类来接收这2个属性
 *
 *
 * @param <T>
 */
public interface Processor<T> {

    /**
     *  配置对象的接收，gamma将会根据配置文件以及jvm参数来生成对应的配置对象
     *
     * @param t
     */
    void setProperty(T t);

    /**
     * 处理的核心接口
     * gamma每扫描到jar中的一个class都会回调该接口
     *
     * @param originClass 被扫描jar中的某一个class
     */
    void processor(Class originClass);

    /**
     * 当全部扫描完后回调
     *
     */
    void finishProcessor();

}
