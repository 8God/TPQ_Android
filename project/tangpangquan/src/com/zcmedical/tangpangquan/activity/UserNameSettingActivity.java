package com.zcmedical.tangpangquan.activity;

import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class UserNameSettingActivity extends BaseActivity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_username_setting);

        init();
    }

    private void init() {
        initToolbar("真实姓名");
        initUI();
    }

    private void initUI() {
        setViewClickListener(R.id.btn_save_username, this);
        String username = "";
        UserEntity user = TpqApplication.getInstance().getUser();
        if (null != user) {
            username = user.getUsername();
        }
        
        setEditText(R.id.edt_username, username);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_save_username:
            setUserName();
            break;

        default:
            break;
        }
    }

    private void setUserName() {
        String username = getEditTextInput(R.id.edt_username, "");
        if (TextUtils.isEmpty(username)) {
            showToast("没有真实姓名不能保存喔");
        } else {
            updateUserName(username);
        }

    }

    private void updateUserName(String username) {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest updateUsername = new CommonRequest();
        updateUsername.setRequestApiName(InterfaceConstant.API_USER_INFO_UPDATE);
        updateUsername.setRequestID(InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE);
        updateUsername.addRequestParam(APIKey.USER_USERNAME, username);
        updateUsername.addRequestParam(APIKey.COMMON_ID, userId);
        updateUsername.addAdditionalArg(APIKey.USER_USERNAME, username);

        addRequestAsyncTask(updateUsername);

        showProgressDialog("正在保存真实姓名");
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                setEditText(R.id.edt_username, null);
                showToast("真实姓名保存成功");

                String username = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_USERNAME), "");
                if (!TextUtils.isEmpty(username)) {
                    Intent usernameIntent = new Intent();
                    usernameIntent.putExtra(APIKey.USER_USERNAME, username);
                    setResult(RESULT_OK, usernameIntent);
                }

                finish();
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        }
    }

    @Override
    public void finish() {
        String username = getEditTextInput(R.id.edt_username, "");
        if (!TextUtils.isEmpty(username)) {
            DialogUtils.showAlertDialog(getContext(), "放弃真实姓名的修改？", getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setEditText(R.id.edt_username, null);
                    finish();
                }

            }, getString(R.string.no), null);
        } else {
            super.finish();
        }
    }
}
