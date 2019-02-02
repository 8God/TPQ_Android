package com.zcmedical.tangpangquan.activity;

import java.util.List;
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
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class NickNameSettingActivity extends BaseActivity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nickname_setting);

        init();
    }

    private void init() {
        initToolbar("我的昵称");
        initUI();
    }

    private void initUI() {
        setViewClickListener(R.id.btn_save_nickname, this);
        String nickname = "";
        UserEntity user = TpqApplication.getInstance().getUser();
        if (null != user) {
            nickname = user.getNickname();
        }

        setEditText(R.id.edt_nickname, nickname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_save_nickname:
            setNickName();
            break;

        default:
            break;
        }
    }

    private void setNickName() {
        String nickname = getEditTextInput(R.id.edt_nickname, "");
        if (TextUtils.isEmpty(nickname)) {
            showToast("没有昵称不能保存喔");

            return;
        }

        checkNickNameIsExisting(nickname);
    }

    private void checkNickNameIsExisting(String nickname) {
        CommonRequest checkNickname = new CommonRequest();
        checkNickname.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        checkNickname.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        checkNickname.addRequestParam(APIKey.USER_NICKNAME, nickname);
        checkNickname.addAdditionalArg(APIKey.USER_NICKNAME, nickname);

        addRequestAsyncTask(checkNickname);

        showProgressDialog();
    }

    private void updateNickName(String nickname) {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest updateNickname = new CommonRequest();
        updateNickname.setRequestApiName(InterfaceConstant.API_USER_INFO_UPDATE);
        updateNickname.setRequestID(InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE);
        updateNickname.addRequestParam(APIKey.USER_NICKNAME, nickname);
        updateNickname.addRequestParam(APIKey.COMMON_ID, userId);
        updateNickname.addAdditionalArg(APIKey.USER_NICKNAME, nickname);

        addRequestAsyncTask(updateNickname);

    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        boolean isNicknameExisting = false;
        if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        List<UserEntity> userList = EntityUtils.getUserEntityList(userMapList);
                        if (null != userList && userList.size() > 0) {
                            UserEntity user = userList.get(0);
                            if (null != user) {
                                String nickname = user.getNickname();
                                String nickNameParams = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_NICKNAME), "");
                                if (!TextUtils.isEmpty(nickname) && !TextUtils.isEmpty(nickNameParams) && nickname.equals(nickNameParams)) {
                                    isNicknameExisting = true;
                                }
                            }
                        }
                    }
                }
            }

            if (isNicknameExisting) {
                dimissProgressDialog();
                showToast("昵称已被使用，换一个试试");
            } else {
                String nickNameParams = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_NICKNAME), "");
                if (!TextUtils.isEmpty(nickNameParams)) {
                    updateNickName(nickNameParams);
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_USER_INFO_UPDATE.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                setEditText(R.id.edt_nickname, null);
                showToast("昵称保存成功");

                String nickname = TypeUtil.getString(additionalArgsMap.get(APIKey.USER_NICKNAME), "");
                if (!TextUtils.isEmpty(nickname)) {
                    Intent nicknameIntent = new Intent();
                    nicknameIntent.putExtra(APIKey.USER_NICKNAME, nickname);
                    setResult(RESULT_OK, nicknameIntent);
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
        String nickname = getEditTextInput(R.id.edt_nickname, "");
        if (!TextUtils.isEmpty(nickname)) {
            DialogUtils.showAlertDialog(getContext(), "放弃昵称修改？", getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setEditText(R.id.edt_nickname, null);
                    finish();
                }

            }, getString(R.string.no), null);
        } else {
            super.finish();
        }
    }
}
