package com.zcmedical.tangpangquan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.fragment.MyFollowFragment;

public class MyFollowActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_follow);

        init();
    }

    private void init() {
        initToolbar("关注");
        initUI();
    }

    private void initUI() {
        MyFollowFragment myFollowFragment = new MyFollowFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_my_follow, myFollowFragment).commit();
    }

}
