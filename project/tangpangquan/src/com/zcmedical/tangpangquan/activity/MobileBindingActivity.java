package com.zcmedical.tangpangquan.activity;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.MD5UUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.common.widget.ClearTextEditText;
import com.zcmedical.common.widget.CountDownButton;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class MobileBindingActivity extends BaseActivity implements OnClickListener {

    private String verificationCode;
    private int requestCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mobile_binding);

        init();
    }

    private void init() {
        initToolbar("手机绑定");
        initUI();
    }

    private void initUI() {
        CountDownButton requestIdentifyingBtn = findView(R.id.btn_get_verification_code);
        requestIdentifyingBtn.setOnClickListener(this);

        Button registerBtn = findView(R.id.btn_binding);
        registerBtn.setOnClickListener(this);

    }

    private void requestIdentifyingCode(String mobile) {
        requestCount = TpqApplication.getInstance().getTodayRequestIdentifyingCodeCount();
        if (requestCount <= 5) {
            CommonRequest requestIdentifying = new CommonRequest();
            requestIdentifying.setRequestApiName(InterfaceConstant.API_UTIL_REQUEST_IDENTIFYING_CODE);
            requestIdentifying.setRequestID(InterfaceConstant.REQUEST_ID_UTIL_REQUEST_IDENTIFYING_CODE);
            requestIdentifying.addRequestParam(APIKey.USER_MOBILE, mobile);

            addRequestAsyncTask(requestIdentifying);

        } else {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.dialog_msg_refuse_request_identifying_code));
        }
    }

    private void bindMobile() {
        ClearTextEditText mobileXEdt = findView(R.id.ctedt_user_mobile);
        ClearTextEditText verificationCodeXEdt = findView(R.id.ctedt_verification_code);
        ClearTextEditText pswXEdt = findView(R.id.ctedt_user_psw);

        String mobile = mobileXEdt.getText().toString();
        String verificationCode = verificationCodeXEdt.getText().toString();
        String psw = pswXEdt.getText().toString();

        boolean islegalInput = true;

        islegalInput = isMobileInputLegal(mobile);
        islegalInput = isVerificationCodeInputLegal(verificationCode);
        islegalInput = isPswInputLegal(psw);

        if (islegalInput) {
            sendBindMobileRequest(mobile, psw);
        }
    }

    private boolean isMobileInputLegal(String mobile) {
        boolean islegalInput = true;

        if (!Validator.isPhoneNumber(mobile)) {
            islegalInput = false;
            ClearTextEditText mobileXEdt = findView(R.id.ctedt_user_mobile);
            mobileXEdt.setError(getString(R.string.error_msg_mobile_error));
        }

        return islegalInput;
    }

    private boolean isVerificationCodeInputLegal(String verificationCode) {
        boolean islegalInput = true;
        ClearTextEditText verificationCodeXEdt = findView(R.id.ctedt_verification_code);

        if (TextUtils.isEmpty(verificationCode)) {
            islegalInput = false;
            verificationCodeXEdt.setError(getString(R.string.error_msg_empty_verification_code));
        } else if (!this.verificationCode.equals(verificationCode)) {
            islegalInput = false;
            verificationCodeXEdt.setError(getString(R.string.error_msg_verification_code_error));
        }

        return islegalInput;
    }

    private boolean isPswInputLegal(String psw) {
        boolean islegalInput = true;

        if (!Validator.isPassword(psw)) {
            islegalInput = false;
            ClearTextEditText pswXEdt = findView(R.id.ctedt_user_psw);
            pswXEdt.setError(getString(R.string.error_msg_psw_error));
        }

        return islegalInput;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_verification_code:
                ClearTextEditText mobileXEdt = findView(R.id.ctedt_user_mobile);
                String mobile = mobileXEdt.getText() != null ? mobileXEdt.getText().toString() : "";
                if (!TextUtils.isEmpty(mobile)) {
                    checkMobileIsExisting();
                } else {
                    showToast(getString(R.string.input_mobile_hint));
                }
                break;
            case R.id.btn_binding:
                bindMobile();
                break;
            default:
                break;
        }
    }

    /**
     * 先检查手机号是否被注册过，注册过则无法获取验证码
     */
    private void checkMobileIsExisting() {
        String mobile = getEditTextInput(R.id.ctedt_user_mobile, "");
        CommonRequest checkMobileRequest = new CommonRequest();
        checkMobileRequest.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        checkMobileRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        checkMobileRequest.addRequestParam(APIKey.USER_MOBILE, mobile);

        addRequestAsyncTask(checkMobileRequest);

        showProgressDialog(getString(R.string.pb_msg_get_identify));
    }

    private void sendBindMobileRequest(String mobile, String psw) {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest registerRequest = new CommonRequest();
        registerRequest.setRequestApiName(InterfaceConstant.API_USER_INFO_UPDATE);
        registerRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE);
        registerRequest.addRequestParam(APIKey.USER_MOBILE, mobile);
        registerRequest.addRequestParam(APIKey.USER_PASSWORD, psw);
        registerRequest.addRequestParam(APIKey.COMMON_ID, userId);

        registerRequest.addAdditionalArg(APIKey.USER_MOBILE, mobile);
        registerRequest.addAdditionalArg(APIKey.USER_PASSWORD, MD5UUtil.generate(psw));

        addRequestAsyncTask(registerRequest);

        showProgressDialog("手机号绑定中");
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

                    TpqApplication.getInstance().setTodayRequestIdentifyingCodeCount(++requestCount);
                }
            } else {
                showToast(message);
            }

            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE.equals(requestID)) {
            boolean isBindingMobile = false;
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    Map<String, Object> userMap = TypeUtil.getMap(resultMap.get(APIKey.USER));
                    if (null != userMap) {
                        isBindingMobile = true;
                        UserEntity logonUser = TpqApplication.getInstance().getUser(); //保存绑定的手机号和设置的密码
                        if (null != logonUser) {
                            String mobile = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_MOBILE), "");
                            String pswMD5 = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_PASSWORD), "");

                            logonUser.setMobile(mobile);
                            logonUser.setPswMD5(pswMD5);

                            TpqApplication.getInstance().setUser(logonUser);
                        }

                    }
                }
            }
            dimissProgressDialog();
            if (isBindingMobile) {
                showToast("成功绑定手机");
                setResult(RESULT_OK);

                finish();
            } else {
                showToast("绑定失败,请重试");
            }
        } else if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            boolean isExisting = false;
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        isExisting = true;
                    }
                }
            }

            if (isExisting) {
                showToast("该手机号已被使用");
                dimissProgressDialog();
            } else {
                String mobile  = getEditTextInput(R.id.ctedt_user_mobile, "");
                requestIdentifyingCode(mobile);
            }
        }

    }

}
