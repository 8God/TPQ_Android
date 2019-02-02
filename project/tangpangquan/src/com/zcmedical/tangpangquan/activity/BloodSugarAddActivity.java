package com.zcmedical.tangpangquan.activity;

import hirondelle.date4j.DateTime;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.Header;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.db.BloodSugar;
import com.zcmedical.common.db.DbUtils;
import com.zcmedical.common.utils.DateUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;

public class BloodSugarAddActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "BloodSugarAddActivity";

    TextView tvBs;
    antistatic.spinnerwheel.WheelHorizontalView bigNum;
    antistatic.spinnerwheel.WheelHorizontalView smallNum;
    TextView tvDate;
    TimePicker tpTime;
    EditText etNote;
    DateTime dateTime;

    private static final int INIT_DATA_BIG = 7;
    private static final int INIT_DATA_SMALL = 0;

    private int current_big = INIT_DATA_BIG;
    private int current_small = INIT_DATA_SMALL;

    private int meal_type = 0;

    private AsyncHttpClient client;

    private int curHours = 0;
    int curMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bs);
        client = new AsyncHttpClient();
        mDbUtils = DbUtils.getInstance(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            meal_type = getIntent().getExtras().getInt("meal_type");
            dateTime = DateUtils.timestamp2DateTime(getIntent().getExtras().getInt("timestamp"));
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "meal_type : " + meal_type + "  dateTime:" + dateTime.toString());
            }
        } else {
            dateTime = DateTime.today(TimeZone.getDefault());
        }
        initUI();
        initToolbar();
    }

    private void initUI() {
        tvBs = findView(R.id.tvBs);
        bigNum = findView(R.id.bigNum);
        smallNum = findView(R.id.smallNum);
        tvDate = findView(R.id.tvDate);
        tpTime = findView(R.id.tpTime);
        etNote = findView(R.id.etNote);
        // set current time
        tpTime.setIs24HourView(true);
        Calendar c = Calendar.getInstance();
        switch (meal_type) {
        case BloodSugarActivity.MEAL_1_BEFORE:
            curHours = 7;
            break;
        case BloodSugarActivity.MEAL_1_AFTER:
            curHours = 9;
            break;
        case BloodSugarActivity.MEAL_2_BEFORE:
            curHours = 12;
            break;
        case BloodSugarActivity.MEAL_2_AFTER:
            curHours = 14;
            break;
        case BloodSugarActivity.MEAL_3_BEFORE:
            curHours = 18;
            break;
        case BloodSugarActivity.MEAL_3_AFTER:
            curHours = 20;
            break;
        case BloodSugarActivity.MEAL_4_BEFORE:
            curHours = 23;
            curMinutes = 16;
            break;
        case BloodSugarActivity.MEAL_4_AFTER:
            curHours = 23;
            curMinutes = 56;
            break;
        default:
            curHours = c.get(Calendar.HOUR_OF_DAY);
            curMinutes = c.get(Calendar.MINUTE);
            break;
        }
        tpTime.setCurrentHour(curHours);
        tpTime.setCurrentMinute(curMinutes);
        dateTime = new DateTime(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), curHours, curMinutes, 0, 0);
        tpTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                //Log.d(TAG, "hourOfDay : " + hourOfDay + "  minute: " + minute);
                curHours = hourOfDay;
                curMinutes = minute;
            }
        });

        //

        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(this, 0, 99, "%02d");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        bigNum.setViewAdapter(hourAdapter);

        NumericWheelAdapter minAdapter = new NumericWheelAdapter(this, 0, 9, "%01d");
        minAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        minAdapter.setItemTextResource(R.id.text);
        smallNum.setViewAdapter(minAdapter);

        bigNum.setCurrentItem(INIT_DATA_BIG);
        smallNum.setCurrentItem(INIT_DATA_SMALL);
        // add listeners
        addChangingListener(bigNum, "min");
        addChangingListener(smallNum, "hour");

        tvDate.setText("测量时间: " + dateTime.format("YYYY年MM月DD日"));
        tvBs.setText(INIT_DATA_BIG + "." + INIT_DATA_SMALL);
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
            actionBar.setTitle("血糖");
        }
    }

    private void addChangingListener(final AbstractWheel wheel, final String label) {
        wheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                Log.d(TAG, "oldValue : " + oldValue + "  newValue: " + newValue);
                switch (wheel.getId()) {
                case R.id.bigNum:
                    current_big = newValue;
                    break;
                case R.id.smallNum:
                    current_small = newValue;

                default:
                    break;
                }
                tvBs.setText(current_big + "." + current_small);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blood_sugar_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //    ID id
    //    用户ID user_id
    //    血糖值 blood_sugar
    //    测量时间 measure_time
    //    备注 remarks 【不是必填】
    //    创建时间 created_at
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_save:
            dateTime = new DateTime(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), curHours, curMinutes, 0, 0);
            showProgressDialog();
            RequestParams params = new RequestParams();
            params.put("user_id", TpqApplication.getInstance().getUserId());
            params.put("blood_sugar", Float.parseFloat((current_big + current_small / 10) + ""));
            params.put("measure_time", DateUtils.dateTime2Timestamp(dateTime));
            params.put("meal_type", meal_type);
            if (!TextUtils.isEmpty(etNote.getText().toString())) {
                params.put("remarks", etNote.getText().toString());
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "params : " + params.toString());
            }
            client.post(InterfaceConstant.BLOOD_SUGAR_CREAT, params, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    dimissProgressDialog();
                    showToast("当前网络状况不佳，请稍后再试！");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "response : " + response.toString());
                    if (JsonUtils.getStatus(response.toString()) != 0) {
                        showToast(JsonUtils.getSthObjectString(response.toString(), "message"));
                        return;
                    }
                    Type type = new TypeToken<BloodSugar>() {
                    }.getType();
                    Gson gson = new Gson();
                    BloodSugar bs = gson.fromJson((JsonUtils.getOjectString((response.toString()), "blood_sugar")), type);
                    mDbUtils.insertNewBloodSugar(bs);
                    dimissProgressDialog();
                    showToast("添加成功！");
                    finish();
                    super.onSuccess(statusCode, headers, response);
                }

            });
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}
