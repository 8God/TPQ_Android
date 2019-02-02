package com.zcmedical.tangpangquan.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.common.widget.ClearTextEditText;
import com.zcmedical.common.widget.CountDownButton;
import com.zcmedical.tangpangquan.R;

public class ForgetPswSecondStepActivity extends BaseActivity implements OnClickListener {

    public static final String KEY_VERIFICATION_CODE = "KEY_VERIFICATION_CODE";
    public static final String KEY_MOBILE = "KEY_MOBILE";
    public static final String KEY_USER_ID = "KEY_USER_ID";

    private String verificationCode;
    private String mobile;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_psw_second_step);

        init();
    }

    private void init() {
        initToolbar("短信验证");
        initData();
        initUI();
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            verificationCode = intent.getStringExtra(KEY_VERIFICATION_CODE);
            mobile = intent.getStringExtra(KEY_MOBILE);
            userId = intent.getStringExtra(KEY_USER_ID);
        }
    }

    private void initUI() {
        setTextView(R.id.tv_mobile, mobile + "");
        setViewClickListener(R.id.btn_get_verification_code, this);
        setViewClickListener(R.id.btn_reset_psw, this);
    }

    private void requestIdentifyingCode(String mobile) {
        if (Validator.isMobilePhoneNumber(mobile)) {
            verificationCode = null;
            CommonRequest requestIdentifying = new CommonRequest();
            requestIdentifying.setRequestApiName(InterfaceConstant.API_UTIL_REQUEST_IDENTIFYING_CODE);
            requestIdentifying.setRequestID(InterfaceConstant.REQUEST_ID_UTIL_REQUEST_IDENTIFYING_CODE);
            requestIdentifying.addRequestParam(APIKey.USER_MOBILE, mobile);

            addRequestAsyncTask(requestIdentifying);

            showProgressDialog(getString(R.string.pb_msg_get_identify));
        }
    }

    private boolean isVerificationCodeInputLegal() {
        boolean islegalInput = true;
        ClearTextEditText verificationCodeXEdt = findView(R.id.ctedt_identifying_code);

        String verificationCode = verificationCodeXEdt.getText() != null ? verificationCodeXEdt.getText().toString() : "";

        if (TextUtils.isEmpty(verificationCode)) {
            islegalInput = false;
            verificationCodeXEdt.setError(getString(R.string.error_msg_empty_verification_code));
        } else if (!this.verificationCode.equals(verificationCode)) {
            islegalInput = false;
            verificationCodeXEdt.setError(getString(R.string.error_msg_verification_code_error));
        }

        return islegalInput;
    }

    private boolean isPswInputLegal() {
        boolean islegalInput = true;

        ClearTextEditText pswXEdt = findView(R.id.ctedt_new_psw);
        ClearTextEditText pswConfirmXEdt = findView(R.id.ctedt_new_psw_confirm);

        String newPsw = pswXEdt.getText() != null ? pswXEdt.getText().toString() : "";
        String newPswConfirm = pswConfirmXEdt.getText() != null ? pswConfirmXEdt.getText().toString() : "";

        if (!Validator.isPassword(newPsw)) {
            islegalInput = false;
            pswXEdt.setError(getString(R.string.error_msg_psw_error));
        }

        if (!Validator.isPassword(newPswConfirm)) {
            islegalInput = false;
            pswConfirmXEdt.setError(getString(R.string.error_msg_psw_error));
        }

        if (islegalInput && !newPsw.equals(newPswConfirm)) {
            islegalInput = false;
            pswConfirmXEdt.setError(getString(R.string.error_msg_new_password_not_same));
        }

        if (!islegalInput) {
            pswXEdt.setText(null);
            pswConfirmXEdt.setText(null);
        }

        return islegalInput;
    }

    private void checkInput() {
        if (isVerificationCodeInputLegal() && isPswInputLegal()) {
            resetPassword();
        }
    }

    private void resetPassword() {
        ClearTextEditText pswConfirmXEdt = findView(R.id.ctedt_new_psw_confirm);
        String newPswConfirm = pswConfirmXEdt.getText() != null ? pswConfirmXEdt.getText().toString() : "";

        CommonRequest resetPswRequest = new CommonRequest();
        resetPswRequest.setRequestApiName(InterfaceConstant.API_USER_RESET_PASSWORD);
        resetPswRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_RESET_PASSWORD);
        resetPswRequest.addRequestParam(APIKey.COMMON_ID, userId);
        resetPswRequest.addRequestParam(APIKey.USER_PASSWORD, newPswConfirm);

        addRequestAsyncTask(resetPswRequest);

        showProgressDialog(getString(R.string.pb_msg_resetting_new_psw));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_get_verification_code:
            requestIdentifyingCode(mobile);
            break;
        case R.id.btn_reset_psw:
            checkInput();
            break;

        default:
            break;
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_UTIL_REQUEST_IDENTIFYING_CODE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    verificationCode = TypeUtil.getString(resultMap.get(APIKey.USER_IDENTIFYING_CODE), "");

                    showToast(getString(R.string.toast_msg_has_send_identifying_code));

                    CountDownButton requestIdentifyingBtn = findView(R.id.btn_get_verification_code);
                    requestIdentifyingBtn.startCountDown();

                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_USER_RESET_PASSWORD.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast(getString(R.string.toast_msg_set_new_psw_successfully));

                finish();
            } else {
                showToast(getString(R.string.toast_msg_set_new_psw_failed));
            }
            dimissProgressDialog();
        }
    }
}
