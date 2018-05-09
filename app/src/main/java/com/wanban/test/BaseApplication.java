package com.wanban.test;

import android.app.Application;

import com.wanban.retrofit.conf.NetworkConf;
import com.wanban.retrofit.RetrofitUtil;

import java.io.File;

import okhttp3.Cache;

/**
 * Created by fq on 2018/4/4.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initRetrofit();

    }

    private void initRetrofit() {
        // 本地缓存文件配置
        File cacheFile = new File(getExternalCacheDir(), "testCache");
        Cache netLocalCache = new Cache(cacheFile, 100 * 1024 * 1024);

        NetworkConf networkConf = new NetworkConf.Builder(this)
                .setBaseUrl("http://cdn.100uu.tv/")
                .setLocalCache(netLocalCache)
                .setUseCache(true)
                .setRetryOnConnection(true)
                .build();

        RetrofitUtil.init(networkConf);
    }
}
