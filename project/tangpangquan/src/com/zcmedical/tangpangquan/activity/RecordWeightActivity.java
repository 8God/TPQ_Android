package com.zcmedical.tangpangquan.activity;

import hirondelle.date4j.DateTime;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.TimeZone;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.db.DbUtils;
import com.zcmedical.common.db.Weight;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserInfo;
import com.zcmedical.tangpangquan.view.WheelView;

public class RecordWeightActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "RecordWeightActivity";
    private static final int LENGTH = 120;
    private static final String[] PLANETS = new String[LENGTH];
    private static final String[] PLANETS_POINT = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    private WheelView wva1, wva2;
    private Button btn1, btn2;
    private static final int LOWEST = 30;
    private static final int SELECT = 30;
    private int weight1 = 49;
    private int weight2 = 0;
    private int weight3 = 49;
    private int weight4 = 0;
    private boolean isRecordCurrentWeight = true;
    private boolean isEdit = false;
    private TextView tv;
    private boolean isTodayWeight = false;
    private float height = 170;
    private boolean init = false;

    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_weight);
        mDbUtils = DbUtils.getInstance(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            isTodayWeight = getIntent().getExtras().getBoolean("isTodayWeight", false);
            height = getIntent().getExtras().getFloat("height", 0);
            init = getIntent().getExtras().getBoolean("init", false);
            isEdit = getIntent().getExtras().getBoolean("isEdit", false);
        }
        client = new AsyncHttpClient();
        initUI();
        initToolbar();
    }

    private void initUI() {
        wva1 = findView(R.id.wheelView1);
        wva2 = findView(R.id.wheelView2);
        btn1 = findView(R.id.btn1);
        btn2 = findView(R.id.btn2);
        tv = findView(R.id.tv);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

        changeUI();

        for (int i = 0; i < LENGTH; i++) {
            PLANETS[i] = "" + (LOWEST + i);
        }

        wva1.setOffset(2);
        wva1.setItems(Arrays.asList(PLANETS));
        wva1.setSeletion(SELECT);
        wva1.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (isRecordCurrentWeight) {
                    weight1 = Integer.parseInt(item);
                } else {
                    weight3 = Integer.parseInt(item);
                }

                Log.d(TAG, "selectedIndex.0: " + selectedIndex + ", item: " + item);
            }
        });
        if (isRecordCurrentWeight) {
            weight1 = Integer.parseInt(wva1.getSeletedItem());
        } else {
            weight3 = Integer.parseInt(wva1.getSeletedItem());
        }

        wva2.setOffset(2);
        wva2.setItems(Arrays.asList(PLANETS_POINT));
        wva2.setSeletion(0);
        wva2.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (isRecordCurrentWeight) {
                    weight2 = Integer.parseInt(item);
                } else {
                    weight4 = Integer.parseInt(item);
                }
                Log.d(TAG, "selectedIndex.1: " + selectedIndex + ", item: " + item);
            }
        });
        if (isRecordCurrentWeight) {
            weight2 = Integer.parseInt(wva2.getSeletedItem());
        } else {
            weight4 = Integer.parseInt(wva2.getSeletedItem());
        }
    }

    private void changeUI() {
        if (init) {
            btn1.setText("上一步");
            if (isRecordCurrentWeight) {
                tv.setText("您现在的体重是？(单位:kg)");
                btn2.setText("下一步");
            } else {
                wva1.setSeletion(SELECT + 20);
                wva2.setSeletion(0);
                tv.setText("您的健康体重范围是50~60kg \n 您的目标体重是？(单位:kg)");
                btn2.setText("完成");
            }
        } else {
            //isedit istodayweight
            if (isEdit) {
                //编辑目标体重
                wva1.setSeletion(SELECT + 20);
                wva2.setSeletion(0);
                tv.setText("您的目标体重是？(单位:kg)");
            } else {
                tv.setText("您现在的体重是？(单位:kg)");
            }
            btn1.setText("取消");
            btn2.setText("确定");
        }
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

    private boolean isStep1Succeed = false;
    private boolean isStep2Succeed = false;
    private int times = 0;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 0:
                times++;
                if (times == 2) {
                    times = 0;
                    RecordWeightActivity.this.dimissProgressDialog();
                    if (isStep1Succeed && isStep2Succeed) {
                        doneAndBack(true);
                    } else {
                        if (!isStep1Succeed && isStep2Succeed) {
                            times = 1;
                        } else if (!isStep2Succeed && isStep1Succeed) {
                            times = 1;
                        } else {
                            times = 0;
                        }
                        DialogUtils.showAlertDialog(RecordWeightActivity.this, "网络异常，请重试");
                    }
                } else {
                    uploadHeightAndTargetWeight();
                }
                break;
            case 20:
                doneAndBack(false);
                break;
            case 30:
                if (msg.obj != null) {
                    showToast(msg.obj.toString());
                    RecordWeightActivity.this.dimissProgressDialog();
                }
                break;
            default:
                break;
            }
        }
    };

    private void doneAndBack(final boolean goHome) {
        DialogUtils.showAlertDialog(RecordWeightActivity.this, "记录成功！", "成功", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (goHome) {
                    startActivity(new Intent(RecordWeightActivity.this, HomeActivity.class));
                }
                finish();
            }
        });
    }

    private void uploadWeight(final boolean isupdate, Weight weight) {
        RequestParams params = new RequestParams();
        params.put("user_id", TpqApplication.getInstance().getUserId());
        params.put("weight", (weight1 * 10 + weight2) / 10);
        Log.d(TAG, "we : " + ((weight1 * 10 + weight2) / 10));
        if (weight != null) {
            params.put("id", weight.getId());
        }
        String url = isupdate ? InterfaceConstant.WEIGHT_UPDATE : InterfaceConstant.WEIGHT_CREAT;
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "response : " + errorResponse.toString());
                if (isupdate || isTodayWeight) {
                    handler.sendEmptyMessage(20);
                } else {
                    isStep1Succeed = false;
                    handler.sendEmptyMessage(0);
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "response : " + response.toString());
                if (JsonUtils.getStatus(response.toString()) != 0) {
                    handler.sendMessage(handler.obtainMessage(30, JsonUtils.getSthObjectString(response.toString(), "message")));
                    return;
                }
                Type type = new TypeToken<Weight>() {
                }.getType();
                Gson gson = new Gson();
                Weight weight = gson.fromJson((JsonUtils.getOjectString((response.toString()), "weight")), type);
                Log.d(TAG, "" + weight.toString());
                if (isupdate) {
                    mDbUtils.modifyWeight(weight);
                    handler.sendEmptyMessage(20);
                } else {
                    mDbUtils.insertNewWeight(weight);
                    if (isTodayWeight) {
                        handler.sendEmptyMessage(20);
                    } else {
                        isStep1Succeed = true;
                        handler.sendEmptyMessage(0);
                    }
                }
            }

        });
    }

    private void uploadWeight() {
        uploadWeight(false, null);
    }

    private void uploadHeightAndTargetWeight(final boolean isUpdate) {
        RequestParams params = new RequestParams();
        params.put("id", TpqApplication.getInstance().getUserId());
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "uploadHeightAndTargetWeight.isUpdate : " + isUpdate + "   height:" + height + "   target_weight:" + (weight3 * 10 + weight4) / 10);
        }
        if (!isUpdate) {
            params.put("height", height);
            params.put("target_weight", (weight3 * 10 + weight4) / 10);
        } else {
            params.put("target_weight", (weight1 * 10 + weight2) / 10);
        }
        client.post(InterfaceConstant.API_USER_UPDATE, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "response : " + errorResponse.toString());
                if (isUpdate) {
                    handler.sendEmptyMessage(20);
                } else {
                    isStep2Succeed = false;
                    handler.sendEmptyMessage(0);
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "response : " + response.toString());
                Type type = new TypeToken<UserInfo>() {
                }.getType();
                Gson gson = new Gson();
                UserInfo userInfo = gson.fromJson((JsonUtils.getOjectString((response.toString()), "user")), type);
                Log.d(TAG, "" + userInfo.toString());
                if (isUpdate) {
                    spf.edit().putInt("target_weight", (int) ((weight1 * 10 + weight2) / 10)).commit();
                    handler.sendEmptyMessage(20);
                } else {
                    spf.edit().putInt("target_weight", (int) ((weight3 * 10 + weight4) / 10)).commit();
                    spf.edit().putInt("height", (int) height).commit();
                    isStep2Succeed = true;
                    handler.sendEmptyMessage(0);
                }
                super.onSuccess(statusCode, headers, response);
            }

        });

    }

    private void uploadHeightAndTargetWeight() {
        uploadHeightAndTargetWeight(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn1:
            if (isEdit || isTodayWeight) {
                finish();
            } else {
                if (isRecordCurrentWeight) {
                    finish();
                } else {
                    isRecordCurrentWeight = true;
                    changeUI();
                }
            }
            break;

        case R.id.btn2:
            if (init) {
                showToast("init");
                if (isRecordCurrentWeight) {
                    //目标体重
                    isRecordCurrentWeight = false;
                    changeUI();
                } else {
                    //保存
                    showProgressDialog();
                    if (!isStep1Succeed && isStep2Succeed) {
                        uploadWeight();
                    } else if (!isStep2Succeed && isStep1Succeed) {
                        uploadHeightAndTargetWeight();
                    } else {
                        uploadWeight();
                    }

                }
            } else if (isEdit && !isTodayWeight) {
                showToast("isEdit");
                //编辑目标体重数据
                uploadHeightAndTargetWeight(true);
            } else {
                //isTodayWeight
                showToast("uploadWeight");
                //新增当天数据，先判断当天有没有记录，如果有，则更新，否则添加
                Weight weight = mDbUtils.queryOneWeight(DateTime.today(TimeZone.getDefault()).format("YYYYMMDDhhmmss"));
                if (weight == null) {
                    uploadWeight();
                } else {
                    uploadWeight(true, weight);
                }
            }
            break;

        default:
            break;
        }
    }
}