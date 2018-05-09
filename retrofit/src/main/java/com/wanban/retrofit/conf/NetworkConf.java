package com.wanban.retrofit.conf;

import android.content.Context;

import com.wanban.retrofit.cache.NetworkCacheInterceptor;
import com.wanban.retrofit.cache.OfflineCacheInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;

/**
 * Created by fq on 2018/3/26.
 */

public final class NetworkConf {

    // retrofit缓存文件配置
    public Cache netLocalCache;
    // 缓存的具体配置
    public Interceptor networkInterceptor;
    // 断网情况配置
    public Interceptor offlineIntercept;
    // log日志拦截器
    public Interceptor loggingInterceptor;
    // 参数拦截器
    public Interceptor paramsInterceptor;
    // head参数拦截器
    public Interceptor headInterceptor;
    // 添加其他插件比如RxJava
    public CallAdapter.Factory factory;
    // 是否允许失败重试
    public boolean isRetryOnConnection = true;
    // 是否使用缓存
    public boolean isUseCache = true;
    // 是否显示日志信息
    public boolean isShowLogging = true;

    // retrofit的baseUrl
    public String baseUrl;

    public final int DEFAULT_HTTP_TIME_OUT = 15000;
    public final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 100;


    // 设置网络时间 毫秒
    public int connTime = DEFAULT_HTTP_TIME_OUT;
    public int readTime = DEFAULT_HTTP_TIME_OUT;
    public int writeTime = DEFAULT_HTTP_TIME_OUT;


    private NetworkConf(Builder builder) {
        this.netLocalCache = builder.netLocalCache;
        this.networkInterceptor = builder.networkInterceptor;
        this.offlineIntercept = builder.offlineIntercept;
        this.loggingInterceptor = builder.loggingInterceptor;
        this.paramsInterceptor = builder.paramsInterceptor;
        this.headInterceptor = builder.headInterceptor;
        this.factory = builder.factory;
        this.isRetryOnConnection = builder.isRetryOnConnection;
        this.isUseCache = builder.isUseCache;
        this.connTime = builder.connTime;
        this.readTime = builder.readTime;
        this.writeTime = builder.writeTime;
        this.baseUrl = builder.baseUrl;
        this.isShowLogging = builder.isShowLogging;
    }

    public Interceptor getDefaultLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }




    public static class Builder {
        // retrofit缓存文件配置
        private Cache netLocalCache;
        // 缓存的具体配置
        private Interceptor networkInterceptor;
        // 断网情况配置
        private Interceptor offlineIntercept;
        // log日志拦截器
        private Interceptor loggingInterceptor;
        // 参数拦截器
        private Interceptor paramsInterceptor;
        // head参数拦截器
        private Interceptor headInterceptor;
        // 添加其他插件比如RxJava
        private CallAdapter.Factory factory;
        // 是否允许失败重试
        private boolean isRetryOnConnection = true;
        // 是否使用缓存
        private boolean isUseCache = true;
        // 是否显示日志信息
        boolean isShowLogging = true;
        final int DEFAULT_HTTP_TIME_OUT = 15000;
        final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 100;
        // 设置网络时间 毫秒
        private int connTime = DEFAULT_HTTP_TIME_OUT;
        private int readTime = DEFAULT_HTTP_TIME_OUT;
        private int writeTime = DEFAULT_HTTP_TIME_OUT;
        // retrofit的baseUrl
        private String baseUrl;

        private Context context;

        public Builder(Context context) {
            this.context = context.getApplicationContext();

        }

        public Builder setUseCache(boolean isUseCache) {
            this.isUseCache = isUseCache;
            return this;
        }

        public Builder setLocalCache(Cache cache) {
            netLocalCache = cache;
            return this;
        }

        public Builder setLoggingInterceptor(Interceptor loggingInterceptor) {
            this.loggingInterceptor = loggingInterceptor;
            return this;
        }

        public Builder setNetworkInterceptor(Interceptor networkInterceptor) {
            this.networkInterceptor = networkInterceptor;

            return this;
        }

        public Builder setOfflineIntercept(Interceptor offlineIntercept) {
            this.offlineIntercept = offlineIntercept;
            return this;
        }

        public Builder setRetryOnConnection(boolean isRetryOnConnection) {
            this.isRetryOnConnection = isRetryOnConnection;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setRetrofitFactory(CallAdapter.Factory factory) {
            this.factory = factory;
            return this;
        }


        /**
         * 设置retrofit的http请求相关
         *
         * @param connTime  conn链接的超时时间
         * @param readTime  read流的超时时间
         * @param writeTime write流的超时时间
         */
        public Builder setTimeout(int connTime, int readTime, int writeTime) {
            this.connTime = connTime;
            this.readTime = readTime;
            this.writeTime = writeTime;
            return this;
        }

        public Builder setTimeout(int connTime, int readTime) {
            setTimeout(connTime, readTime, writeTime);
            return this;
        }

        public Builder setTimeout(int connTime) {
            setTimeout(connTime, readTime, writeTime);
            return this;
        }

        public Cache getDefaultCache() {
            File cacheFile = new File(context.getApplicationContext().getExternalCacheDir(), "retrofitCache");
            Cache netLocalCache = new Cache(cacheFile, DEFAULT_CACHE_SIZE);
            return netLocalCache;
        }

        public NetworkConf build() {
            // 缓存设置
            if (isUseCache){
                if (netLocalCache == null) {
                    netLocalCache = getDefaultCache();
                }

                if (networkInterceptor == null) {
                    networkInterceptor = new NetworkCacheInterceptor();
                }

                if (offlineIntercept == null) {
                    offlineIntercept = new OfflineCacheInterceptor(context);
                }
            }

            return new NetworkConf(this);
        }

        public void addRequestParams(final Map<String, String> params) {
            if (params != null && params.size() > 0) {
                paramsInterceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        HttpUrl.Builder modifiedUrl = originalRequest.url().newBuilder();
                        for (String key : params.keySet()) {
                            modifiedUrl.addQueryParameter(key, params.get(key));
                        }
                        Request request = originalRequest.newBuilder().url(modifiedUrl.build()).build();
                        return chain.proceed(request);
                    }
                };
            }
        }

        public Interceptor addHeaderParams(final Map<String, String> params) {
            if (params != null && params.size() > 0) {
                headInterceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request.Builder requestBuilder = originalRequest.newBuilder();
                        for (String key : params.keySet()) {
                            requestBuilder.addHeader(key, params.get(key));
                        }
                        requestBuilder.method(originalRequest.method(), originalRequest.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                };
            }
            return headInterceptor;
        }
    }
}
