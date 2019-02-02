package com.zcmedical.common.utils;

import java.io.File;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.PictureUploader;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonAsyncConnector;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.network.IConnectorToRenderListener;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class UserHeadPicUploader extends PictureUploader {

    private static final String DIALOG_TITLE = "我的头像";

    private OnUserHeadPicUpdateCallback onUserHeadPicUpdateCallback;

    public UserHeadPicUploader(Context context) {
        super(context, DIALOG_TITLE);
    }

    @Override
    public void uploadPictureRequest(File pictureFile) {
        if (null != pictureFile && pictureFile.exists()) {
            String userId = TpqApplication.getInstance().getUserId();
            CommonRequest updateUserHeadRequest = new CommonRequest();
            updateUserHeadRequest.setRequestApiName(InterfaceConstant.API_USER_INFO_UPDATE);
            updateUserHeadRequest.addRequestParam(APIKey.COMMON_ID, userId);
            updateUserHeadRequest.addRequestParam(APIKey.USER_USER_HEAD_PIC, pictureFile.getAbsoluteFile());

            CommonAsyncConnector commonAsyncConnector = new CommonAsyncConnector(context, new IConnectorToRenderListener() {

                @Override
                public void toRender(Map<String, Object> result) {
                    if (null != result) {
                        int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                        String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
                        if (status == APIKey.STATUS_SUCCESSFUL) {
                            Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                            if (null != resultMap) {
                                Map<String, Object> userMap = TypeUtil.getMap(resultMap.get(APIKey.USER));
                                if (null != userMap) {
                                    UserEntity user = EntityUtils.getUserEntity(userMap);
                                    Log.i("cth", "UserHeadPicUploader:user = " + user);
                                    if (null != user) {
                                        Log.i("cth", "UserHeadPicUploader:user.getHeadPic() = " + user.getHeadPic());

                                        Toast.makeText(context, "修改头像成功", Toast.LENGTH_SHORT).show();

                                        if (null != onUserHeadPicUpdateCallback) {
                                            onUserHeadPicUpdateCallback.onUserHeadPicUpdate(user);
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    dimissProgressDialog();
                }
            });

            commonAsyncConnector.execute(updateUserHeadRequest);

            showProgressDialog("修改头像");
        }
    }

    public OnUserHeadPicUpdateCallback getOnUserHeadPicUpdateCallback() {
        return onUserHeadPicUpdateCallback;
    }

    public void setOnUserHeadPicUpdateCallback(OnUserHeadPicUpdateCallback onUserHeadPicUpdateCallback) {
        this.onUserHeadPicUpdateCallback = onUserHeadPicUpdateCallback;
    }

    public interface OnUserHeadPicUpdateCallback {
        void onUserHeadPicUpdate(UserEntity user);
    }

}
