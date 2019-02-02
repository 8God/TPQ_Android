package com.zcmedical.tangpangquan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.fragment.MyFansFragment;

public class MyFansActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_fans);

        init();
    }

    private void init() {
        initToolbar("粉丝");
        initUI();
    }

    private void initUI() {
        MyFansFragment myFansFragment = new MyFansFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_my_fans, myFansFragment).commit();
    }

}
