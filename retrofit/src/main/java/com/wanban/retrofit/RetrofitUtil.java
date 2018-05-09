package com.wanban.retrofit;

import android.text.TextUtils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.wanban.retrofit.conf.NetworkConf;
import com.wanban.retrofit.conf.RetrofitConf;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fq on 2018/3/26.
 */

public class RetrofitUtil {

    private static class SingletonHolder {
        static RetrofitUtil instance = new RetrofitUtil();
    }

    private RetrofitUtil() {
    }

    public static RetrofitUtil getInstance() {
        return RetrofitUtil.SingletonHolder.instance;
    }

    private static NetworkConf networkConf;
    private static Retrofit.Builder retrofitBuilder;

    private Retrofit retrofit;


    private static String getBaseUrl() {
        String baseUrl = null;
        if (TextUtils.isEmpty(networkConf.baseUrl)) {
                baseUrl = BuildConfig.BASE_URL;
        } else {
            baseUrl = networkConf.baseUrl;
        }
        return baseUrl;
    }

    private static OkHttpClient.Builder generalBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // Log信息拦截器，代码略
        if (networkConf.isShowLogging) {
            if (networkConf.loggingInterceptor == null) {
                builder.addInterceptor(networkConf.getDefaultLoggingInterceptor());
            } else {
                builder.addInterceptor(networkConf.loggingInterceptor);
            }
        }
        // 是否使用缓存
        if (networkConf.isUseCache) {
            if (networkConf.netLocalCache != null) {
                builder.cache(networkConf.netLocalCache);
            }

            if (networkConf.networkInterceptor != null) {
                builder.addNetworkInterceptor(networkConf.networkInterceptor);
            }

            if (networkConf.offlineIntercept != null) {
                builder.addInterceptor(networkConf.offlineIntercept);
            }
        }

        builder.retryOnConnectionFailure(networkConf.isRetryOnConnection);
        builder.connectTimeout(networkConf.connTime, TimeUnit.MILLISECONDS);
        builder.readTimeout(networkConf.readTime, TimeUnit.MILLISECONDS);
        builder.writeTimeout(networkConf.writeTime, TimeUnit.MILLISECONDS);


        // 设置通用参数
        if (networkConf.paramsInterceptor != null) {
            builder.addInterceptor(networkConf.paramsInterceptor);
        }

        // 设置header
        if (networkConf.headInterceptor != null) {
            builder.addInterceptor(networkConf.headInterceptor);
        }


        return builder;
    }

    private void updateConf(RetrofitConf retrofitConf) {
        if (retrofitConf.isRetryOnConnection != networkConf.isRetryOnConnection){
            networkConf.isRetryOnConnection = retrofitConf.isRetryOnConnection;
        }

        if (retrofitConf.isShowLogging != networkConf.isShowLogging){
            networkConf.isShowLogging = retrofitConf.isShowLogging;
        }

        if (retrofitConf.isUseCache != networkConf.isUseCache){
            networkConf.isUseCache = retrofitConf.isUseCache;
        }

        if (retrofitConf.baseUrl != networkConf.baseUrl){
            networkConf.baseUrl = retrofitConf.baseUrl;
        }

        if (retrofitConf.connTime != networkConf.connTime){
            networkConf.connTime = retrofitConf.connTime;
        }

        if (retrofitConf.factory != networkConf.factory){
            networkConf.factory = retrofitConf.factory;
        }

        if (retrofitConf.headInterceptor != networkConf.headInterceptor){
            networkConf.headInterceptor = retrofitConf.headInterceptor;
        }

        if (retrofitConf.loggingInterceptor != networkConf.loggingInterceptor){
            networkConf.loggingInterceptor = retrofitConf.loggingInterceptor;
        }

        if (retrofitConf.netLocalCache != networkConf.netLocalCache){
            networkConf.netLocalCache = retrofitConf.netLocalCache;
        }

        if (retrofitConf.offlineIntercept != networkConf.offlineIntercept){
            networkConf.offlineIntercept = retrofitConf.offlineIntercept;
        }

        if (retrofitConf.paramsInterceptor != networkConf.paramsInterceptor){
            networkConf.paramsInterceptor = retrofitConf.paramsInterceptor;
        }


        if (retrofitConf.readTime != networkConf.readTime){
            networkConf.readTime = retrofitConf.readTime;
        }

        if (retrofitConf.writeTime != networkConf.writeTime){
            networkConf.writeTime = retrofitConf.writeTime;
        }
    }


    private static Retrofit.Builder generalRetrofitBuilder() {
        Retrofit.Builder retrofitBuilder;
        // 先生成OkHttpClient的Builder
        OkHttpClient.Builder builder = generalBuilder();
        if (builder == null) {
            throw new NullPointerException("generalRetrofitBuilder can not be initialized with null");
        } else {
            String baseUrl = getBaseUrl();
            retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    // 添加Retrofit到RxJava的转换器
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(builder.build());
            if (networkConf.factory == null) {
                retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
            }
        }
        return retrofitBuilder;
    }


    /**
     * 通过传入的参数，配置对应的retrofit，并获得Retrofit.Builder
     * @param conf
     */
    public static void init(NetworkConf conf) {
        if (conf == null) {
            throw new NullPointerException("conf can not be initialized with null");
        }
        networkConf = conf;
        // 根据配置生成对应的Builder
        retrofitBuilder = generalRetrofitBuilder();
    }

    /**
     * 返回retrofit
     * @return
     */
    public Retrofit getRetrofit() {
        if (retrofitBuilder == null) {
            throw new NullPointerException("please configure application init");
        }

        if (retrofit == null) {
            retrofit = retrofitBuilder.build();
        }
        return retrofit;
    }

    /**
     * 创建或更新retrofit配置获取retrofit对象
     * @return
     */
    public Retrofit getRetrofit(RetrofitConf retrofitConf) {
        if (networkConf == null) {
            throw new NullPointerException("conf can not be initialized with null");
        }
        // 如果没有配置项，则返回初始配置的retrofit
        if (retrofitConf == null) {
            retrofit = getRetrofit();
        }else {
            // 更新配置
            updateConf(retrofitConf);
            // 通过新的配置生成新的Builder
            retrofitBuilder = generalRetrofitBuilder();
            if (retrofitBuilder == null) {
                throw new NullPointerException("retrofitBuilder is null");
            }
            retrofit = retrofitBuilder.build();
        }
        return retrofit;
    }

    /**
     * 使用RxJava后的处理方式
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> T observable(Class<T> clazz){
        return getInstance().getRetrofit().create(clazz);
    }


    public static  <T> void doHttp(Observable<T> observable, final IRetrofitCallback<T> callback){
        observable.subscribeOn(Schedulers.io())
//                .doOnSubscribe(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        // 可以做个网络判断
//
//                    }
//                })

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T homeResponse) throws Exception {
                        if (callback != null){
                            callback.onSuccess(homeResponse);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (callback != null){
                            callback.onError(throwable);
                        }
                    }
                });


    }
}
