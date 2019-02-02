package com.zcmedical.tangpangquan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.utils.NotifyUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.push.AlarmReceiver;
import com.zcmedical.tangpangquan.view.PushSlideSwitchView;
import com.zcmedical.tangpangquan.view.PushSlideSwitchView.OnSwitchChangedListener;

public class RemindMainActivity extends BaseActivity implements OnClickListener, OnSwitchChangedListener {

    private PushSlideSwitchView mPushSlideSwitchView1;//体重
    private PushSlideSwitchView mPushSlideSwitchView2;//血糖
    private PushSlideSwitchView mPushSlideSwitchView3;//每天计划
    private PushSlideSwitchView mPushSlideSwitchView4;//医生问答
    private PushSlideSwitchView mPushSlideSwitchView5;//社区消息 
    private TextView tvTime1;//体重
    private TextView tvTime2;//血糖
    private TextView tvTime3;//每天计划 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_main);
        initUI();
        initToolbar();
    }

    private void initUI() {
        mPushSlideSwitchView1 = findView(R.id.mPushSlideSwitchView1);
        mPushSlideSwitchView2 = findView(R.id.mPushSlideSwitchView2);
        mPushSlideSwitchView3 = findView(R.id.mPushSlideSwitchView3);
        mPushSlideSwitchView4 = findView(R.id.mPushSlideSwitchView4);
        mPushSlideSwitchView5 = findView(R.id.mPushSlideSwitchView5);
        tvTime1 = findView(R.id.tvTime1);
        tvTime2 = findView(R.id.tvTime2);
        tvTime3 = findView(R.id.tvTime3);
        mPushSlideSwitchView1.setOnChangeListener(this);
        mPushSlideSwitchView2.setOnChangeListener(this);
        mPushSlideSwitchView3.setOnChangeListener(this);
        mPushSlideSwitchView4.setOnChangeListener(this);
        mPushSlideSwitchView5.setOnChangeListener(this);
        tvTime1.setOnClickListener(this);
        tvTime2.setOnClickListener(this);
        tvTime3.setOnClickListener(this);
    }

    private void changeSwitchUI() {
        mPushSlideSwitchView1.setChecked(spf.getBoolean("mPushSlideSwitchView1", AlarmReceiver.DEFAULT_STATUS));
        mPushSlideSwitchView2.setChecked(spf.getBoolean("mPushSlideSwitchView2", AlarmReceiver.DEFAULT_STATUS));
        mPushSlideSwitchView3.setChecked(spf.getBoolean("mPushSlideSwitchView3", AlarmReceiver.DEFAULT_STATUS));
        mPushSlideSwitchView4.setChecked(spf.getBoolean("mPushSlideSwitchView4", AlarmReceiver.DEFAULT_STATUS));
        mPushSlideSwitchView5.setChecked(spf.getBoolean("mPushSlideSwitchView5", AlarmReceiver.DEFAULT_STATUS));
        NotifyUtil.setTaskAlarm(this, CommonConstant.REMIND_WEIGHT, spf.getBoolean("mPushSlideSwitchView1", !AlarmReceiver.DEFAULT_STATUS));
        for (int i = 1; i <= 8; i++) {
            String key = "Remind_Bs_Time_" + i;
            int taskid = CommonConstant.REMIND_BLOOD_SUGAR_1 + i - 1;
            NotifyUtil.setTaskAlarm(this, taskid, spf.getBoolean("mPushSlideSwitchView1", !AlarmReceiver.DEFAULT_STATUS) ? spf.getBoolean(key, !AlarmReceiver.DEFAULT_STATUS) : false);
        }
        NotifyUtil.setTaskAlarm(this, CommonConstant.REMIND_EVERYDAY, spf.getBoolean("mPushSlideSwitchView3", !AlarmReceiver.DEFAULT_STATUS));
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
            actionBar.setTitle("我的提醒");
        }
    }

    @Override
    public void onClick(View v) {
        int type = 0;
        switch (v.getId()) {
        case R.id.tvTime1:
            type = RemindDetailsActivity.TYPE_WEIGHT;
            break;
        case R.id.tvTime2:
            type = RemindDetailsActivity.TYPE_BLOOD_SUGAR;
            break;
        case R.id.tvTime3:
            type = RemindDetailsActivity.TYPE_EVERYDAY;
            break;
        default:
            break;
        }
        startActivity(new Intent(RemindMainActivity.this, RemindDetailsActivity.class).putExtra("type", type));
    }

    @Override
    public void onSwitchChange(PushSlideSwitchView switchView, boolean isChecked) {
        switch (switchView.getId()) {
        case R.id.mPushSlideSwitchView1:
            spf.edit().putBoolean("mPushSlideSwitchView1", !spf.getBoolean("mPushSlideSwitchView1", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView2:
            spf.edit().putBoolean("mPushSlideSwitchView2", !spf.getBoolean("mPushSlideSwitchView2", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView3:
            spf.edit().putBoolean("mPushSlideSwitchView3", !spf.getBoolean("mPushSlideSwitchView3", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView4:
            spf.edit().putBoolean("mPushSlideSwitchView4", !spf.getBoolean("mPushSlideSwitchView4", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView5:
            spf.edit().putBoolean("mPushSlideSwitchView5", !spf.getBoolean("mPushSlideSwitchView5", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        default:
            break;
        }
        changeSwitchUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeSwitchUI();
    }

}
