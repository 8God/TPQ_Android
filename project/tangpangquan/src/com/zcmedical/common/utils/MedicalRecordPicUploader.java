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
import com.zcmedical.tangpangquan.entity.MedicalRecordPicEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class MedicalRecordPicUploader extends PictureUploader {

    private onFinishUploadMedicalRecordPicCallBack onFinishUploadMedicalRecordPicCallBack;

    public MedicalRecordPicUploader(Context context) {
        super(context, "上传");

        isCancelable = false; //不可取消进度条窗口
    }

    @Override
    public void uploadPictureRequest(File pictureFile) {
        Log.i("cth", "uploadPictureRequest");
        if (null != pictureFile && pictureFile.exists()) {
            String userId = TpqApplication.getInstance().getUserId();

            CommonRequest uploadPictureRequest = new CommonRequest();
            uploadPictureRequest.setRequestApiName(InterfaceConstant.API_MEDICAL_RECORD_PIC_CREATE);
            uploadPictureRequest.addRequestParam(APIKey.USER_ID, userId);
            uploadPictureRequest.addRequestParam(APIKey.MEDICAL_RECORD_PIC, pictureFile.getAbsoluteFile());

            CommonAsyncConnector commonAsyncConnector = new CommonAsyncConnector(context, new IConnectorToRenderListener() {

                @Override
                public void toRender(Map<String, Object> result) {
                    if (null != result) {
                        int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                        String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
                        if (status == APIKey.STATUS_SUCCESSFUL) {
                            Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                            Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                            if (null != resultMap) {
                                Map<String, Object> medicalRecordPicMap = TypeUtil.getMap(resultMap.get(APIKey.MEDICAL_RECORD_PIC));
                                if (null != medicalRecordPicMap) {
                                    MedicalRecordPicEntity medicalRecordPicEntity = EntityUtils.getMedicalRecordPicEntity(medicalRecordPicMap);

                                    if (null != onFinishUploadMedicalRecordPicCallBack) {
                                        onFinishUploadMedicalRecordPicCallBack.onFinishUpload(medicalRecordPicEntity);
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

            commonAsyncConnector.execute(uploadPictureRequest);

            showProgressDialog("上传中，请稍后");
        }
    }

    public void setOnFinishUploadMedicalRecordPicCallBack(onFinishUploadMedicalRecordPicCallBack onFinishUploadMedicalRecordPicCallBack) {
        this.onFinishUploadMedicalRecordPicCallBack = onFinishUploadMedicalRecordPicCallBack;
    }

    public interface onFinishUploadMedicalRecordPicCallBack {
        void onFinishUpload(MedicalRecordPicEntity medicalRecordPicEntity);
    }

}
