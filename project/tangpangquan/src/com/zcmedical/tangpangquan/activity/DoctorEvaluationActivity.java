package com.zcmedical.tangpangquan.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.Doctor;
import com.zcmedical.tangpangquan.entity.Evaluation;

public class DoctorEvaluationActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "DoctorEvaluationActivity";

    private ImageView ivHead;
    private TextView tvName;
    private TextView tvClass;
    private TextView tvScore;

    private com.zcmedical.common.component.AutoLoadMoreListView listView;
    //private ListView listView;

    private String evaluationJsonText;
    private String doctorJsonText;

    private EvaluationAdapter evaluationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_evaluation);
        initUI();
        evaluationJsonText = getIntent().getExtras().getString("evaluationJsonText");
        doctorJsonText = getIntent().getExtras().getString("doctorJsonText");
        if (TextUtils.isEmpty(doctorJsonText)) {
            //重新获取医生个人信息
        } else {
            Type listType = new TypeToken<LinkedList<Doctor>>() {
            }.getType();
            Gson gson = new Gson();
            LinkedList<Doctor> doctors = gson.fromJson((JsonUtils.getOjectString(doctorJsonText, "doctors")), listType);
            setDoctorInfoUI(doctors.get(0));
        }

        if (TextUtils.isEmpty(evaluationJsonText)) {
            //重新获取医生评价
        } else {
            Type listType = new TypeToken<LinkedList<Evaluation>>() {
            }.getType();
            Gson gson = new Gson();
            LinkedList<Evaluation> evaluations = gson.fromJson((JsonUtils.getOjectString(evaluationJsonText, "doctor_comments")), listType);
            //Log.d(TAG, "JsonHttpResponseHandler.size : " + evaluations.size());
            if (evaluations.size() != 0) {
                List<Evaluation> datas = new ArrayList<Evaluation>();
                for (Iterator iterator = evaluations.iterator(); iterator.hasNext();) {
                    Evaluation evaluation = (Evaluation) iterator.next();
                    if (evaluation.getComment_status().equals("1") && evaluation.getUser() != null) {
                        datas.add(evaluation);
                    }
                    Log.d(TAG, "Evaluation : " + evaluation.toString());
                }
                setEvaluationUI(datas);
            } else {
                //没有用户评价
                showToast("该医生还没有用户评价哦...");
            }
        }
    }

    private void setEvaluationUI(List<Evaluation> datas) {
        evaluationAdapter = new EvaluationAdapter(this, datas);
        listView.setAdapter(evaluationAdapter);
    }

    private void setDoctorInfoUI(Doctor doctor) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "doctor : " + doctor.toString());
        }
        if (!TextUtils.isEmpty(doctor.getHead_pic())) {
            Picasso.with(this).load(doctor.getHead_pic()).into(ivHead);
        }
        tvName.setText(TextUtils.isEmpty(doctor.getNickname()) ? doctor.getId() : doctor.getNickname());
        tvClass.setText(TextUtils.isEmpty(doctor.getJob_title()) ? " " : doctor.getJob_title());
        tvScore.setText(TextUtils.isEmpty(doctor.getRecommended()) ? " " : doctor.getRecommended());
    }

    private void initUI() {
        ivHead = findView(R.id.ivHead);
        tvName = findView(R.id.tvName);
        tvClass = findView(R.id.tvClass);
        tvScore = findView(R.id.tvScore);
        listView = findView(R.id.listView);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        findView(R.id.tvBack).setOnClickListener(this);
    }

    private class EvaluationAdapter extends BaseAdapter {

        private List<Evaluation> evaluationDatas;
        private Context context;
        private LayoutInflater mInflater;

        @SuppressWarnings("unused")
        public EvaluationAdapter(Context context, List<Evaluation> evaluationDatas) {
            this.context = context;
            this.evaluationDatas = evaluationDatas;
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);  ;
        }

        @Override
        public int getCount() {
            return evaluationDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return evaluationDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_doctor_evaluation, null);
                holder = new ViewHolder();
                holder.user = (TextView) convertView.findViewById(R.id.tvUser);
                holder.content = (TextView) convertView.findViewById(R.id.tvUserContent);
                holder.say = (TextView) convertView.findViewById(R.id.tvSay);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.user.setText(TextUtils.isEmpty(evaluationDatas.get(position).getUser().getNickname()) ? evaluationDatas.get(position).getUser_id() : evaluationDatas.get(position).getUser()
                    .getNickname());
            holder.content.setText(TextUtils.isEmpty(evaluationDatas.get(position).getContent()) ? "" : evaluationDatas.get(position).getContent());
            holder.say.setText(TextUtils.isEmpty(evaluationDatas.get(position).getComment_status()) ? Evaluation.COMMENTSTATUS[0] : Evaluation.COMMENTSTATUS[Integer.parseInt(evaluationDatas.get(
                    position).getComment_status())]);
            holder.say.setTextColor(TextUtils.isEmpty(evaluationDatas.get(position).getComment_status()) ? (ColorStateList) getResources().getColorStateList(Evaluation.COMMENTCOLORS[0])
                    : (ColorStateList) getResources().getColorStateList(Evaluation.COMMENTCOLORS[Integer.parseInt(evaluationDatas.get(position).getComment_status())]));
            return convertView;
        }

        private class ViewHolder {
            TextView user;
            TextView content;
            TextView say;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.tvBack:
            finish();
            break;

        default:
            break;
        }

    }

}
