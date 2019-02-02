package com.zcmedical.tangpangquan.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.PictureShowingActivity;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.MedicalRecordPicEntity;

public class MedicalRecordPicShowingActivity extends PictureShowingActivity<MedicalRecordPicEntity> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);

        entity = TpqApplication.getInstance().getShowingMedicalRecordPicEntity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TpqApplication.getInstance().setShowingMedicalRecordPicEntity(null);
    }

    protected void deletePic() {
        if (null != entity) {
            String userId = TpqApplication.getInstance().getUserId();
            CommonRequest deleteRecordPicRequest = new CommonRequest();
            deleteRecordPicRequest.setRequestApiName(InterfaceConstant.API_MEDICAL_RECORD_PIC_REMOVE);
            deleteRecordPicRequest.setRequestID(InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_PIC_REMOVE);
            deleteRecordPicRequest.addRequestParam(APIKey.USER_ID, userId);
            deleteRecordPicRequest.addRequestParam(APIKey.COMMON_ID, entity.getId());

            addRequestAsyncTask(deleteRecordPicRequest);

            showProgressDialog("删除中");
        }
    }

    @Override
    protected void showPic(ImageView imageView) {
        if (null != entity && imageView != null) {
            setImageView(R.id.simv_showing_pic, entity.getMedicalRecordPic(), R.drawable.loading_bg);
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);

        if (InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_PIC_REMOVE.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    showToast("删除成功");

                    if (null != entity) {
                        Intent deleteRecordPic = new Intent();
                        deleteRecordPic.setAction(MedicalRecordActivity.ACTION_DELETE_IMAGE_RECORD);
                        deleteRecordPic.putExtra(APIKey.COMMON_ID, entity.getId());
                        sendBroadcast(deleteRecordPic);
                    }

                    finish();
                } else {
                    showToast(message);
                }
            }
            dimissProgressDialog();
        }

    }
}
