package com.repair.repair.ui;

import android.os.Bundle;
import com.blankj.utilcode.util.ActivityUtils;
import com.repair.repair.base.BaseActivity;
import com.repair.repair.databinding.ActivityFlashBinding;

public class FlashActivity extends BaseActivity<ActivityFlashBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.buttonFirst.setOnClickListener(v -> {
            ActivityUtils.startActivity(LoginActivity.class);
            finish();
        });
    }
}