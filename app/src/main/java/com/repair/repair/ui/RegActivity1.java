package com.repair.repair.ui;


import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.repair.repair.base.BaseActivity;
import com.repair.repair.databinding.ActivityReg1Binding;

public class RegActivity1 extends BaseActivity<ActivityReg1Binding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding.reg1.setOnClickListener(v->{
            ActivityUtils.startActivity(RegUserActivity.class);

        });
        binding.reg2.setOnClickListener(v -> {
            ActivityUtils.startActivity(RegWorkActivity.class);
        });
    }
}