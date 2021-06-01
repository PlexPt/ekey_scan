package com.repair.repair.ui;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.repair.repair.App;
import com.repair.repair.base.BaseActivity;
import com.repair.repair.databinding.ActivityRegUserBinding;
import com.repair.repair.dto.AdminOp;
import com.repair.repair.dto.UserDTO;
import com.repair.repair.util.MMKVUtil;

public class RegUserActivity extends BaseActivity<ActivityRegUserBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding.reg.setOnClickListener(v -> {
            boolean validate = validate(binding.mobile,
                    binding.password,
                    binding.name
            );
            if (!validate) {
                return;
            }

            UserDTO dto = new UserDTO();
            dto.setMobile(binding.mobile.getText().toString());
            dto.setName(binding.name.getText().toString());
            dto.setPassword(binding.password.getText().toString());
            dto.setType(0);

            App.appData.userDTOMap.put(binding.mobile.getText().toString(), dto);
            App.appData.user = dto;
            App.appData.islogin = true;

            App.appData.adminOps.add(AdminOp.getInstance("注册"));

            MMKVUtil.syncAllAppdata();

            ActivityUtils.startActivity(HomeActivity.class);

            finish();
        });
    }

    private boolean validate(EditText... mobile) {
        for (EditText editText : mobile) {
            if (TextUtils.isEmpty(editText.getText().toString())) {
                ToastUtils.showLong("请填写完整");
                return false;
            }
        }
        return true;

    }
}