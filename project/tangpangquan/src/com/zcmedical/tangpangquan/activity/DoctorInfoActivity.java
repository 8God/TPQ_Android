package com.zcmedical.tangpangquan.activity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Picasso;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.Doctor;

public class DoctorInfoActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "DoctorInfoActivity";
    ImageView ivHead;
    TextView tvFans;
    TextView tvName;
    TextView tvClass;
    TextView tvHospital;
    TextView tvTeam;

    TextView tvHigher1;
    TextView tvHigher2;
    TextView tvHigher3;

    Button btnFollow;
    Button btnAsk;

    private String doctorJsonText;
    private Doctor doctor;

    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_info);
        initUI();
        client = new AsyncHttpClient();
        doctorJsonText = getIntent().getExtras().getString("doctorJsonText");
        if (TextUtils.isEmpty(doctorJsonText)) {
            //一般情况下，这里不会发生
            showLongToast("好像页面出了点小问题，请返回");
        } else {
            Type listType = new TypeToken<LinkedList<Doctor>>() {
            }.getType();
            Gson gson = new Gson();
            LinkedList<Doctor> doctors = gson.fromJson((JsonUtils.getOjectString(doctorJsonText, "doctors")), listType);
            doctor = doctors.get(0);
            setDoctorUI(doctor);
        }

    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case DoctorDetailsActivity.HANDLER_DISSMISS_DIALOG://dimiss dialog
                dimissProgressDialog();
                break;
            case DoctorDetailsActivity.HANDLER_ERROR://error toast
                dimissProgressDialog();
                showToast("请求出错，请稍后再试。");
                break;
            case DoctorDetailsActivity.HANDLER_FAN://isFans : arg1:0 未关注 ; arg1:1 已关注;
                if (msg.arg1 == DoctorDetailsActivity.HANDLER_ARG1_ISFAN) {
                    btnFollow.setText("取消关注");
                    if (msg.arg2 == DoctorDetailsActivity.HANDLER_ARG2_SHOW_DIALOG) {
                        DialogUtils.showAlertDialog(DoctorInfoActivity.this, "关注成功！");
                    }
                } else {
                    btnFollow.setText("关注");
                    if (msg.arg2 == DoctorDetailsActivity.HANDLER_ARG2_SHOW_DIALOG) {
                        DialogUtils.showAlertDialog(DoctorInfoActivity.this, "取消关注成功！");
                    }
                }
                break;
            default:
                break;
            }
        };
    };

    private void setDoctorUI(Doctor doctor) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doctor : " + doctor.toString());
        }
        if (!TextUtils.isEmpty(doctor.getHead_pic())) {
            Picasso.with(this).load(doctor.getHead_pic()).into(ivHead);
        }
        tvFans.setText(TextUtils.isEmpty(doctor.getFans_count()) ? " 0 " : doctor.getFans_count() + "  粉丝");
        tvName.setText(TextUtils.isEmpty(doctor.getNickname()) ? doctor.getId() : doctor.getNickname());
        tvClass.setText(TextUtils.isEmpty(doctor.getJob_title()) ? " " : doctor.getJob_title());
        tvHospital.setText(TextUtils.isEmpty(doctor.getHospital()) ? "" : doctor.getHospital());
        tvTeam.setText(doctor.getDoctor_team() == null ? " " : doctor.getDoctor_team().getTeam_name());
        //待确定需求 高于或低于的一些评价

        tvHigher1.setText(TextUtils.isEmpty(doctor.getSkill()) ? "" : doctor.getSkill());
        tvHigher2.setText(TextUtils.isEmpty(doctor.getEducation()) ? "" : doctor.getEducation());
        tvHigher3.setText(TextUtils.isEmpty(doctor.getPrize()) ? "" : doctor.getPrize());
    }

    private void initUI() {
        findView(R.id.ivBack).setOnClickListener(this);
        ivHead = findView(R.id.ivHead);
        tvFans = findView(R.id.tvFans);
        tvName = findView(R.id.tvName);
        tvClass = findView(R.id.tvClass);
        tvHospital = findView(R.id.tvHospital);
        tvTeam = findView(R.id.tvTeam);

        tvHigher1 = findView(R.id.tvHigher1);
        tvHigher2 = findView(R.id.tvHigher2);
        tvHigher3 = findView(R.id.tvHigher3);

        btnFollow = findView(R.id.btnFollow);
        btnFollow.setOnClickListener(this);
        btnAsk = findView(R.id.btnAsk);
        btnAsk.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DoctorDetailsActivity.checkIsFan(client, DoctorDetailsActivity.doctorId, DoctorDetailsActivity.userID, handler);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ivBack:
            finish();
            break;
        case R.id.btnFollow:
            if (TextUtils.isEmpty(DoctorDetailsActivity.FanID)) {
                DoctorDetailsActivity.BindFan(client, DoctorDetailsActivity.doctorId, DoctorDetailsActivity.userID, handler);
            } else {
                DoctorDetailsActivity.unBindFan(client, DoctorDetailsActivity.FanID, handler);
            }
            break;
        case R.id.btnAsk:
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("userId", DoctorDetailsActivity.doctorId);
            startActivity(intent);
            break;
        default:
            break;
        }
    }
}
