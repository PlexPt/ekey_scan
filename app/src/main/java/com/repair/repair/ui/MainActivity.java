package com.repair.repair.ui;


import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.repair.repair.App;
import com.repair.repair.base.AweSomeObserver;
import com.repair.repair.base.BaseActivity;
import com.repair.repair.databinding.ActivityMainBinding;
import com.repair.repair.service.MyAccessibilityService;
import com.repair.repair.util.ASTool;
import com.repair.repair.util.MMKVUtil;

import lombok.SneakyThrows;
import me.goldze.mvvmhabit.utils.RxUtils;
import rxhttp.RxHttp;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    boolean status = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.settings.setOnClickListener(v -> {
            ActivityUtils.startActivity(ConnectActivity.class);
        });

        binding.fuzu.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });

        binding.permission.setOnClickListener(v -> {
            ASTool.gosetting(this);
        });
        binding.run.setOnClickListener(v -> {
//            ActivityUtils.startActivity("com.netease.gl", "com.netease.gl.ui.activity.MkeyActivity");
            AppUtils.launchApp("com.netease.gl");
        });
        binding.run2.setOnClickListener(v -> {
            AppUtils.launchApp("com.netease.mkey");
//            ActivityUtils.startActivity("com.netease.mkey", "com.netease.mkey.activity.NtSecActivity");
        });

        binding.exit.setOnClickListener(v -> {
            System.exit(0);
        });

    }

    public void onResume() {
        super.onResume();
        if (!ASTool.checkFloatPermission(this)) {
            showAlertWindowPermissionDialog();
        } else if (!ASTool.m3a(this, MyAccessibilityService.class)) {
            showSettingDialog();
        } else {
            sendBroadcast(new Intent(MyAccessibilityService.ACTION_ADD_WINDOWS));
        }

        MMKVUtil.getAllAppdata();

        binding.tvServer.setText("服务器地址：" + App.appData.server);

        checkserver();

    }

    @SneakyThrows
    private void checkserver() {
        RxHttp.get(App.appData.server + "api/token/status")
                .connectTimeout(2000)  //连接超时3s
                .asString()
                .compose(bindToLifecycle()) //请求与View周期同步
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .doOnSubscribe(e -> showDialog("检查中"))
                .subscribe(new AweSomeObserver<String>() {
                    @Override
                    public void _onNext(String s) {
                        if (s == "1") {
                            status = true;
                            binding.tvStatus.setText("服务器状态：ok");

                        }
                    }

                    @Override
                    protected void _onError(Throwable throwable) {
                        super._onError(throwable);
                        status = false;
                        binding.tvStatus.setText("服务器状态：连接失败");

                    }

                    @Override
                    public void _onFinish() {
                        super._onFinish();
                        dismissDialog();
                    }
                });
    }


    private void showAlertWindowPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("未授予悬浮窗权限")
                .setMessage("用于浮动在所有应用程序之上,未授权可能有些手机无法看到主悬浮窗界面.")
                .setPositiveButton("去设置", (dialogInterface, i) -> {
                    ASTool.requestFloatPermission(this, 666);

//                    go(MainActivity.this);

                    dialogInterface.dismiss();
                })

                .setCancelable(false)
                .show();
    }


    private static String miuiversion() {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getDeclaredMethod("get", String.class, String.class).invoke(cls, "ro.miui.ui.version.name", null);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    private static void gotoappsetting(Context context) {
        int i = Build.VERSION.SDK_INT;
        Intent intent = new Intent();
        if (i >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else {
            String str = i == 8 ? "pkg" : "com.android.settings.ApplicationPkgName";
            intent.setAction("android.intent.action.VIEW");
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra(str, context.getPackageName());
        }
        context.startActivity(intent);
    }


    private void showSettingDialog() {
        new AlertDialog.Builder(this).setTitle("未授予辅助服务权限")
                .setMessage("未开启辅助(无障碍)服务权限,未授权则无法使用,是否现在开启?")
                .setPositiveButton("去设置", (dialogInterface, i) -> {
                    gotoasset(this, MyAccessibilityService.class);
                    dialogInterface.dismiss();
                }).setCancelable(false).show();
    }

    public static void gotoasset(Context context, Class<? extends AccessibilityService> cls) {
        if (!m4a(context, context.getPackageName() + "/" + cls.getCanonicalName())) {
            context.startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
        }
    }

    private static boolean m4a(Context context, String str) {
        int i;
        String string;
        try {
            i = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException e) {
            i = 0;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        if (i != 1 || (string = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), "enabled_accessibility_services")) == null) {
            return false;
        }
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            if (simpleStringSplitter.next().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

}