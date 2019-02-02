package com.zcmedical.tangpangquan.activity;

import android.os.Bundle;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        init();
    }

    private void init() {
        initToolbar(getString(R.string.title_about_us_activity));
    }

}
