package com.wanban.test.response;

import android.util.Log;

import com.wanban.retrofit.IRetrofitCallback;
import com.wanban.retrofit.RetrofitUtil;
import com.wanban.test.service.HomeService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by fq on 2018/4/4.
 */

public class HomeResponse {

    public ArrayList<GenresResponse> genres;

    public class GenresResponse {
        public int id;
        public int genre_id;
        public String name;
        public String image_focus;
    }


    public static void doHttpHomeData(String platform, final IRetrofitCallback<HomeResponse> callback){
        // 获取Retrofit对象
        Retrofit retrofit = RetrofitUtil.getInstance().getRetrofit();
        // 获取对应接口的Service
        HomeService homeService = retrofit.create(HomeService.class);
        // 获取Call对象
        Call<HomeResponse> call = homeService.getData(platform);
        // 进行接口请求
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                Log.e("Tag", "response:" + response.body().genres.size());
                if(callback != null){
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("Tag", "error:" + t.getLocalizedMessage());
                if(callback != null){
                    callback.onError(t);
                }
            }
        });
    }


    public static void doHttpRx(String platform, final IRetrofitCallback<HomeResponse> callback){
        RetrofitUtil.getInstance()
                .getRetrofit()
                .create(HomeService.class)
                .getRxData(platform)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        // 可以做个网络判断
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HomeResponse>() {
                    @Override
                    public void accept(HomeResponse homeResponse) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });



    }

    public static void doHttp( String platform, final IRetrofitCallback<HomeResponse> callback){
        RetrofitUtil.doHttp(RetrofitUtil.observable(HomeService.class)
                .getRxData(platform), callback);

    }



}