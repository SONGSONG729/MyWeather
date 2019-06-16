package com.example.myweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 和服务器端交互
 */
public class HttpUtil {

    /**
     * 发送请求并反馈
     * @param address  请求的地址
     * @param callback  返回数据
     */
    public static  void sendOkHttpRequests(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}
