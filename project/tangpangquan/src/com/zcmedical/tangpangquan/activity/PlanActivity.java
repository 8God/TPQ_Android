package com.zcmedical.tangpangquan.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.fragment.PlanListFragment;

public class PlanActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_list);

        init();
    }

    private void init() {
        initToolbar("计划");
        initUI();
    }

    private void initUI() {
        PlanListFragment planListFragment = new PlanListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, planListFragment).commit();
    }
}
