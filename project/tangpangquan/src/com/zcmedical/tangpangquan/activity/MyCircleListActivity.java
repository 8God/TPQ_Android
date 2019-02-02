package com.zcmedical.tangpangquan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.fragment.MyCircleListFragment;

public class MyCircleListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_circle_list);

        init();
    }

    private void init() {
        initToolbar("我的圈子");
        initUI();
    }

    private void initUI() {
        MyCircleListFragment myCircleListFragment = new MyCircleListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_my_circle_list, myCircleListFragment).commit();
    }

}
