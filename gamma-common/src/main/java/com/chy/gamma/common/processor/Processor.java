package com.chy.gamma.common.processor;


public interface Processor<T> {


    void setProperty(T t);

    void processor(Class originClass);

    void finishProcessor();

}
