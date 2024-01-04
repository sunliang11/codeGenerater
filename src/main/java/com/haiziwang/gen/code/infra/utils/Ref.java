package com.haiziwang.gen.code.infra.utils;

/**
 * Created by root on 2018-03-13.
 */
public class Ref<T> {
    public Ref(T t){
        this.t=t;
    }
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
