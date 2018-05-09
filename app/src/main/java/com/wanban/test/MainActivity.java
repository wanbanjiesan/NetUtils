package com.wanban.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wanban.retrofit.IRetrofitCallback;
import com.wanban.test.response.HomeResponse;

/**
 * Created by fq on 2018/4/4.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeResponse.doHttpHomeData("platform", new IRetrofitCallback<HomeResponse>() {
            @Override
            public void onSuccess(HomeResponse homeResponse) {

            }

            @Override
            public void onError(Throwable t) {

            }
        });

        HomeResponse.doHttp("asdfaf", new IRetrofitCallback<HomeResponse>() {
            @Override
            public void onSuccess(HomeResponse homeResponse) {
                Log.e("TAG", "123123123  ");
            }

            @Override
            public void onError(Throwable t) {
                Log.e("TAG", "onError  ");
            }
        });
    }
}
