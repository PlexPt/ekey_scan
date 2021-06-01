package com.repair.repair.util;


import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ASTool {
    /* renamed from: a */
    private static int m0a(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 19) {
            Object systemService = context.getSystemService("appops");
            Class<?> cls = systemService.getClass();
            try {
                return ((Integer) cls.getDeclaredMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class)
                        .invoke(systemService, Integer.valueOf(i), Integer.valueOf(Binder.getCallingUid()), context.getPackageName())).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    /***
     * 检查悬浮窗开启权限
     * @param context
     * @return
     */
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    /**
     * 悬浮窗开启权限
     * @param context
     * @param requestCode
     */
    public static void requestFloatPermission(Activity context, int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivityForResult(intent, requestCode);
    }

    /* renamed from: a */
    public static boolean m1a() {
        return "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER);
    }

    /* renamed from: a */
    public static boolean m2a(Context context) {
        return m0a(context, 24) != 1;
    }

    /* renamed from: a */
    public static boolean m3a(Context context, Class<? extends AccessibilityService> cls) {
        return m4a(context, context.getPackageName() + "/" + cls.getCanonicalName());
    }

    /* renamed from: a */
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

    /* renamed from: b */
    private static String m5b() {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getDeclaredMethod("get", String.class, String.class).invoke(cls, "ro.miui.ui.version.name", null);
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static void gosetting(Context context) {
        if (m1a()) {
            try {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                if ("V5".equals(m5b())) {
                    PackageInfo packageInfo = null;
                    try {
                        packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                    intent.setClassName("com.miui.securitycenter", "com.miui.securitycenter.permission.AppPermissionsEditor");
                    intent.putExtra("extra_package_uid", packageInfo.applicationInfo.uid);
                } else {
                    intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                    intent.putExtra("extra_pkgname", context.getPackageName());
                }
                context.startActivity(intent);
            } catch (Exception e2) {
                m8c(context);
            }
        } else {
            m8c(context);
        }
    }

    /* renamed from: b */
    public static void m7b(Context context, Class<? extends AccessibilityService> cls) {
        if (!m4a(context, context.getPackageName() + "/" + cls.getCanonicalName())) {
            context.startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
        }
    }

    /* renamed from: c */
    private static void m8c(Context context) {
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
}