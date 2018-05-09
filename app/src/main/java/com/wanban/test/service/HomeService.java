package com.wanban.test.service;

import com.wanban.test.response.HomeResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by fq on 2018/4/4.
 */

public interface HomeService  {

//    @GET("index2?format=json")
//    Call<HomeResponse> getHomeData(@Query("platform") String platform);


    @GET("index2?format=json")
    Call<HomeResponse> getData(@Query("platform")String platform);


    @GET("index2?format=json")
    Observable<HomeResponse> getRxData(@Query("platform")String platform);



}
