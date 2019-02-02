package com.zcmedical.tangpangquan.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.zcmedical.tangpangquan.entity.Evaluation;

public class DoctorDetailsActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "DoctorDetailsActivity";

    public static final int HANDLER_DISSMISS_DIALOG = 10;
    public static final int HANDLER_ERROR = 20;
    public static final int HANDLER_FAN = 30;

    public static final int HANDLER_ARG1_ISFAN = 1;
    public static final int HANDLER_ARG1_NOTFAN = 0;

    public static final int HANDLER_ARG2_NO_DIALOG = 0;
    public static final int HANDLER_ARG2_SHOW_DIALOG = 1;
    ImageView ivHead;
    TextView tvFans;
    TextView tvName;
    TextView tvClass;
    TextView tvHospital;
    TextView tvTeam;
    TextView tvGood;
    TextView tvScore1;
    TextView tvScore2;
    TextView tvScore3;
    ImageView ivScore1;
    ImageView ivScore2;
    ImageView ivScore3;
    TextView tvHigher1;
    TextView tvHigher2;
    TextView tvHigher3;
    TextView tvUser1;
    TextView tvUser2;
    TextView tvUser3;
    TextView tvUser4;
    TextView tvUser5;
    TextView tvSay1;
    TextView tvSay2;
    TextView tvSay3;
    TextView tvSay4;
    TextView tvSay5;
    TextView tvUserContent1;
    TextView tvUserContent2;
    TextView tvUserContent3;
    TextView tvUserContent4;
    TextView tvUserContent5;
    RelativeLayout rlUser1;
    RelativeLayout rlUser2;
    RelativeLayout rlUser3;
    RelativeLayout rlUser4;
    RelativeLayout rlUser5;
    Button btnFollow;
    Button btnAsk;
    View line1;
    View line2;
    View line3;
    View line4;
    View line5;

    List<TextView> users;
    List<TextView> says;
    List<TextView> contents;
    List<RelativeLayout> reUsers;
    List<View> lines;

    public static String doctorId = "";

    private AsyncHttpClient client;
    private List<Evaluation> evaluationDatas;

    private String evaluationJsonText;
    private String doctorJsonText;

    public static String userID = "";

    public static String FanID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);
        initUI();
        userID = TpqApplication.getInstance().getUserId();
        doctorId = getIntent().getExtras().getString("doctorId");
        evaluationDatas = new ArrayList<Evaluation>();
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id", doctorId);
        Log.d(TAG, "doctorId : " + doctorId);
        client.post(InterfaceConstant.DOCTOR_FETCH, params, mJsonHttpResponseHandler);
        showProgressDialog("正在加载中，请稍后");
        RequestParams requestParams = new RequestParams();
        requestParams.put("doctor_id", doctorId);
        client.post(InterfaceConstant.DOCTOR_COMMENT_FETCH, requestParams, mCommentJsonHttpResponseHandler);
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case HANDLER_DISSMISS_DIALOG://dimiss dialog
                dimissProgressDialog();
                break;
            case HANDLER_ERROR://error toast
                dimissProgressDialog();
                showToast("请求出错，请稍后再试。");
                break;
            case HANDLER_FAN://isFans : arg1:0 未关注 ; arg1:1 已关注;
                if (msg.arg1 == HANDLER_ARG1_ISFAN) {
                    btnFollow.setText("取消关注");
                    if (msg.arg2 == HANDLER_ARG2_SHOW_DIALOG) {
                        DialogUtils.showAlertDialog(DoctorDetailsActivity.this, "关注成功！");
                    }
                } else {
                    btnFollow.setText("关注");
                    if (msg.arg2 == HANDLER_ARG2_SHOW_DIALOG) {
                        DialogUtils.showAlertDialog(DoctorDetailsActivity.this, "取消关注成功！");
                    }
                }
                break;
            default:
                break;
            }
        };
    };

    JsonHttpResponseHandler mCommentJsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JsonHttpResponseHandler.re : " + response.toString());
            }
            int status = JsonUtils.getStatus(response.toString());
            if (status == 0) {
                //Log.d(TAG, "JsonHttpResponseHandler.json : " + (JsonUtils.getOjectString((response.toString()), "doctor_comments")));
                evaluationJsonText = response.toString();
                Type listType = new TypeToken<LinkedList<Evaluation>>() {
                }.getType();
                Gson gson = new Gson();
                LinkedList<Evaluation> evaluations = gson.fromJson((JsonUtils.getOjectString((response.toString()), "doctor_comments")), listType);
                //Log.d(TAG, "JsonHttpResponseHandler.size : " + evaluations.size());
                if (evaluations != null && evaluations.size() != 0) {
                    evaluationDatas.clear();
                    for (Iterator iterator = evaluations.iterator(); iterator.hasNext();) {
                        Evaluation evaluation = (Evaluation) iterator.next();
                        if (evaluation.getComment_status().equals("1")) {
                            evaluationDatas.add(evaluation);
                        }
                        //Log.d(TAG, "JsonHttpResponseHandler.Evaluation : " + evaluation.toString());
                    }
                    setEvaluationUI(evaluationDatas);
                } else {

                }

            } else {
                showToast("请求评价数据失败，请稍后再试~");
            }
            handler.sendEmptyMessage(HANDLER_DISSMISS_DIALOG);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "JsonHttpResponseHandler.onFailure : " + responseString);
                Log.d(TAG, "JsonHttpResponseHandler.onFailure.throwable : " + throwable.toString());
            }
            handler.sendEmptyMessage(HANDLER_ERROR);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            handler.sendEmptyMessage(HANDLER_ERROR);
        }
    };

    private void setEvaluationUI(List<Evaluation> evaluationDatas) {
        int size = evaluationDatas.size() >= 5 ? 5 : evaluationDatas.size();
        for (int i = 0; i < size; i++) {
            if (evaluationDatas.get(i).getUser() == null) {
                break;
            }
            reUsers.get(i).setVisibility(View.VISIBLE);
            lines.get(i).setVisibility(View.VISIBLE);
            Log.d(TAG, " i .user : " + i + "  " + evaluationDatas.get(i).getUser().toString());
            users.get(i).setText(TextUtils.isEmpty(evaluationDatas.get(i).getUser().getNickname()) ? evaluationDatas.get(i).getUser_id() : evaluationDatas.get(i).getUser().getNickname());
            contents.get(i).setText(TextUtils.isEmpty(evaluationDatas.get(i).getContent()) ? "" : evaluationDatas.get(i).getContent());
            says.get(i).setText(
                    TextUtils.isEmpty(evaluationDatas.get(i).getComment_status()) ? Evaluation.COMMENTSTATUS[0]
                            : Evaluation.COMMENTSTATUS[Integer.parseInt(evaluationDatas.get(i).getComment_status())]);
            says.get(i).setTextColor(
                    TextUtils.isEmpty(evaluationDatas.get(i).getComment_status()) ? (ColorStateList) getResources().getColorStateList(Evaluation.COMMENTCOLORS[0]) : (ColorStateList) getResources()
                            .getColorStateList(Evaluation.COMMENTCOLORS[Integer.parseInt(evaluationDatas.get(i).getComment_status())]));

        }
    }

    JsonHttpResponseHandler mJsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d(TAG, "DoctorJsonHttpResponseHandler.re : " + response.toString());
            doctorJsonText = response.toString();
            Type listType = new TypeToken<LinkedList<Doctor>>() {
            }.getType();
            Gson gson = new Gson();
            LinkedList<Doctor> doctors = gson.fromJson((JsonUtils.getOjectString((response.toString()), "doctors")), listType);
            if (doctors.size() > 0) {
                setDoctorUI(doctors.get(0));
                handler.sendEmptyMessage(HANDLER_DISSMISS_DIALOG);
            } else {
                handler.sendEmptyMessage(HANDLER_ERROR);
            }

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            handler.sendEmptyMessage(HANDLER_ERROR);
        }
    };

    private void setDoctorUI(Doctor doctor) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doctor : " + doctor.toString());
        }
        if (!TextUtils.isEmpty(doctor.getHead_pic())) {
            Picasso.with(this).load(doctor.getHead_pic()).into(ivHead);
        }
        tvFans.setText(TextUtils.isEmpty(doctor.getFans_count()) ? " 0 " : doctor.getFans_count());
        tvName.setText(TextUtils.isEmpty(doctor.getNickname()) ? doctor.getId() : doctor.getNickname());
        tvClass.setText(TextUtils.isEmpty(doctor.getJob_title()) ? " " : doctor.getJob_title());
        tvHospital.setText(TextUtils.isEmpty(doctor.getHospital()) ? "" : doctor.getHospital());
        tvTeam.setText(doctor.getDoctor_team() == null ? " " : doctor.getDoctor_team().getTeam_name());
        tvGood.setText(TextUtils.isEmpty(doctor.getSkill()) ? "擅长 ：" : doctor.getSkill());
        tvScore1.setText(TextUtils.isEmpty(doctor.getRecommended()) ? "推荐指数：" : "推荐指数：" + doctor.getRecommended());
        tvScore2.setText(TextUtils.isEmpty(doctor.getAttitude()) ? "服务态度：" : "服务态度：" + doctor.getAttitude());
        tvScore3.setText(TextUtils.isEmpty(doctor.getPlane()) ? "医疗水平：" : "医疗水平：" + doctor.getPlane());
        //待确定需求 高于或低于的一些评价

    }

    private void initUI() {
        findView(R.id.ivBack).setOnClickListener(this);
        findView(R.id.tvMore).setOnClickListener(this);
        ivHead = findView(R.id.ivHead);
        tvFans = findView(R.id.tvFans);
        tvName = findView(R.id.tvName);
        tvClass = findView(R.id.tvClass);
        tvHospital = findView(R.id.tvHospital);
        tvTeam = findView(R.id.tvTeam);
        tvGood = findView(R.id.tvGood);
        tvGood.setOnClickListener(this);
        tvScore1 = findView(R.id.tvScore1);
        tvScore2 = findView(R.id.tvScore2);
        tvScore3 = findView(R.id.tvScore3);
        ivScore1 = findView(R.id.ivScore1);
        ivScore2 = findView(R.id.ivScore2);
        ivScore3 = findView(R.id.ivScore3);
        tvHigher1 = findView(R.id.tvHigher1);
        tvHigher2 = findView(R.id.tvHigher2);
        tvHigher3 = findView(R.id.tvHigher3);
        tvUser1 = findView(R.id.tvUser1);
        tvUser2 = findView(R.id.tvUser2);
        tvUser3 = findView(R.id.tvUser3);
        tvUser4 = findView(R.id.tvUser4);
        tvUser5 = findView(R.id.tvUser5);
        tvSay1 = findView(R.id.tvSay1);
        tvSay2 = findView(R.id.tvSay2);
        tvSay3 = findView(R.id.tvSay3);
        tvSay4 = findView(R.id.tvSay4);
        tvSay5 = findView(R.id.tvSay5);
        tvUserContent1 = findView(R.id.tvUserContent1);
        tvUserContent2 = findView(R.id.tvUserContent2);
        tvUserContent3 = findView(R.id.tvUserContent3);
        tvUserContent4 = findView(R.id.tvUserContent4);
        tvUserContent5 = findView(R.id.tvUserContent5);
        rlUser1 = findView(R.id.rlUser1);
        rlUser2 = findView(R.id.rlUser2);
        rlUser3 = findView(R.id.rlUser3);
        rlUser4 = findView(R.id.rlUser4);
        rlUser5 = findView(R.id.rlUser5);
        line1 = findViewById(R.id.vLine12);
        line2 = findViewById(R.id.vLine13);
        line3 = findViewById(R.id.vLine14);
        line4 = findViewById(R.id.vLine15);
        line5 = findViewById(R.id.vLine16);
        btnFollow = findView(R.id.btnFollow);
        btnFollow.setOnClickListener(this);
        btnAsk = findView(R.id.btnAsk);
        btnAsk.setOnClickListener(this);
        users = new ArrayList<TextView>();
        users.add(tvUser1);
        users.add(tvUser2);
        users.add(tvUser3);
        users.add(tvUser4);
        users.add(tvUser5);
        says = new ArrayList<TextView>();
        says.add(tvSay1);
        says.add(tvSay2);
        says.add(tvSay3);
        says.add(tvSay4);
        says.add(tvSay5);
        contents = new ArrayList<TextView>();
        contents.add(tvUserContent1);
        contents.add(tvUserContent2);
        contents.add(tvUserContent3);
        contents.add(tvUserContent4);
        contents.add(tvUserContent5);
        reUsers = new ArrayList<RelativeLayout>();
        reUsers.add(rlUser1);
        reUsers.add(rlUser2);
        reUsers.add(rlUser3);
        reUsers.add(rlUser4);
        reUsers.add(rlUser5);
        lines = new ArrayList<View>();
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);
        lines.add(line5);
        for (RelativeLayout rl : reUsers) {
            rl.setVisibility(View.GONE);
        }
        for (View view : lines) {
            view.setVisibility(View.GONE);
        }
    }

    public static void checkIsFan(AsyncHttpClient asyncHttpClient, String docId, String userId, final Handler uiHandler) {
        if (asyncHttpClient != null) {
            RequestParams params = new RequestParams();
            params.put("doctor_id", docId);
            params.put("user_id", userId);
            asyncHttpClient.post(InterfaceConstant.DOCTOR_FAN_FETCH, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "checkIsFan.response : " + response.toString());
                    }
                    if (!TextUtils.isEmpty(JsonUtils.getResult(response.toString()))) {
                        JSONArray jsonArray = JsonUtils.getJsonArray(JsonUtils.getJson(JsonUtils.getResult(response.toString())), "doctor_fans");
                        JSONObject jsonObject = JsonUtils.getJsonobject(jsonArray, 0);
                        try {
                            String id = jsonObject.getString("id");
                            if (!TextUtils.isEmpty(id)) {
                                FanID = id;
                            }
                            //Log.d(TAG, "checkIsFan.id : " + id);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (!TextUtils.isEmpty(FanID)) {
                        //已关注
                        uiHandler.sendMessage(uiHandler.obtainMessage(HANDLER_FAN, HANDLER_ARG1_ISFAN, HANDLER_ARG2_NO_DIALOG));
                    } else {
                        uiHandler.sendMessage(uiHandler.obtainMessage(HANDLER_FAN, HANDLER_ARG1_NOTFAN, HANDLER_ARG2_NO_DIALOG));
                    }
                    //Log.d(TAG, "checkIsFan.re : " + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        }
    }

    public static void unBindFan(AsyncHttpClient asyncHttpClient, String id, final Handler uiHandler) {
        RequestParams params = new RequestParams();
        params.put("id", id);
        asyncHttpClient.post(InterfaceConstant.DOCTOR_FAN_REMOVE, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                String status = JsonUtils.getJsonString(response, "status");
                if (status.equals("0")) {
                    FanID = "";
                    uiHandler.sendMessage(uiHandler.obtainMessage(HANDLER_FAN, HANDLER_ARG1_NOTFAN, HANDLER_ARG2_SHOW_DIALOG));
                    Log.d(TAG, "unBindFan . succeed");
                } else {
                    Log.d(TAG, "unBindFan . failed : " + response.toString());
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                //                String status = JsonUtils.getOjectString(responseString, "status");
                //                if (status.equals("0")) {
                //                    FanID = "";
                //                    uiHandler.sendMessage(uiHandler.obtainMessage(HANDLER_FAN, HANDLER_ARG1_NOTFAN, 0));
                //                    Log.d(TAG, "unBindFan . succeed");
                //                } else {
                //                    Log.d(TAG, "unBindFan . failed");
                //                }
            }

        });
    }

    public static void BindFan(AsyncHttpClient asyncHttpClient, String docId, String userId, final Handler uiHandler) {
        if (asyncHttpClient != null) {
            RequestParams params = new RequestParams();
            params.put("doctor_id", docId);
            params.put("user_id", userId);
            asyncHttpClient.post(InterfaceConstant.DOCTOR_FAN_CREATE, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    if (!TextUtils.isEmpty(JsonUtils.getResult(response.toString()))) {
                        try {
                            JSONObject jsonObject = JsonUtils.getJson((JsonUtils.getJson(JsonUtils.getResult(response.toString())).get("doctor_fan")).toString());
                            Log.d(TAG, "BindFan.jsonObject : " + jsonObject.toString());
                            String id = jsonObject.getString("id");
                            FanID = id;
                            Log.d(TAG, "BindFan.id : " + id);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    if (!TextUtils.isEmpty(FanID)) {
                        //已关注
                        uiHandler.sendMessage(uiHandler.obtainMessage(HANDLER_FAN, HANDLER_ARG1_ISFAN, HANDLER_ARG2_SHOW_DIALOG));
                    } else {
                        //uiHandler.sendMessage(uiHandler.obtainMessage(HANDLER_FAN, HANDLER_ARG1_NOTFAN, 0));
                    }
                    Log.d(TAG, "BindFan.re : " + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsFan(client, doctorId, userID, handler);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ivBack:
            finish();
            break;
        case R.id.tvMore:
            //more evaluation
            if (TextUtils.isEmpty(doctorJsonText))
                return;
            startActivity(new Intent(DoctorDetailsActivity.this, DoctorEvaluationActivity.class).putExtra("doctorJsonText", doctorJsonText).putExtra("evaluationJsonText", evaluationJsonText));
            break;
        case R.id.btnFollow:
            //关注
            if (TextUtils.isEmpty(FanID)) {
                //Log.d(TAG, "onclick . BindFan");
                BindFan(client, doctorId, userID, handler);
            } else {
                //Log.d(TAG, "onclick . unBindFan");
                unBindFan(client, FanID, handler);
            }
            break;
        case R.id.btnAsk:
            //开始咨询
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("userId", doctorId);
            if (tvName != null && !TextUtils.isEmpty(tvName.getText())) {
                intent.putExtra("userName", tvName.getText());
            }
            startActivity(intent);
            break;
        case R.id.tvGood:
            if (TextUtils.isEmpty(doctorJsonText))
                return;
            startActivity(new Intent(DoctorDetailsActivity.this, DoctorInfoActivity.class).putExtra("doctorJsonText", doctorJsonText));
            break;

        default:
            break;
        }

    };

}
