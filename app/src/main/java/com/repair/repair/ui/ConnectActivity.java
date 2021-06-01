package com.repair.repair.ui;

import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.repair.repair.App;
import com.repair.repair.base.AweSomeObserver;
import com.repair.repair.base.BaseActivity;
import com.repair.repair.databinding.ActivityConnectBinding;
import com.repair.repair.util.MMKVUtil;

import me.goldze.mvvmhabit.utils.RxUtils;
import rxhttp.RxHttp;

public class ConnectActivity extends BaseActivity<ActivityConnectBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.pan.setText("地址: " + App.appData.server);
        binding.ip.setText(App.appData.ip);
        binding.port.setText(App.appData.port + "");

        binding.connect.setOnClickListener(v -> {

            String ip = binding.ip.getText().toString();
            String port = binding.port.getText().toString();
            if (TextUtils.isEmpty(ip)) {
                ToastUtils.showLong("请输入ip");
                return;
            }
            if (TextUtils.isEmpty(port)) {
                ToastUtils.showLong("请输入端口");
                return;
            }
            if (!TextUtils.isDigitsOnly(port)) {
                ToastUtils.showLong("请输入数字端口");
                return;
            }

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
                                ToastUtils.showLong("连接成功");
                            }
                        }

                        @Override
                        protected void _onError(Throwable throwable) {
                            super._onError(throwable);
                            ToastUtils.showLong("连接失败");
                        }

                        @Override
                        public void _onFinish() {
                            super._onFinish();
                            dismissDialog();
                        }
                    });

        });

        binding.save.setOnClickListener(v -> {
            String ip = binding.ip.getText().toString();
            String port = binding.port.getText().toString();
            if (TextUtils.isEmpty(ip)) {
                ToastUtils.showLong("请输入ip");
                return;
            }
            if (TextUtils.isEmpty(port)) {
                ToastUtils.showLong("请输入端口");
                return;
            }
            if (!TextUtils.isDigitsOnly(port)) {
                ToastUtils.showLong("请输入数字端口");
                return;
            }

            App.appData.ip = ip;
            App.appData.port = Integer.parseInt(port);

            App.appData.server = "http://" + ip + ":" + port + "/";

            MMKVUtil.syncAllAppdata();

            ToastUtils.showLong("保存成功");

            finish();

        });
    }
}