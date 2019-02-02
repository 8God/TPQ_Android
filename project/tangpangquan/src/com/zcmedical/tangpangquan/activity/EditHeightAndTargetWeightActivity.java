package com.zcmedical.tangpangquan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;

public class EditHeightAndTargetWeightActivity extends BaseActivity {

    private static final String TAG = "EditHeightAndTargetWeightActivity";
    TextView tvW;
    TextView tvH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_h_w);
        initToolbar();
        tvH = findView(R.id.tvH);
        tvW = findView(R.id.tvW);
    }

    private void initToolbar() {
        Toolbar toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setTitle("BMI");
        }
    }

    public void editW(View v) {
        startActivity(new Intent(this, RecordWeightActivity.class).putExtra("isEdit", true));
    }

    public void editH(View v) {
        startActivity(new Intent(this, RecordHeightActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvH.setText(spf.getInt("height", 0) + " cm");
        tvW.setText(spf.getInt("target_weight", 0) + " kg");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
