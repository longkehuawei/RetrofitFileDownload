package com.hengda.zwf.httputil;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RequestApi {

    public static final int DEFAULT_TIMEOUT = 5;
    public static final String HTTP_STATUS_SUCCEED_NEW = "1";
    public static final String HTTP_STATUS_SUCCEED_OLD = "000";
    public Retrofit retrofit;

    public RequestApi(String baseUrl) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public <T> void doSubscribe(Subscriber<T> subscriber, Observable<Response<T>> observable) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (TextUtils.equals(HTTP_STATUS_SUCCEED_OLD, response.getStatus())
                            || TextUtils.equals(HTTP_STATUS_SUCCEED_NEW, response.getStatus())) {
                        return response.getData();
                    } else {
                        throw new RequestException(response.getMsg());
                    }
                })
                .subscribe(subscriber);
    }

}
