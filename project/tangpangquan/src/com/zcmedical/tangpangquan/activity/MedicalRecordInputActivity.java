package com.zcmedical.tangpangquan.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.MedicalRecordEntity;

public class MedicalRecordInputActivity extends BaseActivity implements OnClickListener {

    public static final String KEY_IS_INPUT_MODE = "KEY_IS_INPUT_MODE";

    //输入数据的EditText列表
    private final int[] edtIdArray = new int[] { R.id.edt_bxb, R.id.edt_lbxb, R.id.edt_hxb, R.id.edt_xhdb, R.id.edt_qbdb, R.id.edt_ztdb, R.id.edt_kfxt, R.id.edt_ch2xsxt, R.id.edt_thxhdb, R.id.edt_kfyds, R.id.edt_kfct, R.id.edt_ch2xsyds, R.id.edt_ch2xsct, R.id.edt_bdb, R.id.edt_gysz, R.id.edt_dgc, R.id.edt_dmdzdb, R.id.edt_gmdzdb, R.id.edt_ns, R.id.edt_gbzam, R.id.edt_gczam, R.id.edt_gaxztm, R.id.edt_nsd, R.id.edt_jg, R.id.edt_nwlbdb };
    //显示结果的TextView列表
    private final int[] tvIdArray = new int[] { R.id.tv_bxb_analysis_result, R.id.tv_lbxb_analysis_result, R.id.tv_hxb_analysis_result, R.id.tv_xhdb_analysis_result, R.id.tv_qbdb_analysis_result, R.id.tv_ztdb_analysis_result, R.id.tv_kfxt_analysis_result, R.id.tv_ch2xsxt_analysis_result, R.id.tv_thxhdb_analysis_result, R.id.tv_kfyds_analysis_result, R.id.tv_kfct_analysis_result, R.id.tv_ch2xsyds_analysis_result, R.id.tv_ch2xsct_analysis_result, R.id.tv_bdb_analysis_result, R.id.tv_gysz_analysis_result, R.id.tv_dgc_analysis_result, R.id.tv_dmdzdb_analysis_result, R.id.tv_gmdzdb_analysis_result, R.id.tv_ns_analysis_result, R.id.tv_gbzam_analysis_result, R.id.tv_gczam_analysis_result, R.id.tv_gaxztm_analysis_result, R.id.tv_nsd_analysis_result, R.id.tv_jg_analysis_result, R.id.tv_nwlbdb_analysis_result };
    //各项指标最小值列表
    private float[] floors = new float[] { 3.5f, 1.1f, 3.8f, 115f, 0.17f, 2f, 3.9f, 3.9f, 3.1f, 2.6f, 0.37f, 0f, 0f, 40f, 0.44f, 3.1f, 0.0f, 0.83f, 155f, 0f, 0f, 0f, 1.8f, 44f, 0f };
    //各项指标最大值列表
    private float[] ceilings = new float[] { 9.5f, 3.2f, 5.1f, 150f, 0.42f, 3.6f, 6.0f, 7.7f, 6.1f, 24.9f, 1.47f, 0f, 0f, 55f, 1.65f, 5.7f, 3.36f, 1.98f, 357f, 50f, 40f, 60f, 7.1f, 97f, 24f };
    //接口字段列表
    private String[] apiKeys = new String[] { APIKey.MEDICAL_RECORD_LEUKOCYTE,//白细胞           
    APIKey.MEDICAL_RECORD_LEUKOMONOCYTE,//淋巴细胞         
    APIKey.MEDICAL_RECORD_ERYTHROCYTE,//红细胞           
    APIKey.MEDICAL_RECORD_HEMOGLOBIN,//血红蛋白         
    APIKey.MEDICAL_RECORD_PREALBUMIN,//前白蛋白         
    APIKey.MEDICAL_RECORD_SIDEROPHILIN,//转铁蛋白         
    APIKey.MEDICAL_RECORD_FASTING_BLOOD_GLUCOSE,//空腹血糖         
    APIKey.MEDICAL_RECORD_TWO_BLOOD_GLUCOSE,//餐后2小时血糖    
    APIKey.MEDICAL_RECORD_GLYCOSYLATED_HEMOGLOBIN,//糖化血红蛋白     
    APIKey.MEDICAL_RECORD_FASTING_INSULIN,//空腹胰岛素       
    APIKey.MEDICAL_RECORD_FASTING_C_PEPTIDE,//空腹C肽          
    APIKey.MEDICAL_RECORD_TWO_INSULIN,//餐后2小时胰岛素  
    APIKey.MEDICAL_RECORD_TWO_C_PEPTIDE,//餐后2小时C肽     
    APIKey.MEDICAL_RECORD_ALBUMIN,//白蛋白           
    APIKey.MEDICAL_RECORD_GLYCERIN_TRILAURATE,//甘油三酯         
    APIKey.MEDICAL_RECORD_CHOLESTEROL,//胆固醇           
    APIKey.MEDICAL_RECORD_LDL,//低密度脂蛋白     
    APIKey.MEDICAL_RECORD_HDL,//高密度脂蛋白     
    APIKey.MEDICAL_RECORD_URIC_ACID,//尿酸             
    APIKey.MEDICAL_RECORD_GPT,//谷丙转氨酶       
    APIKey.MEDICAL_RECORD_GOT,//谷草转氨酶       
    APIKey.MEDICAL_RECORD_GGT,//γ–谷氨酰转肽酶                 
    APIKey.MEDICAL_RECORD_USEA_NITROGEN,//尿素氮           
    APIKey.MEDICAL_RECORD_CREATININE,//肌酐             
    APIKey.MEDICAL_RECORD_URINE_TRACE_ALBUMIN };//尿微量白蛋白   

    private boolean isInputMode = true; // 是否是输入模式

    private List<InputDataEntity> inputDataEntityList;

    private MedicalRecordEntity medicalRecordEntity;

    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_body_check_record);

        init();
    }

    private void init() {
        initData();

        String title = "手动录入";
        if (!isInputMode) {
            title = "分析结果";
        }
        initToolbar(title);
        initUI();
    }

    private void initData() {
        medicalRecordEntity = TpqApplication.getInstance().getShowingMedicalRecordEntity();
        isInputMode = getIntent().getBooleanExtra(KEY_IS_INPUT_MODE, true);
    }

    private void initUI() {
        initDateUI();
        initInputDataEntity();
        if (!isInputMode) {
            initRecordInfo();
        }
    }

    private void initDateUI() {
        setViewClickListener(R.id.edt_operation_time, this);
        setViewClickListener(R.id.edt_record_time, this);
        setViewClickListener(R.id.rl_select_operation_time, this);
        setViewClickListener(R.id.rl_select_record_time, this);
    }

    private void initInputDataEntity() {
        inputDataEntityList = new ArrayList<MedicalRecordInputActivity.InputDataEntity>();
        for (int i = 0; i < edtIdArray.length; i++) {
            EditText inputEdt = findView(edtIdArray[i]);
            if (null != inputEdt) {
                InputDataEntity inputDataEntity = new InputDataEntity();
                inputDataEntity.setInputEdt(inputEdt);
                inputDataEntity.setFloor(floors[i]);
                inputDataEntity.setCeiling(ceilings[i]);
                inputDataEntity.setApiKey(apiKeys[i]);

                TextView analysisResultTv = findView(tvIdArray[i]);
                if (null != analysisResultTv) {
                    inputDataEntity.setAnalysisResultTv(analysisResultTv);
                }

                inputDataEntityList.add(inputDataEntity);
            }
        }
    }

    //显示数据
    private void initRecordInfo() {
        if (null != medicalRecordEntity) {
            //设置时间
            String recordTime = medicalRecordEntity.getCreatedAt();
            String operationTime = medicalRecordEntity.getOperationTime();

            try {
                Date recordDate = CommonConstant.serverTimeFormat.parse(recordTime);
                recordTime = CommonConstant.dateFormat.format(recordDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Date operationDate = CommonConstant.serverTimeFormat.parse(operationTime);
                operationTime = CommonConstant.dateFormat.format(operationDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            setEditText(R.id.edt_record_time, recordTime);
            setEditText(R.id.edt_operation_time, operationTime);

            //设置数据
            setEditText(R.id.edt_bxb, "" + medicalRecordEntity.getLeukocyte());
            setEditText(R.id.edt_lbxb, "" + medicalRecordEntity.getLeukomonocyte());
            setEditText(R.id.edt_hxb, "" + medicalRecordEntity.getErythrocyte());
            setEditText(R.id.edt_xhdb, "" + medicalRecordEntity.getHemoglobin());
            setEditText(R.id.edt_qbdb, "" + medicalRecordEntity.getPrealbumin());
            setEditText(R.id.edt_ztdb, "" + medicalRecordEntity.getSiderophilin());
            setEditText(R.id.edt_kfxt, "" + medicalRecordEntity.getFastingBloodGlucose());
            setEditText(R.id.edt_ch2xsxt, "" + medicalRecordEntity.getTwoBloodGlucose());
            setEditText(R.id.edt_thxhdb, "" + medicalRecordEntity.getGlycosylatedHemoglobin());
            setEditText(R.id.edt_kfyds, "" + medicalRecordEntity.getFastingInsulin());
            setEditText(R.id.edt_kfct, "" + medicalRecordEntity.getFastingCPeptide());
            setEditText(R.id.edt_ch2xsyds, "" + medicalRecordEntity.getTwoInsulin());
            setEditText(R.id.edt_ch2xsct, "" + medicalRecordEntity.getTwoCPeptide());
            setEditText(R.id.edt_bdb, "" + medicalRecordEntity.getAlbumin());
            setEditText(R.id.edt_gysz, "" + medicalRecordEntity.getGlycerinTrilaurate());
            setEditText(R.id.edt_dgc, "" + medicalRecordEntity.getCholesterol());
            setEditText(R.id.edt_dmdzdb, "" + medicalRecordEntity.getLdl());
            setEditText(R.id.edt_gmdzdb, "" + medicalRecordEntity.getHdl());
            setEditText(R.id.edt_ns, "" + medicalRecordEntity.getUricAcid());
            setEditText(R.id.edt_gbzam, "" + medicalRecordEntity.getGpt());
            setEditText(R.id.edt_gczam, "" + medicalRecordEntity.getGot());
            setEditText(R.id.edt_gaxztm, "" + medicalRecordEntity.getGgt());
            setEditText(R.id.edt_nsd, "" + medicalRecordEntity.getUseaNitrogen());
            setEditText(R.id.edt_jg, "" + medicalRecordEntity.getCreatinine());
            setEditText(R.id.edt_nwlbdb, "" + medicalRecordEntity.getUrineTraceAlbumin());

            analyzeBodyCheckRecord();
        } else { //当拿到的数据对象为空，视为非编辑模式
            isInputMode = true;
            initToolbar("手动录入");
        }
    }

    /**
     * 分析各个数据的结果
     */
    private void analyzeBodyCheckRecord() {
        if (null != inputDataEntityList && inputDataEntityList.size() > 0) {
            for (InputDataEntity input : inputDataEntityList) {
                if (null != input) {
                    EditText inputEdt = input.getInputEdt();
                    if (null != inputEdt) {
                        inputEdt.setEnabled(false);
                        String inputDataStr = inputEdt.getText().toString();
                        if (!TextUtils.isEmpty(inputDataStr)) { //已填写数据
                            float inputValue = Float.parseFloat(inputDataStr);
                            if (inputValue > 0) {
                                TextView analysisResultTv = input.getAnalysisResultTv();
                                if (null != analysisResultTv) {
                                    float floor = input.getFloor();
                                    float ceiling = input.getCeiling();
                                    if (ceiling > 0) {
                                        if (inputValue < floor) {
                                            analysisResultTv.setText(getString(R.string.title_bodycheck_quota_low));
                                            analysisResultTv.setTextColor(getResources().getColor(R.color.bodycheck_quota_low));
                                        } else if (inputValue > floor && inputValue < ceiling) {
                                            analysisResultTv.setText(getString(R.string.title_bodycheck_quota_normal));
                                            analysisResultTv.setTextColor(getResources().getColor(R.color.bodycheck_quota_normal));
                                        } else if (inputValue > ceiling) {
                                            analysisResultTv.setText(getString(R.string.title_bodycheck_quota_high));
                                            analysisResultTv.setTextColor(getResources().getColor(R.color.bodycheck_quota_high));
                                        }
                                        analysisResultTv.setVisibility(View.VISIBLE);
                                    } else {
                                        analysisResultTv.setVisibility(View.GONE);
                                    }
                                }
                            } else { //未填写数据
                                inputEdt.setText("未填写数据");
                                inputEdt.setTextColor(getResources().getColor(R.color.default_hint));
                            }
                        } else { //未填写数据
                            inputEdt.setText("未填写数据");
                            inputEdt.setTextColor(getResources().getColor(R.color.default_hint));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bodycheck_record, menu);

        menuItem = menu.getItem(0);

        if (null != menuItem && !isInputMode) {
            menuItem.setTitle("删除");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_bodycheck_record:
                if (isInputMode) {
                    saveRecord();
                } else {

                    DialogUtils.showAlertDialog(getContext(), "您确定删除这轮的体检数据么？", getString(R.string.yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRecord();

                            dialog.dismiss();
                        }
                    }, getString(R.string.no), null);

                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 提交数据
     */
    private void saveRecord() {
        final CommonRequest saveRecordRequest = new CommonRequest();
        String operationTime = getEditTextInput(R.id.edt_operation_time, "");
        String recordTime = getEditTextInput(R.id.edt_record_time, "");

        if (TextUtils.isEmpty(operationTime) || TextUtils.isEmpty(recordTime)) {
            DialogUtils.showAlertDialog(getContext(), "请选择时间");

            return;
        } else {
            try {
                Date operationTimeDate = CommonConstant.dateFormat.parse(operationTime);
                Date recordTimeDate = CommonConstant.dateFormat.parse(recordTime);

                operationTime = CommonConstant.serverTimeFormat.format(operationTimeDate);
                recordTime = CommonConstant.serverTimeFormat.format(recordTimeDate);

                saveRecordRequest.addRequestParam(APIKey.MEDICAL_RECORD_OPERATION_TIME, operationTime);
                saveRecordRequest.addRequestParam(APIKey.COMMON_CREATED_AT, recordTime);
            } catch (ParseException e) {
                e.printStackTrace();

                showToast("日期选择出错，请重新选择");

                return;
            }

        }

        String userId = TpqApplication.getInstance().getUserId();
        saveRecordRequest.addRequestParam(APIKey.USER_ID, userId);

        int inputDataCount = 0; //填写数据的数量
        saveRecordRequest.setRequestApiName(InterfaceConstant.API_MEDICAL_RECORD_CREATE);
        saveRecordRequest.setRequestID(InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_CREATE);
        //取出填写数据
        if (null != inputDataEntityList && inputDataEntityList.size() > 0) {
            for (InputDataEntity input : inputDataEntityList) {
                if (null != input) {
                    EditText edt = input.getInputEdt();
                    if (null != edt) {
                        String inputData = edt.getText() != null ? edt.getText().toString() : "";
                        if (!TextUtils.isEmpty(inputData)) {
                            saveRecordRequest.addRequestParam(input.getApiKey(), inputData);
                            inputDataCount++;
                        }
                    }
                }
            }
        }

        //判断所填数据完整性
        if (inputDataCount > 0) {
            if (inputDataCount < edtIdArray.length) {
                DialogUtils.showAlertDialog(getContext(), "您的数据并未录入完整，确定保存吗？保存后数据将不能修改。", getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addRequestAsyncTask(saveRecordRequest);
                        dialog.dismiss();

                        showProgressDialog("体检数据提交中");
                    }
                }, getString(R.string.no), null);
            } else if (inputDataCount == edtIdArray.length) {
                DialogUtils.showAlertDialog(getContext(), "您确定保存这次的体检数据吗？保存后数据将不能修改。", getString(R.string.yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addRequestAsyncTask(saveRecordRequest);
                        dialog.dismiss();

                        showProgressDialog("体检数据提交中");
                    }
                }, getString(R.string.no), null);
            }
        } else {
            DialogUtils.showAlertDialog(getContext(), "请填写体检数据");
        }
    }

    /**
     * 删除数据
     */
    private void deleteRecord() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest deleteRecordRequest = new CommonRequest();
        deleteRecordRequest.setRequestApiName(InterfaceConstant.API_MEDICAL_RECORD_REMOVE);
        deleteRecordRequest.setRequestID(InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_REMOVE);
        deleteRecordRequest.addRequestParam(APIKey.USER_ID, userId);
        deleteRecordRequest.addRequestParam(APIKey.COMMON_ID, medicalRecordEntity.getId());

        addRequestAsyncTask(deleteRecordRequest);

        showProgressDialog("删除中");
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                menuItem.setVisible(false);
                analyzeBodyCheckRecord();

                if (null != resultMap) {
                    Map<String, Object> medicalRecordMap = TypeUtil.getMap(resultMap.get(APIKey.MEDICAL_RECORD));
                    if (null != medicalRecordMap) {
                        MedicalRecordEntity medicalRecordEntity = EntityUtils.getMedicalRecordEntity(medicalRecordMap);

                        TpqApplication.getInstance().setAddMedicalRecordEntity(medicalRecordEntity);

                        Intent addInputRecord = new Intent();
                        addInputRecord.setAction(MedicalRecordActivity.ACTION_ADD_INPUT_RECORD);
                        sendBroadcast(addInputRecord);
                    }

                }

            }

            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_MEDICAL_RECORD_REMOVE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("删除成功");

                if (null != medicalRecordEntity) {
                    Intent deleteInputRecord = new Intent();
                    deleteInputRecord.setAction(MedicalRecordActivity.ACTION_DELETE_INPUT_RECORD);
                    deleteInputRecord.putExtra(APIKey.COMMON_ID, medicalRecordEntity.getId());
                    sendBroadcast(deleteInputRecord);
                }

                finish();
            } else {
                showToast(message);
            }

            dimissProgressDialog();
        }
    }

    /**
     * 输入数据实体，包含输入控件，分析结果控件，合理范围的最小值、最大值
     * 
     * @author cth
     *
     */
    class InputDataEntity {
        private EditText inputEdt;
        private TextView analysisResultTv;

        private float ceiling; //该指标合理范围最大值
        private float floor; //该指标合理范围最小值

        private String apiKey;

        public EditText getInputEdt() {
            return inputEdt;
        }

        public void setInputEdt(EditText inputEdt) {
            this.inputEdt = inputEdt;
        }

        public TextView getAnalysisResultTv() {
            return analysisResultTv;
        }

        public void setAnalysisResultTv(TextView analysisResultTv) {
            this.analysisResultTv = analysisResultTv;
        }

        /**
         * 获取该指标合理范围的最大值
         * 
         * @return
         */
        public float getCeiling() {
            return ceiling;
        }

        public void setCeiling(float ceiling) {
            this.ceiling = ceiling;
        }

        /**
         * 获取该指标合理范围的最小值
         * 
         * @return
         */
        public float getFloor() {
            return floor;
        }

        public void setFloor(float floor) {
            this.floor = floor;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TpqApplication.getInstance().setShowingMedicalRecordEntity(null); //防止数据污染
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edt_operation_time:
            case R.id.rl_select_operation_time:
                final EditText operationTimeEdt = findView(R.id.edt_operation_time);

                final Calendar oprTimeCalendar = Calendar.getInstance();
                int oprTimeYear = oprTimeCalendar.get(Calendar.YEAR);
                int oprTimeMonthOfYear = oprTimeCalendar.get(Calendar.MONTH);
                int oprTimeDayOfMonth = oprTimeCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog oprTimePickerDialog = new DatePickerDialog(getContext(), new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        oprTimeCalendar.set(year, monthOfYear, dayOfMonth);
                        Date selectDate = oprTimeCalendar.getTime();
                        String date = CommonConstant.dateFormat.format(selectDate);
                        operationTimeEdt.setText(date);
                    }
                }, oprTimeYear, oprTimeMonthOfYear, oprTimeDayOfMonth);

                oprTimePickerDialog.show();
                break;
            case R.id.edt_record_time:
            case R.id.rl_select_record_time:
                final EditText recordTimeEdt = findView(R.id.edt_record_time);

                final Calendar recordTimeCalendar = Calendar.getInstance();
                int recordTimeYear = recordTimeCalendar.get(Calendar.YEAR);
                int recordTimeMonthOfYear = recordTimeCalendar.get(Calendar.MONTH);
                int recordTimeDayOfMonth = recordTimeCalendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog recordTimePickerDialog = new DatePickerDialog(getContext(), new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        recordTimeCalendar.set(year, monthOfYear, dayOfMonth);
                        Date selectDate = recordTimeCalendar.getTime();
                        String date = CommonConstant.dateFormat.format(selectDate);
                        recordTimeEdt.setText(date);
                    }
                }, recordTimeYear, recordTimeMonthOfYear, recordTimeDayOfMonth);

                recordTimePickerDialog.show();
                break;
            default:
                break;
        }

    }

}
