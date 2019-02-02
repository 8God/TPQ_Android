package com.zcmedical.tangpangquan.activity;

import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.MD5UUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class ModifyPasswordActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_password);

        init();
    }

    private void init() {
        initToolbar("修改密码");
        initUI();
    }

    private void initUI() {
        setViewClickListener(R.id.btn_save_new_psw, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_new_psw:
                modifyPassword();
                break;

            default:
                break;
        }
    }

    private void modifyPassword() {
        String oldPsw = getEditTextInput(R.id.ctedt_old_psw, "");
        String newPsw = getEditTextInput(R.id.ctedt_new_psw, "");
        String newPswConfirm = getEditTextInput(R.id.ctedt_new_psw_confirm, "");

        boolean isOldPswLegal = checkOldPassword(oldPsw);
        boolean isNewPswLegal = checkNewPassword(newPsw, newPswConfirm);

        if (isOldPswLegal && isNewPswLegal) {
            sendModifyPasswordRequest();
        }

    }

    private boolean checkOldPassword(String oldPsw) {
        boolean isOldPswLegal = true;
        String oldPswMD5 = MD5UUtil.generate(oldPsw);
        String pswMD5 = "";
        UserEntity user = TpqApplication.getInstance().getUser();
        if (null != user) {
            pswMD5 = user.getPswMD5();
        }
        if (!Validator.isPassword(oldPsw) || !pswMD5.equals(oldPswMD5)) {
            EditText oldPswEdt = findView(R.id.ctedt_old_psw);
            oldPswEdt.setError("旧密码错误");

            isOldPswLegal = false;
        }

        return isOldPswLegal;
    }

    private boolean checkNewPassword(String newPsw, String newPswConfirm) {
        boolean isNewPswLegal = true;

        if (!Validator.isPassword(newPsw)) {
            EditText newPswEdt = findView(R.id.ctedt_new_psw);
            newPswEdt.setError("新密码格式错误，长度为6-16位");

            isNewPswLegal = false;
        }

        if (!Validator.isPassword(newPswConfirm)) {
            EditText newPswConfirmEdt = findView(R.id.ctedt_new_psw_confirm);
            newPswConfirmEdt.setError("确认新密码格式错误，长度为6-16位");

            isNewPswLegal = false;
        }

        if (Validator.isPassword(newPsw) && Validator.isPassword(newPswConfirm) && !newPsw.equals(newPswConfirm)) {
            EditText newPswConfirmEdt = findView(R.id.ctedt_new_psw_confirm);
            newPswConfirmEdt.setError("两次密码输入不一致");

            isNewPswLegal = false;
        }

        return isNewPswLegal;
    }

    private void sendModifyPasswordRequest() {
        String userId = TpqApplication.getInstance().getUserId();
        String newPsw = getEditTextInput(R.id.ctedt_new_psw, "");
        CommonRequest modifyPasswordRequest = new CommonRequest();
        modifyPasswordRequest.setRequestApiName(InterfaceConstant.API_USER_INFO_UPDATE);
        modifyPasswordRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE);
        modifyPasswordRequest.addRequestParam(APIKey.COMMON_ID, userId);
        modifyPasswordRequest.addRequestParam(APIKey.USER_PASSWORD, newPsw);
        modifyPasswordRequest.addAdditionalArg(APIKey.USER_PASSWORD, newPsw);

        addRequestAsyncTask(modifyPasswordRequest);

        showProgressDialog("修改密码中");
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);

        if (InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("密码修改成功");
                UserEntity user = TpqApplication.getInstance().getUser();

                if (null != user) {
                    String newPsw = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_PASSWORD), "");
                    String pswMD5 = MD5UUtil.generate(newPsw);
                    user.setPswMD5(pswMD5);

                    TpqApplication.getInstance().setUser(user);
                    TpqApplication.getInstance().setHxPassword(newPsw);
                }

                finish();
            } else {
                showToast(message);
            }

            dimissProgressDialog();
        }
    }

}
