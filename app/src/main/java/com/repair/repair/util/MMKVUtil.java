package com.repair.repair.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.repair.repair.App;
import com.repair.repair.AppData;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;


public class MMKVUtil {
    static SharedPreferences sp;

    public static void init(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        getAllAppdata();
    }


    public static MMKV getMmkv() {
        return MMKV.defaultMMKV();
    }


    public static void syncAllAppdata() {
        String json = JSON.toJSONString(App.appData);
        getMmkv().putString("data", json);
    }

    public static void getAllAppdata() {
        String string = getMmkv().getString("data", "");

        KLog.e("取出保存的数据：" + string);
        if (TextUtils.isEmpty(string)) {
            App.appData = new AppData();
            return;
        }
        App.appData = JSON.parseObject(string, AppData.class);
    }
}
