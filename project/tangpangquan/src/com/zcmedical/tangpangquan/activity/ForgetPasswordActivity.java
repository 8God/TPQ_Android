package com.zcmedical.tangpangquan.activity;

import java.util.List;
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
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.common.widget.ClearTextEditText;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class ForgetPasswordActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_password);

        init();
    }

    private void init() {
        initToolbar(getString(R.string.title_forget_password_activity));
        initUI();
    }

    private void initUI() {
        setViewClickListener(R.id.btn_forget_psw_next, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_forget_psw_next:
            checkMobile();
            break;

        default:
            break;
        }
    }

    private void checkMobile() {
        ClearTextEditText mobileEdt = findView(R.id.ctedt_mobile);
        String mobile = mobileEdt.getText() != null ? mobileEdt.getText().toString() : "";
        if (!Validator.isPhoneNumber(mobile)) {
            mobileEdt.setError(getString(R.string.error_msg_mobile_error));
        } else {
            checkUser(mobile);
        }
    }

    private void checkUser(String mobile) {
        CommonRequest checkMobileRequest = new CommonRequest();
        checkMobileRequest.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        checkMobileRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        checkMobileRequest.addRequestParam(APIKey.USER_MOBILE, mobile);
        checkMobileRequest.addAdditionalArg(APIKey.USER_MOBILE, mobile);

        addRequestAsyncTask(checkMobileRequest);

        showProgressDialog(getString(R.string.pb_msg_get_identify));
    }

    private void requestIdentifyingCode(String userId, String mobile) {
        CommonRequest requestIdentifying = new CommonRequest();
        requestIdentifying.setRequestApiName(InterfaceConstant.API_UTIL_REQUEST_IDENTIFYING_CODE);
        requestIdentifying.setRequestID(InterfaceConstant.REQUEST_ID_UTIL_REQUEST_IDENTIFYING_CODE);
        requestIdentifying.addRequestParam(APIKey.USER_MOBILE, mobile);
        requestIdentifying.addAdditionalArg(APIKey.USER_ID, userId);
        requestIdentifying.addAdditionalArg(APIKey.USER_MOBILE, mobile);

        addRequestAsyncTask(requestIdentifying);

    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            boolean isValidMobile = false;
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        List<UserEntity> userList = EntityUtils.getUserEntityList(userMapList);
                        if (null != userList && userList.size() > 0) {
                            UserEntity user = userList.get(0);
                            if (null != user) {
                                String userId = user.getId();
                                String mobile = user.getMobile();
                                String userMobile = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_MOBILE));
                                if (Validator.isIdValid(userId) && Validator.isMobilePhoneNumber(userMobile) && Validator.isMobilePhoneNumber(mobile) && userMobile.equals(mobile)) {
                                    requestIdentifyingCode(userId, mobile);
                                    isValidMobile = true;
                                }
                            }
                        }
                    }
                }
            }
            if (!isValidMobile) {
                DialogUtils.showAlertDialog(getContext(), getString(R.string.dialog_msg_invalid_mobile));
                dimissProgressDialog();
            }
        } else if (InterfaceConstant.REQUEST_ID_UTIL_REQUEST_IDENTIFYING_CODE.equals(requestID)) {
            boolean isGetVerificationCode = false;
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    String verificationCode = TypeUtil.getString(resultMap.get(APIKey.USER_IDENTIFYING_CODE), "");
                    if (!TextUtils.isEmpty(verificationCode)) {
                        isGetVerificationCode = true;
                        String userId = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_ID));
                        String userMobile = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_MOBILE));

                        Intent openSecondStep = new Intent(getContext(), ForgetPswSecondStepActivity.class);
                        openSecondStep.putExtra(ForgetPswSecondStepActivity.KEY_USER_ID, userId);
                        openSecondStep.putExtra(ForgetPswSecondStepActivity.KEY_MOBILE, userMobile);
                        openSecondStep.putExtra(ForgetPswSecondStepActivity.KEY_VERIFICATION_CODE, verificationCode);

                        startActivity(openSecondStep);

                        finish();
                    }
                }
            }
            if (!isGetVerificationCode) {
                showToast(getString(R.string.toast_msg_get_identifying_code_fail));
            }
            dimissProgressDialog();
        }
    }
}
