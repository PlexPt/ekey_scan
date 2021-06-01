package com.repair.repair;


import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.GsonBuilder;
import com.repair.repair.ui.LoginActivity;
import com.repair.repair.util.MMKVUtil;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rxhttp.RxHttp;
import rxhttp.RxHttpPlugins;
import rxhttp.wrapper.converter.GsonConverter;


public class App extends MultiDexApplication {

    public static AppData appData = new AppData();

    @Override
    public void onCreate() {
        super.onCreate();

        //是否开启打印日志
        KLog.init(true);
//        XUI.init(this); //初始化UI框架
//        XUI.debug(true);  //开启UI框架调试日志
        MMKV.initialize(this);
        MMKVUtil.init(this);
        initOkhttp();

//        MMKVUtil.getAllAppdata();

    }


    private void initOkhttp() {

        // Log信息拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        //设置日志打印级别
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //将拦截器添加到ok中
        //设置读、写、连接超时时间为15s
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        RxHttpPlugins
                .setConverter(GsonConverter.create(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()));
        RxHttp.init(client);
    }

    public static void exitLogin() {
        App.appData.islogin = false;

        App.appData.user = null;

        MMKVUtil.syncAllAppdata();

        ActivityUtils.startActivity(LoginActivity.class);
    }


}
