package com.repair.repair.ui;


import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.repair.repair.App;
import com.repair.repair.base.BaseActivity;
import com.repair.repair.databinding.ActivityLoginBinding;
import com.repair.repair.dto.AdminOp;
import com.repair.repair.dto.UserDTO;
import com.repair.repair.util.MMKVUtil;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (App.appData.islogin) {
            if (App.appData.user.getType() == 0) {
                ActivityUtils.startActivity(HomeActivity.class);
            } else {
//                ActivityUtils.startActivity(HomeAdminActivity.class);
            }
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.login.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.username.getText().toString())) {
                ToastUtils.showLong("请输入");
                return;
            }
            if (TextUtils.isEmpty(binding.password.getText().toString())) {
                ToastUtils.showLong("请输入密码");
                return;
            }
            UserDTO userDTO = App.appData.userDTOMap.get(binding.username.getText().toString());
            if (userDTO == null) {
                ToastUtils.showLong("用户不存在");
                return;
            }
            if (userDTO.getPassword().equals(binding.password.getText().toString())) {
                ToastUtils.showLong("登录成功");
                App.appData.user = userDTO;
                MMKVUtil.syncAllAppdata();

                if (App.appData.user.getType() == 0) {
                    AdminOp op = new AdminOp().setId(userDTO.getMobile())
                            .setUsername(userDTO.getName())
                            .setContent("登录");
                    App.appData.adminOps.add(op);
                    ActivityUtils.startActivity(HomeActivity.class);
                } else {
//                    ActivityUtils.startActivity(HomeAdminActivity.class);
                }
                MMKVUtil.syncAllAppdata();

                finish();
                return;
            }
            ToastUtils.showLong("密码错误");
        });
        binding.reg.setOnClickListener(v -> {
            ActivityUtils.startActivity(RegUserActivity.class);
        });
    }
}