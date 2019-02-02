package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.MedicalRecordPicUploader;
import com.zcmedical.common.utils.MedicalRecordPicUploader.onFinishUploadMedicalRecordPicCallBack;
import com.zcmedical.common.utils.OpenFileUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.MedicalRecordEntity;
import com.zcmedical.tangpangquan.entity.MedicalRecordPicEntity;
import com.zcmedical.tangpangquan.view.MedicalRecordInputView;

public class MedicalRecordActivity extends BaseActivity implements OnClickListener {

    public static String ACTION_ADD_INPUT_RECORD = "ACTION_ADD_INPUT_RECORD";
    public static String ACTION_DELETE_INPUT_RECORD = "ACTION_DELETE_INPUT_RECORD";
    public static String ACTION_ADD_IMAGE_RECORD = "ACTION_ADD_IMAGE_RECORD";
    public static String ACTION_DELETE_IMAGE_RECORD = "ACTION_DELETE_IMAGE_RECORD";

    private UpdateRecordListReceiver updateRecordListReceiver;

    private LinearLayout inputLayout;
    private LinearLayout uploadLayout;

    private List<MedicalRecordPicEntity> uploadPicList;
    private List<MedicalRecordEntity> inputRecordList;

    private boolean isFinishFetchRecordList = false;
    private boolean isFinishFetchPicList = false;

    private MedicalRecordPicUploader medicalRecordPicUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_body_check);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_ADD_INPUT_RECORD);
        filter.addAction(ACTION_DELETE_INPUT_RECORD);
        filter.addAction(ACTION_ADD_IMAGE_RECORD);
        filter.addAction(ACTION_DELETE_IMAGE_RECORD);
        updateRecordListReceiver = new UpdateRecordListReceiver();

        registerReceiver(updateRecordListReceiver, filter);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(updateRecordListReceiver);
    }

    private void init() {
        uploadPicList = new ArrayList<MedicalRecordPicEntity>();
        inputRecordList = new ArrayList<MedicalRecordEntity>();

        initToolbar("体检记录");
        initUI();
        fetchData();
    }

    private void fetchData() {
        fetchMedicalRecordList();
        fetchMedicalRecordPicList();

        showProgressDialog("获取体检记录中");
    }

    private void initUI() {
        setViewClickListener(R.id.btn_record_input, this);
        setViewClickListener(R.id.btn_record_upload, this);

        inputLayout = findView(R.id.ll_record_input);
        uploadLayout = findView(R.id.ll_record_upload);

        //        addInputRecordImage(null,0);
        //        addUploadRecordImage(null,0);
    }

    private void fetchMedicalRecordList() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchMedicalRecordEntitys = new CommonRequest();
        fetchMedicalRecordEntitys.setRequestApiName(InterfaceConstant.API_MEDICAL_RECORD_FETCH);
        fetchMedicalRecordEntitys.setRequestID(InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_FETCH);
        fetchMedicalRecordEntitys.addRequestParam(APIKey.USER_ID, userId);
        //        fetchMedicalRecordEntitys.addRequestParam(APIKey.COMMON_STATUS, APIKey.COMMON_STATUS_LEGAL);
        fetchMedicalRecordEntitys.addRequestParam(APIKey.COMMON_PAGE_SIZE, Integer.MAX_VALUE);
        fetchMedicalRecordEntitys.addRequestParam(APIKey.COMMON_OFFSET, 0);
        fetchMedicalRecordEntitys.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);
        fetchMedicalRecordEntitys.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.COMMON_CREATED_AT);

        addRequestAsyncTask(fetchMedicalRecordEntitys);
    }

    private void fetchMedicalRecordPicList() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchMedicalRecordPicListRequest = new CommonRequest();
        fetchMedicalRecordPicListRequest.setRequestApiName(InterfaceConstant.API_MEDICAL_RECORD_PIC_FETCH);
        fetchMedicalRecordPicListRequest.setRequestID(InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_PIC_FETCH);
        fetchMedicalRecordPicListRequest.addRequestParam(APIKey.USER_ID, userId);
        //        fetchMedicalRecordEntitys.addRequestParam(APIKey.COMMON_STATUS, APIKey.COMMON_STATUS_LEGAL);
        fetchMedicalRecordPicListRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, Integer.MAX_VALUE);
        fetchMedicalRecordPicListRequest.addRequestParam(APIKey.COMMON_OFFSET, 0);
        fetchMedicalRecordPicListRequest.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);
        fetchMedicalRecordPicListRequest.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.COMMON_CREATED_AT);

        addRequestAsyncTask(fetchMedicalRecordPicListRequest);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> medicalRecordMapList = TypeUtil.getList(resultMap.get(APIKey.MEDICAL_RECORDS));
                    if (null != medicalRecordMapList && medicalRecordMapList.size() > 0) {
                        inputRecordList = EntityUtils.getMedicalRecordEntityList(medicalRecordMapList);
                    }
                }
            }

            isFinishFetchRecordList = true;
            showDataList();
        } else if (InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_PIC_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> medicalRecordPicMapList = TypeUtil.getList(resultMap.get(APIKey.MEDICAL_RECORD_PICS));
                    if (null != medicalRecordPicMapList && medicalRecordPicMapList.size() > 0) {
                        uploadPicList = EntityUtils.getMedicalRecordPicEntityList(medicalRecordPicMapList);
                    }
                }
            }

            isFinishFetchPicList = true;
            showDataList();
        }
    }

    private void showDataList() {
        if (isFinishFetchRecordList && isFinishFetchPicList) {
            if (null != inputRecordList && inputRecordList.size() > 0) {
                for (int i = 0; i < inputRecordList.size(); i++) {
                    MedicalRecordEntity medicalRecordEntity = inputRecordList.get(i);

                    addInputRecordImage(medicalRecordEntity, i);
                }
            } else { //如果没有数据则添加一个未输入体测数据的View
                addInputRecordImage(null, 0);
            }
            if (null != uploadPicList && uploadPicList.size() > 0) {
                for (int i = 0; i < uploadPicList.size(); i++) {
                    MedicalRecordPicEntity medicalRecordPicEntity = uploadPicList.get(i);

                    addUploadRecordImage(medicalRecordPicEntity, i);
                }
            } else {//如果没有数据则添加一个未上传体测图片的View
                addUploadRecordImage(null, 0);
            }

            dimissProgressDialog();
        }
    }

    /**
     * 添加输入体检记录的View
     * 
     * @param medicalRecordEntity
     *            记录实体
     */
    private void addInputRecordImage(final MedicalRecordEntity medicalRecordEntity, int position) {
        LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 120), DensityUtil.dip2px(getContext(), 160));
        FrameLayout.LayoutParams layoutParams = (LayoutParams) inputLayout.getLayoutParams();
        if (null != inputRecordList && inputRecordList.size() > 1) { //图片数大于1
            layoutParams.gravity = Gravity.LEFT; //改变上传图片列表的Gravity为居左
            inputLayout.setLayoutParams(layoutParams);

            //            imvParams.rightMargin = DensityUtil.dip2px(getContext(), 8); //第二张图片开始设置左边距
        } else {
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL; //一张图片时，改变上传图片列表的Gravity为居中
            inputLayout.setLayoutParams(layoutParams);
        }

        imvParams.leftMargin = DensityUtil.dip2px(getContext(), 4);
        imvParams.rightMargin = DensityUtil.dip2px(getContext(), 4);
        MedicalRecordInputView bodyCheckInputView = new MedicalRecordInputView(getContext(), medicalRecordEntity);
        bodyCheckInputView.setLayoutParams(imvParams);
        bodyCheckInputView.setId(2000 + inputRecordList.size());

        inputLayout.addView(bodyCheckInputView, position);

    }

    /**
     * 删除输入体检记录的View
     * 
     * @param medicalRecordEntity
     * @param position
     */
    private void removeInputRecordView(final MedicalRecordEntity medicalRecordEntity, int position) {
        inputLayout.removeViewAt(position);

        FrameLayout.LayoutParams layoutParams = (LayoutParams) inputLayout.getLayoutParams();
        if (null != inputRecordList && inputRecordList.size() > 1) { //图片数大于1
            layoutParams.gravity = Gravity.LEFT; //改变上传图片列表的Gravity为居左
            inputLayout.setLayoutParams(layoutParams);

        } else {
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL; //一张图片时，改变上传图片列表的Gravity为居中
            inputLayout.setLayoutParams(layoutParams);
        }

    }

    /**
     * 添加上传图片的ImageView
     * 
     * @param medicalRecordPicEntity
     *            图片实体
     */
    private void addUploadRecordImage(final MedicalRecordPicEntity medicalRecordPicEntity, int position) {
        LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 120), DensityUtil.dip2px(getContext(), 160));
        FrameLayout.LayoutParams layoutParams = (LayoutParams) uploadLayout.getLayoutParams();

        if (null != uploadPicList && uploadPicList.size() > 1) { //图片数大于1
            layoutParams.gravity = Gravity.LEFT; //改变上传图片列表的Gravity为居左
            uploadLayout.setLayoutParams(layoutParams);

            //            imvParams.leftMargin = DensityUtil.dip2px(getContext(), 8); //第二张图片开始设置左边距
        } else {
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL; //一张图片时，改变上传图片列表的Gravity为居中
            uploadLayout.setLayoutParams(layoutParams);
        }

        imvParams.leftMargin = DensityUtil.dip2px(getContext(), 4);
        imvParams.rightMargin = DensityUtil.dip2px(getContext(), 4);
        ImageView recordImv = new ImageView(getContext());
        recordImv.setScaleType(ScaleType.CENTER_CROP);
        recordImv.setLayoutParams(imvParams);
        recordImv.setId(1000 + position);
        uploadLayout.addView(recordImv, position);

        if (null == medicalRecordPicEntity) {
            Drawable drawable = getResources().getDrawable(R.drawable.bodycheck_img_defaultupload);
            recordImv.setImageDrawable(drawable);
        } else {
            setImageView(recordImv.getId(), medicalRecordPicEntity.getMedicalRecordPic(), R.drawable.bodycheck_img_defaultupload);
            recordImv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //                    OpenFileUtil.openFile(getContext(), medicalRecordPicEntity.getMedicalRecordPic());
                    TpqApplication.getInstance().setShowingMedicalRecordPicEntity(medicalRecordPicEntity);
                    startActivity(new Intent(getContext(), MedicalRecordPicShowingActivity.class));
                }
            });
        }
    }

    /**
     * 删除输入体检记录的View
     * 
     * @param medicalRecordPicEntity
     * @param position
     */
    private void removeUploadRecordImage(final MedicalRecordPicEntity medicalRecordPicEntity, int position) {
        uploadLayout.removeViewAt(position);

        FrameLayout.LayoutParams layoutParams = (LayoutParams) uploadLayout.getLayoutParams();
        if (null != uploadPicList && uploadPicList.size() > 1) { //图片数大于1
            layoutParams.gravity = Gravity.LEFT; //改变上传图片列表的Gravity为居左
            uploadLayout.setLayoutParams(layoutParams);

        } else {
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL; //一张图片时，改变上传图片列表的Gravity为居中
            uploadLayout.setLayoutParams(layoutParams);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record_input:
                Intent openInputRecord = new Intent(getContext(), MedicalRecordInputActivity.class);
                startActivity(openInputRecord);
                break;
            case R.id.btn_record_upload:
                if (medicalRecordPicUploader == null) {
                    medicalRecordPicUploader = new MedicalRecordPicUploader(getContext());

                    medicalRecordPicUploader.setOnFinishUploadMedicalRecordPicCallBack(new onFinishUploadMedicalRecordPicCallBack() {

                        @Override
                        public void onFinishUpload(MedicalRecordPicEntity medicalRecordPicEntity) {
                            //添加View之前先判断uploadPicList没数据，没数据则会有一个默认的View，要先清除掉
                            if (uploadPicList == null || (uploadPicList != null && uploadPicList.size() == 0)) {
                                removeUploadRecordImage(null, 0);
                            }

                            uploadPicList.add(0, medicalRecordPicEntity); //添加View之前应该先把数据对象添加到list中

                            addUploadRecordImage(medicalRecordPicEntity, 0);
                        }
                    });
                }

                medicalRecordPicUploader.showPickerPicDialog();
                break;
            default:
                break;
        }
    }

    class UpdateRecordListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_ADD_INPUT_RECORD.equals(action)) {
                MedicalRecordEntity medicalRecordEntity = TpqApplication.getInstance().getAddMedicalRecordEntity();

                //添加View之前先判断inputRecordList没数据，没数据则会有一个默认的View，要先清除掉
                if (inputRecordList == null || (inputRecordList != null && inputRecordList.size() == 0)) {
                    removeInputRecordView(null, 0);
                }

                inputRecordList.add(0, medicalRecordEntity); //添加View之前应该先把数据对象添加到list中

                addInputRecordImage(medicalRecordEntity, 0);

                TpqApplication.getInstance().setAddMedicalRecordEntity(null); //清空缓存
            } else if (ACTION_DELETE_INPUT_RECORD.equals(action)) {
                String deleteId = intent.getStringExtra(APIKey.COMMON_ID);
                if (Validator.isIdValid(deleteId) && inputRecordList != null && inputRecordList.size() > 0) {
                    for (int i = 0; i < inputRecordList.size(); i++) {
                        MedicalRecordEntity medicalRecordEntity = inputRecordList.get(i);
                        if (null != medicalRecordEntity) {
                            String medicalRecordId = medicalRecordEntity.getId();
                            if (Validator.isIdValid(medicalRecordId) && medicalRecordId.equals(deleteId)) {
                                inputRecordList.remove(i); //删除View之前应该先把list中对应的对象清除掉
                                removeInputRecordView(medicalRecordEntity, i);

                                if (inputRecordList.size() == 0) { //删除View，如果列表为空，则得添加一个默认无体检数据的View
                                    addInputRecordImage(null, 0);
                                }
                            }
                        }
                    }
                }
            } else if (ACTION_ADD_IMAGE_RECORD.equals(action)) {

            } else if (ACTION_DELETE_IMAGE_RECORD.equals(action)) {
                String deleteId = intent.getStringExtra(APIKey.COMMON_ID);
                if (Validator.isIdValid(deleteId) && uploadPicList != null && uploadPicList.size() > 0) {
                    for (int i = 0; i < uploadPicList.size(); i++) {
                        MedicalRecordPicEntity medicalRecordPicEntity = uploadPicList.get(i);
                        if (null != medicalRecordPicEntity) {
                            String medicalRecordPicId = medicalRecordPicEntity.getId();
                            if (Validator.isIdValid(medicalRecordPicId) && medicalRecordPicId.equals(deleteId)) {
                                uploadPicList.remove(i); //删除View之前应该先把list中对应的对象清除掉
                                removeUploadRecordImage(medicalRecordPicEntity, i);

                                if (uploadPicList.size() == 0) { //删除View，如果列表为空，则得添加一个默认无体检图片的View
                                    addUploadRecordImage(null, 0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        medicalRecordPicUploader.uploadPicture(requestCode, resultCode, data);
    }

}
