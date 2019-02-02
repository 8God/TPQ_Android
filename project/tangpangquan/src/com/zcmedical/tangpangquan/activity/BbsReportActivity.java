package com.zcmedical.tangpangquan.activity;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.tangpangquan.R;

public class BbsReportActivity extends BaseActivity {
    public static final String KEY_REPORT_TYPE = "KEY_REPORT_TYPE";

    public static final String KEY_REPORT_POST_ID = "KEY_REPORT_POST_ID";
    public static final String KEY_REPORT_POST_TITLE = "KEY_REPORT_POST_TITLE";

    public static final String KEY_REPORT_COMMENT_ID = "KEY_REPORT_COMMENT_ID";
    public static final String KEY_REPORT_COMMENT_CONTENT = "KEY_REPORT_COMMENT_CONTENT";

    public static final int REPORT_POST = 1;
    public static final int REPORT_COMMENT = 2;

    private int reportType = REPORT_POST;

    private String postId;
    private String postTitle;

    private String commentId;
    private String commentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_report);

        init();
    }

    private void init() {
        initData();
        initToolbar();
        initUI();
    }

    private void initData() {
        Intent intent = getIntent();
        reportType = intent.getIntExtra(KEY_REPORT_TYPE, REPORT_POST);
        switch (reportType) {
        case REPORT_POST:
            postId = getIntent().getStringExtra(KEY_REPORT_POST_ID);
            postTitle = getIntent().getStringExtra(KEY_REPORT_POST_TITLE);
            break;
        case REPORT_COMMENT:
            commentId = getIntent().getStringExtra(KEY_REPORT_COMMENT_ID);
            commentContent = getIntent().getStringExtra(KEY_REPORT_COMMENT_CONTENT);
            break;
        default:
            break;
        }

    }

    private void initToolbar() {
        Toolbar toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setTitle(getString(R.string.bbs_report_title));
        }
    }

    private void initUI() {
        TextView reportPostTitleTv = findView(R.id.tv_report_circle_title);
        switch (reportType) {
        case REPORT_POST:
            reportPostTitleTv.setText(postTitle);
            break;
        case REPORT_COMMENT:
            reportPostTitleTv.setText(commentContent);
            break;
        default:
            break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bbs_report, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_bbs_report:
            reportPost();
            break;
        case android.R.id.home:
            finish();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_REPORT_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("提交成功，感谢您的举报");

                finish();
            } else {
                showToast(message);
            }

            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_REPORT_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("提交成功，感谢您的举报");

                finish();
            } else {
                showToast(message);
            }

            dimissProgressDialog();
        }
    }

    private void reportPost() {
        RadioGroup reportTypeRdoGrp = findView(R.id.rdogrp_report_type);
        int checkId = reportTypeRdoGrp.getCheckedRadioButtonId();

        int reportReason = 0;
        switch (checkId) {
        case R.id.rdobtn_rubbish_ad:
            reportReason = 0;
            break;
        case R.id.rdobtn_attack:
            reportReason = 1;
            break;
        case R.id.rdobtn_adult:
            reportReason = 2;
            break;
        case R.id.rdobtn_illegal:
            reportReason = 3;
            break;
        case R.id.rdobtn_other:
            reportReason = 4;
            break;
        default:
            break;
        }

        String userId = TpqApplication.getInstance(getContext()).getUserId();
        CommonRequest reportRequest = new CommonRequest();

        switch (reportType) {
        case REPORT_POST:
            reportRequest.setRequestApiName(InterfaceConstant.API_THREAD_REPORT_CREATE);
            reportRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_REPORT_CREATE);
            reportRequest.addRequestParam(APIKey.THREAD_ID, postId);
            break;
        case REPORT_COMMENT:
            reportRequest.setRequestApiName(InterfaceConstant.API_THREAD_COMMENT_REPORT_CREATE);
            reportRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COMMENT_REPORT_CREATE);
            reportRequest.addRequestParam(APIKey.THREAD_COMMENT_ID, commentId);
            break;

        default:
            break;
        }
        
        reportRequest.addRequestParam(APIKey.USER_ID, userId);
        reportRequest.addRequestParam(APIKey.THREAD_REPORT_TYPE, reportReason);

        addRequestAsyncTask(reportRequest);

        showProgressDialog(getString(R.string.report_request_submitting));
    }

}
