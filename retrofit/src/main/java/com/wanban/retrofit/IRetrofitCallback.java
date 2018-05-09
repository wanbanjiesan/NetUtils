package com.wanban.retrofit;

/**
 * Created by fq on 2018/4/4.
 */

public interface IRetrofitCallback<T> {

    void onSuccess(T t);

    void onError(Throwable t);
}
