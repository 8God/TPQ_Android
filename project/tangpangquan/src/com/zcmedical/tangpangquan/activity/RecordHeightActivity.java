package com.zcmedical.tangpangquan.activity;

import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserInfo;
import com.zcmedical.tangpangquan.view.WheelView;

public class RecordHeightActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "RecordHeightActivity";
    private Button btn1, btn2;
    private static final int LENGTH = 80;
    private static final int LOWEST = 140;
    private static final int SELECT = 30;
    private static final String[] PLANETS = new String[LENGTH];
    private WheelView wva;
    private float height = 0;
    private boolean init = false;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_height);
        if (getIntent() != null && getIntent().getExtras() != null) {
            init = getIntent().getExtras().getBoolean("init", false);
        }
        height = spf.getInt("height", 0);
        client = new AsyncHttpClient();
        initUI();
        initToolbar();
    }

    private void initUI() {
        wva = findView(R.id.wheelView);
        btn1 = findView(R.id.btn1);
        btn2 = findView(R.id.btn2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        if (!init) {
            btn1.setText("取消");
            btn2.setText("保存");
        }

        for (int i = 0; i < LENGTH; i++) {
            PLANETS[i] = "" + (LOWEST + i);
        }

        wva.setOffset(2);
        wva.setItems(Arrays.asList(PLANETS));
        wva.setSeletion(SELECT);
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                height = Integer.parseInt(item);
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
        height = Integer.parseInt(wva.getSeletedItem());
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
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
            actionBar.setTitle("记录身高体重");
        }
    }

    private void uploadHeight() {
        RequestParams params = new RequestParams();
        params.put("id", TpqApplication.getInstance().getUserId());
        params.put("height", height);
        client.post(InterfaceConstant.API_USER_UPDATE, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "response : " + errorResponse.toString());
                RecordHeightActivity.this.dimissProgressDialog();
                RecordHeightActivity.this.showToast("网络连接状态不佳，请重试~");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                RecordHeightActivity.this.dimissProgressDialog();
                Log.d(TAG, "response : " + response.toString());
                Type type = new TypeToken<UserInfo>() {
                }.getType();
                Gson gson = new Gson();
                UserInfo userInfo = gson.fromJson((JsonUtils.getOjectString((response.toString()), "user")), type);
                spf.edit().putInt("height", (int) height).commit();
                Log.d(TAG, "" + userInfo.toString());
                DialogUtils.showAlertDialog(RecordHeightActivity.this, "修改成功", "确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RecordHeightActivity.this.finish();
                    }

                });
                super.onSuccess(statusCode, headers, response);
            }

        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn1:
            finish();
            break;

        case R.id.btn2:
            if (init) {
                //初始化的时候
                startActivity(new Intent(this, RecordWeightActivity.class).putExtra("height", height).putExtra("init", true));
            } else {
                showProgressDialog();
                //修改身高，保存
                uploadHeight();
            }
            break;

        default:
            break;
        }
    }

}
