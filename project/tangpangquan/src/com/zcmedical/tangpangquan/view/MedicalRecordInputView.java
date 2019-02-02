package com.zcmedical.tangpangquan.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.MedicalRecordInputActivity;
import com.zcmedical.tangpangquan.entity.MedicalRecordEntity;

public class MedicalRecordInputView extends RelativeLayout {
    private static final SimpleDateFormat createTimeFormat = new SimpleDateFormat("yyyy-MM-dd");

    private MedicalRecordEntity medicalRecordEntity;

    private ImageView bgImv;
    private TextView dateTv;

    public MedicalRecordInputView(Context context, MedicalRecordEntity medicalRecordEntity) {
        super(context);
        this.medicalRecordEntity = medicalRecordEntity;

        init();
    }

    public MedicalRecordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.view_bodycheck_input, null);
        addView(layout);

        initUI(layout);
    }

    private void initUI(View layout) {
        bgImv = (ImageView) layout.findViewById(R.id.imv_bodycheck_bg);
        dateTv = (TextView) layout.findViewById(R.id.tv_bodycheck_date);

        bgImv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != medicalRecordEntity) {
                    TpqApplication.getInstance().setShowingMedicalRecordEntity(medicalRecordEntity);
                    Intent openMedicalRecord = new Intent(getContext(), MedicalRecordInputActivity.class);
                    openMedicalRecord.putExtra(MedicalRecordInputActivity.KEY_IS_INPUT_MODE, false);
                    getContext().startActivity(openMedicalRecord);
                }
            }
        });

        initInfo(medicalRecordEntity);
    }

    public MedicalRecordEntity getMedicalRecordEntity() {
        return medicalRecordEntity;
    }

    public void setMedicalRecordEntity(MedicalRecordEntity medicalRecordEntity) {
        this.medicalRecordEntity = medicalRecordEntity;

        initInfo(medicalRecordEntity);
    }

    private void initInfo(MedicalRecordEntity medicalRecordEntity) {
        if (null == medicalRecordEntity) {
            Drawable drawable = getResources().getDrawable(R.drawable.bodycheck_img_defaultanalyse);
            bgImv.setImageDrawable(drawable);
            dateTv.setVisibility(View.GONE);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.bodycheck_img_bganalyse);
            bgImv.setImageDrawable(drawable);

            String createdAt = medicalRecordEntity.getCreatedAt();
            try {
                Date createdDate = CommonConstant.serverTimeFormat.parse(createdAt);
                createdAt = createTimeFormat.format(createdDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTv.setText(createdAt);

            dateTv.setVisibility(View.VISIBLE);
        }
    }

}
