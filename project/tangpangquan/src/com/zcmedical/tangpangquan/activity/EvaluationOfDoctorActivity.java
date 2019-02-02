package com.zcmedical.tangpangquan.activity;

import java.util.LinkedList;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.Doctor;

public class EvaluationOfDoctorActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "EvaluationOfDoctorActivity";
    com.zcmedical.tangpangquan.view.RoundedImageView ivHead;
    TextView name;
    TextView title;
    TextView content;
    ImageView ivEvaluation1;
    ImageView ivEvaluation2;
    ImageView ivEvaluation3;
    EditText etContent;

    private String dortorId;

    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_doctor);
        dortorId = getIntent().getExtras().getString("doctorId");
        client = new AsyncHttpClient();
        initUI();
    }

    private void initUI() {
        ivHead = findView(R.id.ivHead);
        name = findView(R.id.name);
        content = findView(R.id.content);
        title = findView(R.id.title);
        ivEvaluation1 = findView(R.id.ivEvaluation1);
        ivEvaluation2 = findView(R.id.ivEvaluation2);
        ivEvaluation3 = findView(R.id.ivEvaluation3);
        ivEvaluation1.setOnClickListener(this);
        ivEvaluation2.setOnClickListener(this);
        ivEvaluation3.setOnClickListener(this);
        etContent = findView(R.id.etContent);
        initToolbar();
        changeBtn(currentItem);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id", dortorId);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doctor(id) : " + dortorId);
        }
        client.post(InterfaceConstant.DOCTOR_FETCH, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "DoctorJsonHttpResponseHandler.re : " + response.toString());
                java.lang.reflect.Type listType = new TypeToken<LinkedList<Doctor>>() {
                }.getType();
                Gson gson = new Gson();
                LinkedList<Doctor> doctors = gson.fromJson((JsonUtils.getOjectString((response.toString()), "doctors")), listType);
                if (doctors.size() > 0) {
                    Doctor doctor = doctors.get(0);
                    if (!TextUtils.isEmpty(doctor.getJob_title())) {
                        title.setText(doctor.getJob_title());
                    }
                    if (!TextUtils.isEmpty(doctor.getNickname())) {
                        name.setText(doctor.getNickname());
                    }
                    if (!TextUtils.isEmpty(doctor.getHead_pic())) {
                        Picasso.with(EvaluationOfDoctorActivity.this).load(doctor.getHead_pic()).placeholder(R.drawable.common_icon_default_user_head).into(ivHead);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Log.d(TAG, "JsonHttpResponseHandler.re : " + errorResponse.toString());
                Toast.makeText(EvaluationOfDoctorActivity.this, "当前网络状况不好，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
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
            actionBar.setTitle("评价");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exit_circle, menu);
        MenuItem menuItem = menu.getItem(0);
        menuItem.setTitle("提交");
        return super.onCreateOptionsMenu(menu);
    }

    int currentItem = 0;

    private void changeBtn(int item) {
        currentItem = item;
        switch (item) {
        case 0:
            ivEvaluation1.setSelected(true);
            ivEvaluation2.setSelected(false);
            ivEvaluation3.setSelected(false);
            break;
        case 1:
            ivEvaluation1.setSelected(false);
            ivEvaluation2.setSelected(true);
            ivEvaluation3.setSelected(false);
            break;
        case 2:
            ivEvaluation1.setSelected(false);
            ivEvaluation2.setSelected(false);
            ivEvaluation3.setSelected(true);
            break;
        default:
            break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_exit_circle:
            if (TextUtils.isEmpty(etContent.getText().toString())) {
                showLongToast("请填写您的健康状况有哪些转变，以便我们为您更好的服务");
                return true;
            }
            showProgressDialog("正在提交中");
            RequestParams requestParams = new RequestParams();
            requestParams.put("doctor_id", dortorId);
            requestParams.put("user_id", TpqApplication.getInstance().getUserId());
            requestParams.put("content", etContent.getText().toString());
            if (currentItem == 0) {
                requestParams.put("comment_type", 2);
            } else if (currentItem == 2) {
                requestParams.put("comment_type", 0);
            } else {
                requestParams.put("comment_type", currentItem);
            }

            client.post(InterfaceConstant.DOCTOR_COMMENT_CREAT, requestParams, new JsonHttpResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    EvaluationOfDoctorActivity.this.showLongToast("评价失败，请稍后重试");
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    EvaluationOfDoctorActivity.this.dimissProgressDialog();
                    DialogUtils.showAlertDialog(EvaluationOfDoctorActivity.this, "评价成功!谢谢您的支持!", "确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EvaluationOfDoctorActivity.this.finish();
                        }

                    });
                }

            });
        case android.R.id.home:
            //finish();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ivEvaluation1:
            changeBtn(0);
            break;
        case R.id.ivEvaluation2:
            changeBtn(1);
            break;
        case R.id.ivEvaluation3:
            changeBtn(2);
            break;

        default:
            break;
        }
    }

}
