package com.tinllst.zkbackup.utils;

import java.util.concurrent.Callable;

/**
 * @author tinllst
 * @date 23:15 2020/1/13
 */
@SuppressWarnings("unchecked")
public class ThrowExceptionUtils {

    /**
     * 利用泛型处理lambda受检异常无法编译问题
     */
    public static <E extends Exception> void doThrow(Exception e) throws E {
        throw (E) e;
    }

    /**
     * 利用泛型处理lambda受检异常无法编译问题
     */
    public static <V, E extends Exception> void doThrow(Callable<V> callable) throws E {
        try {
            callable.call();
        } catch (Exception e) {
            throw (E) e;
        }
    }
}
