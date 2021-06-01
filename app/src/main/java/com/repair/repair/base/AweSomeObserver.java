package com.repair.repair.base;


import com.alibaba.fastjson.JSONException;
import com.blankj.utilcode.util.ToastUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;
import rxhttp.wrapper.exception.ParseException;

/**
 * 消费器
 */
public abstract class AweSomeObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public final void onNext(T t) {
        //解决一个接口有时候返回json模型  有时候为null 报类型转换问题
        try {
            if (null != t && !t.equals("")) {
                _onNext(t);
            } else {
                _onNext(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onError(Throwable throwable) {
        String message;

        throwable.printStackTrace();

        int code = 1;//默认本地出错
        if (throwable instanceof SocketTimeoutException) {
            message = "网络连接超时";
        } else if (throwable instanceof HttpException) {
            message = " 请求失败，HttpException ";//404not
        } else if (throwable instanceof ParseException) {
            ParseException exception = (ParseException) throwable;
            message = " 请求失败: " + exception.getErrorCode() + "，" + exception.getMessage();//404not
        } else if (throwable instanceof ConnectException) {
            message = " 请求失败，ConnectException ";
        } else if (throwable instanceof UnknownHostException) {//可能是关闭了数据和wifi
            message = " 请求失败，可能是关闭了网络和wifi ";
        } else if (throwable instanceof TimeoutException) {
            message = "网络连接超时";
        } else if (throwable instanceof IOException) {
            message = "请求失败 IOException" + throwable.getMessage();
        } else if (throwable instanceof JSONException) {
            message = "数据解析出错 JSONException";
        } else if (throwable instanceof NullPointerException) {
            message = "数据出错 NullPointerException";

        } else {
            message = "未知错误：" + throwable.getMessage();
        }
        try {
            _onError(code, message);
            _onError(throwable);
            _onFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void _onError(Throwable throwable) {

    }

//    protected void _onTokenOut(TokenThrowable throwable) {
//        ((BaseApp) BaseApp.getContext()).tokenTimeOut();
//    }


    @Override
    public final void onComplete() {
        try {
            _onComplete();
            _onFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract void _onNext(T t);

    public void _onError(int code, String msg) {
        ToastUtils.showLong(msg);
    }


    public void _onComplete() {

    }

    public void _onFinish() {

    }
}