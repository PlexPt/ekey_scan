package com.repair.repair.base;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.SizeUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.OnSelectListener;
import com.socks.library.KLog;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class BaseActivity<T extends ViewBinding> extends RxAppCompatActivity {

    private MaterialDialog dialog;

    LoadingPopupView loadingPopupView;


    protected T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            binding = (T) method.invoke(null, getLayoutInflater());
            setContentView(binding.getRoot());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
//        SystemUIUtil.hideBottomUIMenu(this);
//        SystemUIUtil.hideNavigation(this);
    }


//    public void addRecyclerViewLine(RecyclerView rv, float h, @ColorRes int color) {
//        rv.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, SizeUtils.dp2px(h),
//                color));
//    }
//
//
//    public void addRecyclerViewLineGrid(RecyclerView rv, float h, float w, @ColorRes int color) {
//        rv.addItemDecoration(new GridItemDecoration(SizeUtils.dp2px(h), SizeUtils.dp2px(w), color, false));
//    }


    public void showDialog(String title) {

        loadingPopupView = new XPopup.Builder(this)
                .hasNavigationBar(false)
                .asLoading(title);
        loadingPopupView
                .show();

    }

    public void dismissDialog() {
        if (loadingPopupView != null && loadingPopupView.isShow()) {
            loadingPopupView.smartDismiss();
            loadingPopupView = null;
        }
    }



    /**
     * 普通方法(不需root适合大部分项目)
     */
    public static void hideBottomUIMenu(Activity context) {
        int flags;

        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        // must be executed in main thread :)
        context.getWindow().getDecorView().setSystemUiVisibility(flags);

    }

    public void addRecyclerViewLine(RecyclerView rv, float h, @ColorRes int color) {
        rv.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, SizeUtils.dp2px(h),
                color));
    }

    public void showDialogConfirm(CharSequence title, CharSequence content, OnConfirmListener confirmListener) {
        new XPopup.Builder(this)
                .hasNavigationBar(false)
                .asConfirm(title, content, confirmListener)
                .show();
    }

    public void showDialogBottomList(CharSequence title, String[] data, OnSelectListener selectListener) {
        // 这种弹窗从 1.0.0版本开始实现了优雅的手势交互和智能嵌套滚动
        new XPopup.Builder(this)
                .hasNavigationBar(false)
                .asBottomList(title, data, selectListener)
                .show();
    }

    public void showDialogCenterList(CharSequence title, String[] data, OnSelectListener selectListener) {
        new XPopup.Builder(this)
                .hasNavigationBar(false)
                .asCenterList(title, data, selectListener)
                .show();
    }
}
