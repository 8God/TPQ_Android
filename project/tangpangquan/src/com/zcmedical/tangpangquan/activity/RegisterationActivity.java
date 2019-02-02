package com.zcmedical.tangpangquan.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.XEditText;
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

public class RegisterationActivity extends BaseActivity implements OnClickListener, PlatformActionListener {

    private String verificationCode;
    private int requestCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registeration);

        init();
    }

    private void init() {
        initToolbar(getString(R.string.title_registeration_activity));
        initUI();
    }

    private void initUI() {
        CountDownButton requestIdentifyingBtn = findView(R.id.btn_get_verification_code);
        requestIdentifyingBtn.setOnClickListener(this);

        Button registerBtn = findView(R.id.btn_register);
        registerBtn.setOnClickListener(this);

        setViewClickListener(R.id.imvbtn_logon_qq, this);
        setViewClickListener(R.id.imvbtn_logon_wechat, this);
        setViewClickListener(R.id.imvbtn_logon_weibo, this);
        
        final ClearTextEditText pswEdt = findView(R.id.ctedt_user_psw);
        pswEdt.setTag(false); //tag = false 表示密码是隐藏状态
        setViewClickListener(R.id.imvbtn_show_psw, this);

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

    private void requestIdentifyingCode(String mobile) {
        requestCount = TpqApplication.getInstance().getTodayRequestIdentifyingCodeCount();
        if (requestCount <= 5) {
            CommonRequest requestIdentifying = new CommonRequest();
            requestIdentifying.setRequestApiName(InterfaceConstant.API_UTIL_REQUEST_IDENTIFYING_CODE);
            requestIdentifying.setRequestID(InterfaceConstant.REQUEST_ID_UTIL_REQUEST_IDENTIFYING_CODE);
            requestIdentifying.addRequestParam(APIKey.USER_MOBILE, mobile);

            addRequestAsyncTask(requestIdentifying);

            //            showProgressDialog(getString(R.string.pb_msg_get_identify));
        } else {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.dialog_msg_refuse_request_identifying_code));
        }
    }

    private void register() {
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
            sendRegisterRequest(mobile, psw);
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

    private void sendRegisterRequest(String mobile, String psw) {
        CommonRequest registerRequest = new CommonRequest();
        registerRequest.setRequestApiName(InterfaceConstant.API_USER_REGISTER);
        registerRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_REGISTER);
        registerRequest.addRequestParam(APIKey.USER_MOBILE, mobile);
        registerRequest.addRequestParam(APIKey.USER_PASSWORD, psw);

        addRequestAsyncTask(registerRequest);

        showProgressDialog(getString(R.string.pd_msg_registering));
    }

    private void startHome() {
        Intent startHome = new Intent(getContext(), HomeActivity.class);
        startActivity(startHome);
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
            case R.id.imvbtn_show_psw:
                final XEditText pswEdt = findView(R.id.ctedt_user_psw);
                final ImageButton showPswBtn = findView(R.id.imvbtn_show_psw);
                boolean isShowPsw = TypeUtil.getBoolean(pswEdt.getTag(), false);
                if (isShowPsw) {
                    pswEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPswBtn.setImageDrawable(getResources().getDrawable(R.drawable.log_btn_pswhide_normal));
                } else {
                    pswEdt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPswBtn.setImageDrawable(getResources().getDrawable(R.drawable.log_btn_pswshow_normal));
                }
                pswEdt.setTag(!isShowPsw);
                pswEdt.setSelection(pswEdt.getText().toString().length());
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.imvbtn_logon_qq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
            case R.id.imvbtn_logon_wechat:
                Platform weixin = ShareSDK.getPlatform(Wechat.NAME);
                authorize(weixin);
                break;
            case R.id.imvbtn_logon_weibo:
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(weibo);
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

                    TpqApplication.getInstance().setTodayRequestIdentifyingCodeCount(++requestCount);
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_USER_REGISTER.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    showToast(getString(R.string.register_successful));
                    UserEntity user = EntityUtils.getUserEntity(TypeUtil.getMap(resultMap.get(APIKey.USER)));
                    if (null != user) {
                        TpqApplication.getInstance(getContext()).setUser(user);
                    }

                    startHome();
                    finish();
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    showToast(getString(R.string.logon_sucessful));
                    UserEntity user = EntityUtils.getUserEntity(TypeUtil.getMap(resultMap.get(APIKey.USER)));
                    if (null != user) {
                        final XEditText pswEdt = findView(R.id.ctedt_user_psw);
                        String psw = pswEdt.getText() != null ? pswEdt.getText().toString() : "";
                        if (InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON.equals(requestID)) { //如果是第三方登陆，密码为123456
                            psw = "123456";
                        }

                        String pswMD5 = MD5UUtil.generate(psw);
                        user.setPswMD5(pswMD5);
                        TpqApplication.getInstance(getContext()).setUser(user);
                    }
                    Intent startHome = new Intent(getContext(), HomeActivity.class);
                    startActivity(startHome);
                    finish();
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
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
                String mobile = getEditTextInput(R.id.ctedt_user_mobile, "");
                requestIdentifyingCode(mobile);
            }
        }
    }

    //执行授权,获取用户信息
    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(Platform plat) {

        //判断指定平台是否已经完成授权
        if (plat.isValid() && plat.getDb() != null) {
            String userId = plat.getDb().getUserId();
            String thirdLogonName = plat.getDb().getUserName();
            if (userId != null) {
                showToast("授权成功，正在登陆");
                thirdPartyLogon(plat.getName(), userId, thirdLogonName);
                return;
            }
        }

        plat.setPlatformActionListener(this);
        //关闭SSO授权
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    /********************************* 第三方登陆回调 *****************************************************/

    @Override
    public void onCancel(Platform platform, int action) {
        showToast("取消第三方登陆");
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> acountInfo) {
        String platformName = platform.getName();
        if (!TextUtils.isEmpty(platformName) && action == Platform.ACTION_USER_INFOR) {
            Log.i("cth", "platformName = " + platformName + ",acountInfo = " + acountInfo);
            Log.i("cth", "UserId = " + platform.getDb().getUserId());
            if (null != platform.getDb()) {
                String thirdLogonName = platform.getDb().getUserName();
                thirdPartyLogon(platformName, platform.getDb().getUserId(), thirdLogonName);
            } else {
                showToast("第三方登陆失败");
            }
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        showToast("第三方登陆失败");
    }

    /*****************************************************************************************************/

    private void thirdPartyLogon(String platformName, String platformUserId, String thirdLogonName) {
        CommonRequest thirdPartyLogonRequest = new CommonRequest();
        thirdPartyLogonRequest.setRequestApiName(InterfaceConstant.API_USER_THIRD_LOGON);
        thirdPartyLogonRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON);
        if (!TextUtils.isEmpty(platformName)) {
            if (QQ.NAME.equals(platformName)) {
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_QQ_IDENTIFY, platformUserId);
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_QQ, thirdLogonName);
            } else if (Wechat.NAME.equals(platformName)) {
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIXIN_IDENTIFY, platformUserId);
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIXIN, thirdLogonName);
            } else if (SinaWeibo.NAME.equals(platformName)) {
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIBO_IDENTIFY, platformUserId);
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIBO, thirdLogonName);
            }

            addRequestAsyncTask(thirdPartyLogonRequest);

            showProgressDialog("登录中");
        }
    }
}
