package com.zcmedical.tangpangquan.activity;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.utils.NotifyUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.push.AlarmReceiver;
import com.zcmedical.tangpangquan.view.PushSlideSwitchView;
import com.zcmedical.tangpangquan.view.PushSlideSwitchView.OnSwitchChangedListener;

public class RemindDetailsActivity extends BaseActivity implements OnClickListener, OnSwitchChangedListener, OnDismissListener {

    private static final String TAG = "RemindDetailsActivity";

    private String title = "设置提醒";
    private int type = 0;

    public static final int TYPE_WEIGHT = 0;
    public static final int TYPE_BLOOD_SUGAR = 1;
    public static final int TYPE_EVERYDAY = 2;

    private RelativeLayout rlTpye;
    private ScrollView svTpye;

    //type 1:
    private TextView tvTime;

    //type 2:
    private TextView tvContent1;
    private TextView tvContent2;
    private TextView tvContent3;
    private TextView tvContent4;
    private TextView tvContent5;
    private TextView tvContent6;
    private TextView tvContent7;
    private TextView tvContent8;

    private PushSlideSwitchView mPushSlideSwitchView1;
    private PushSlideSwitchView mPushSlideSwitchView2;
    private PushSlideSwitchView mPushSlideSwitchView3;
    private PushSlideSwitchView mPushSlideSwitchView4;
    private PushSlideSwitchView mPushSlideSwitchView5;
    private PushSlideSwitchView mPushSlideSwitchView6;
    private PushSlideSwitchView mPushSlideSwitchView7;
    private PushSlideSwitchView mPushSlideSwitchView8;

    private TimePickerDialog mTpd;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_details);
        if (getIntent() != null && getIntent().getExtras() != null) {
            type = getIntent().getExtras().getInt("type");
        }
        initUI();
        initToolbar();
    }

    private void initUI() {
        svTpye = findView(R.id.svTpye);
        rlTpye = findView(R.id.rlTpye);
        rlTpye.setVisibility(type == TYPE_BLOOD_SUGAR ? View.GONE : View.VISIBLE);
        svTpye.setVisibility(type == TYPE_BLOOD_SUGAR ? View.VISIBLE : View.GONE);

        tvTime = findView(R.id.tvTime);

        tvContent1 = findView(R.id.tvContent1);
        tvContent2 = findView(R.id.tvContent2);
        tvContent3 = findView(R.id.tvContent3);
        tvContent4 = findView(R.id.tvContent4);
        tvContent5 = findView(R.id.tvContent5);
        tvContent6 = findView(R.id.tvContent6);
        tvContent7 = findView(R.id.tvContent7);
        tvContent8 = findView(R.id.tvContent8);

        mPushSlideSwitchView1 = findView(R.id.mPushSlideSwitchView1);
        mPushSlideSwitchView2 = findView(R.id.mPushSlideSwitchView2);
        mPushSlideSwitchView3 = findView(R.id.mPushSlideSwitchView3);
        mPushSlideSwitchView4 = findView(R.id.mPushSlideSwitchView4);
        mPushSlideSwitchView5 = findView(R.id.mPushSlideSwitchView5);
        mPushSlideSwitchView6 = findView(R.id.mPushSlideSwitchView6);
        mPushSlideSwitchView7 = findView(R.id.mPushSlideSwitchView7);
        mPushSlideSwitchView8 = findView(R.id.mPushSlideSwitchView8);

        rlTpye.setOnClickListener(this);

        mPushSlideSwitchView1.setOnChangeListener(this);
        mPushSlideSwitchView2.setOnChangeListener(this);
        mPushSlideSwitchView3.setOnChangeListener(this);
        mPushSlideSwitchView4.setOnChangeListener(this);
        mPushSlideSwitchView5.setOnChangeListener(this);
        mPushSlideSwitchView6.setOnChangeListener(this);
        mPushSlideSwitchView7.setOnChangeListener(this);
        mPushSlideSwitchView8.setOnChangeListener(this);

        tvContent1.setOnClickListener(this);
        tvContent2.setOnClickListener(this);
        tvContent3.setOnClickListener(this);
        tvContent4.setOnClickListener(this);
        tvContent5.setOnClickListener(this);
        tvContent6.setOnClickListener(this);
        tvContent7.setOnClickListener(this);
        tvContent8.setOnClickListener(this);
    }

    private void changeUI() {
        switch (type) {
        case TYPE_WEIGHT:
            title = "体重录入提醒";
            tvTime.setText("每天 " + spf.getString("Remind_Weight_Time", "08:00"));
            break;
        case TYPE_BLOOD_SUGAR:
            title = "血糖录入提醒";
            tvContent1.setText(spf.getString("Remind_Bs_Time_1", "07:00"));
            tvContent2.setText(spf.getString("Remind_Bs_Time_2", "09:00"));
            tvContent3.setText(spf.getString("Remind_Bs_Time_3", "12:00"));
            tvContent4.setText(spf.getString("Remind_Bs_Time_4", "14:00"));
            tvContent5.setText(spf.getString("Remind_Bs_Time_5", "18:00"));
            tvContent6.setText(spf.getString("Remind_Bs_Time_6", "20:00"));
            tvContent7.setText(spf.getString("Remind_Bs_Time_7", "23:16"));
            tvContent8.setText(spf.getString("Remind_Bs_Time_8", "23:56"));
            mPushSlideSwitchView1.setChecked(spf.getBoolean("Remind_Bs_1", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView2.setChecked(spf.getBoolean("Remind_Bs_2", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView3.setChecked(spf.getBoolean("Remind_Bs_3", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView4.setChecked(spf.getBoolean("Remind_Bs_4", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView5.setChecked(spf.getBoolean("Remind_Bs_5", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView6.setChecked(spf.getBoolean("Remind_Bs_6", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView7.setChecked(spf.getBoolean("Remind_Bs_7", AlarmReceiver.DEFAULT_STATUS));
            mPushSlideSwitchView8.setChecked(spf.getBoolean("Remind_Bs_8", AlarmReceiver.DEFAULT_STATUS));
            break;
        case TYPE_EVERYDAY:
            title = "每日计划提醒";
            tvTime.setText("每天 " + spf.getString("Remind_Everyday_Time", "09:00"));
            break;
        default:
            break;
        }
        if (null != actionBar) {
            actionBar.setTitle(title);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_remind_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_rili:
            //NotifyUtil.addAppNotiFy(this, "测试提醒", "你妹啊，你有一个提醒啊", CommonConstant.REMIND_COMMUNITY);
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeUI();
    }

    @Override
    public void onClick(final View v) {
        mTpd = new TimePickerDialog(this, new OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = hourOfDay < 10 ? "0" + hourOfDay : hourOfDay + "";
                String min = minute < 10 ? "0" + minute : minute + "";
                switch (v.getId()) {
                case R.id.rlTpye:
                    if (type == TYPE_WEIGHT) {
                        spf.edit().putString("Remind_Weight_Time", hour + ":" + min).commit();
                    } else {
                        spf.edit().putString("Remind_Everyday_Time", hour + ":" + min).commit();
                    }
                    break;
                case R.id.tvContent1:
                    spf.edit().putString("Remind_Bs_Time_1", hour + ":" + min).commit();
                    break;
                case R.id.tvContent2:
                    spf.edit().putString("Remind_Bs_Time_2", hour + ":" + min).commit();
                    break;
                case R.id.tvContent3:
                    spf.edit().putString("Remind_Bs_Time_3", hour + ":" + min).commit();
                    break;
                case R.id.tvContent4:
                    spf.edit().putString("Remind_Bs_Time_4", hour + ":" + min).commit();
                    break;
                case R.id.tvContent5:
                    spf.edit().putString("Remind_Bs_Time_5", hour + ":" + min).commit();
                    break;
                case R.id.tvContent6:
                    spf.edit().putString("Remind_Bs_Time_6", hour + ":" + min).commit();
                    break;
                case R.id.tvContent7:
                    spf.edit().putString("Remind_Bs_Time_7", hour + ":" + min).commit();
                    break;
                case R.id.tvContent8:
                    spf.edit().putString("Remind_Bs_Time_8", hour + ":" + min).commit();
                    break;
                default:
                    break;
                }

            }
        }, 12, 0, true);
        mTpd.setTitle("设置提醒时间");
        mTpd.setOnDismissListener(this);
        mTpd.show();
    }

    @Override
    public void onSwitchChange(PushSlideSwitchView switchView, boolean isChecked) {
        switch (switchView.getId()) {
        case R.id.mPushSlideSwitchView1:
            spf.edit().putBoolean("Remind_Bs_1", !spf.getBoolean("Remind_Bs_1", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView2:
            spf.edit().putBoolean("Remind_Bs_2", !spf.getBoolean("Remind_Bs_2", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView3:
            spf.edit().putBoolean("Remind_Bs_3", !spf.getBoolean("Remind_Bs_3", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView4:
            spf.edit().putBoolean("Remind_Bs_4", !spf.getBoolean("Remind_Bs_4", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView5:
            spf.edit().putBoolean("Remind_Bs_5", !spf.getBoolean("Remind_Bs_5", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView6:
            spf.edit().putBoolean("Remind_Bs_6", !spf.getBoolean("Remind_Bs_6", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView7:
            spf.edit().putBoolean("Remind_Bs_7", !spf.getBoolean("Remind_Bs_7", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;
        case R.id.mPushSlideSwitchView8:
            spf.edit().putBoolean("Remind_Bs_8", !spf.getBoolean("Remind_Bs_8", AlarmReceiver.DEFAULT_STATUS)).commit();
            break;

        default:
            break;
        }
        changeUI();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        changeUI();
    }

}
