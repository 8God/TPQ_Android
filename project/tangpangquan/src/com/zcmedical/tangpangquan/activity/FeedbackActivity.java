package com.zcmedical.tangpangquan.activity;

import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class FeedbackActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        init();
    }

    private void init() {
        initToolbar(getString(R.string.title_feedback_activity));
        initUI();
    }

    private void initUI() {
        setViewClickListener(R.id.btn_submit_feedback, this);

        UserEntity user = TpqApplication.getInstance().getUser();
        if (null != user) {
            String mobile = user.getMobile();
            String qq = user.getQq();
            if (Validator.isPhoneNumber(mobile)) {
                setEditText(R.id.edt_feedback_mobile, mobile);
            }
            if (!TextUtils.isEmpty(qq)) {
                setEditText(R.id.edt_feedback_qq, qq);
            }
        }
    }

    private void submitFeedback() {
        String content = getEditTextInput(R.id.edt_feedback_content, "");
        String mobile = getEditTextInput(R.id.edt_feedback_mobile, "");
        String qq = getEditTextInput(R.id.edt_feedback_qq, "");

        if (TextUtils.isEmpty(content)) {
            showToast("请填写反馈内容");

            return;
        }

        if (TextUtils.isEmpty(qq) && !Validator.isPhoneNumber(mobile)) {

            showToast("请填写至少一个联系方式");
            return;
        }

        CommonRequest submitFeedback = new CommonRequest();
        submitFeedback.setRequestApiName(InterfaceConstant.API_FEEDBACK_CREATE);
        submitFeedback.setRequestID(InterfaceConstant.REQUEST_ID_FEEDBACK_CREATE);
        submitFeedback.addRequestParam(APIKey.FEEDBACK_CONTENT, content);
        if (Validator.isPhoneNumber(mobile)) {
            submitFeedback.addRequestParam(APIKey.USER_MOBILE, mobile);
        }
        if (!TextUtils.isEmpty(qq)) {
            submitFeedback.addRequestParam(APIKey.USER_MOBILE, qq);
        }

        addRequestAsyncTask(submitFeedback);

        showProgressDialog("正在提交反馈");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_submit_feedback:
            submitFeedback();
            break;

        default:
            break;
        }

    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_FEEDBACK_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("感谢您的反馈，我们会尽快回复您");

                finish();
            } else {
                showToast("提交失败，请重试");
            }
            dimissProgressDialog();
        }
    }

}
