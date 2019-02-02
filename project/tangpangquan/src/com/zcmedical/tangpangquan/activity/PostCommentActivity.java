package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.widget.ImagePickerView;
import com.zcmedical.common.widget.ImagePickerView.OnImageChangedListener;
import com.zcmedical.tangpangquan.R;

public class PostCommentActivity extends BaseActivity {
    private static final int MAX_PIC_COUNT = 3;

    public static final String COMMENT_TYPE = "COMMENT_TYPE";
    public static final String POST_ID = "POST_ID";
    public static final String COMMENT_ID = "COMMENT_ID";
    public static final String COMMENT_CONTENT = "COMMENT_CONTENT";

    public static final int COMMENT_TYPE_REPLY = 0; //回复帖子
    public static final int COMMENT_TYPE_REPLY_COMMENT = 1; //回复评论
    private int commentType = 0;

    private String postId;
    private String commentId;

    private int picCount = 0; //选择图片张数
    private List<ImagePickerView> picViewsList;
    private LinearLayout picsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_reply);

        init();
    }

    private void init() {
        picsLayout = findView(R.id.ll_comment_pics);
        picViewsList = new ArrayList<ImagePickerView>();

        initData();
        initToolbar();
        initUI();
        addPostPicInLayout();
    }

    private void initData() {
        commentType = getIntent().getIntExtra(COMMENT_TYPE, COMMENT_TYPE_REPLY);
        postId = getIntent().getStringExtra(POST_ID);
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
            actionBar.setTitle(getString(R.string.title_post_comment_activity));
        }
    }

    private void initUI() {
        switch (commentType) {
        case COMMENT_TYPE_REPLY:
            initReplyUI();
            break;
        case COMMENT_TYPE_REPLY_COMMENT:
            initReplyCommentUI();
            break;
        default:
            break;
        }
    }

    /**
     * 初始化回复帖子UI
     */
    private void initReplyUI() {
        TextView replyCommentContentTv = findView(R.id.tv_reply_comment_content);
        replyCommentContentTv.setVisibility(View.GONE);

    }

    /**
     * 初始化回复评论UI
     */
    private void initReplyCommentUI() {
        TextView replyCommentContentTv = findView(R.id.tv_reply_comment_content);
        replyCommentContentTv.setVisibility(View.VISIBLE);

        commentId = getIntent().getStringExtra(COMMENT_ID);
        String commentContent = getIntent().getStringExtra(COMMENT_CONTENT);
        replyCommentContentTv.setText(commentContent);
    }

    private void checkCommentInput() {
        EditText commentEdt = findView(R.id.edt_comment_content);
        final String content = commentEdt.getText().toString().trim();
        final String userId = TpqApplication.getInstance(getContext()).getUserId();

        if (TextUtils.isEmpty(content)) {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.alert_no_comment_content));

            return;
        } else if (content.length() > CommonConstant.COMMENT_MAX_LENGTH) {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.comment_length_illegal));

            return;
        }
        publishComment(userId, content);
    }

    private void publishComment(String userId, String content) {
        List<String> attachPicList = new ArrayList<String>();
        for (int i = 0; i < picViewsList.size(); i++) {
            ImagePickerView picView = picViewsList.get(i);
            if (null != picView) {
                String imagePath = picView.getImagePath();
                if (!TextUtils.isEmpty(imagePath)) {
                    Log.i("cth", i + ",imagePath = " + imagePath);
                    attachPicList.add(imagePath);
                }
            }
        }

        CommonRequest publishComment = new CommonRequest();
        publishComment.setRequestApiName(InterfaceConstant.API_THREAD_COMMENT_CREATE);
        publishComment.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COMMENT_CREATE);
        publishComment.addRequestParam(APIKey.THREAD_CONTENT, content);
        publishComment.addRequestParam(APIKey.THREAD_ID, postId);
        publishComment.addRequestParam(APIKey.USER_ID, userId);
        if (null != attachPicList && attachPicList.size() > 0) {
            publishComment.addRequestParam(APIKey.THREADS_COMMENT_PIC, attachPicList);
        }

        if (commentType == COMMENT_TYPE_REPLY_COMMENT) {
            publishComment.addRequestParam(APIKey.THREAD_COMMENT_ID, commentId);
        }

        addRequestAsyncTask(publishComment);

        showProgressDialog(getString(R.string.publishing_comment));

    }

    private void addPostPicInLayout() {
        if (null != picViewsList && picViewsList.size() < MAX_PIC_COUNT) {
            final ImagePickerView pic = new ImagePickerView(getContext());
            pic.setOnImageChangedListener(new OnImageChangedListener() {

                @Override
                public void onDelImage() {
                    removePostPicFromLayout(pic);

                    if (picCount == MAX_PIC_COUNT) {
                        addPostPicInLayout();
                    }

                    picCount -= 1;
                }

                @Override
                public void onAddImage(String filePath) {
                    picCount += 1;

                    addPostPicInLayout();
                }

            });
            LinearLayout.LayoutParams picParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(getContext(), 68), DensityUtil.dip2px(getContext(), 68));
            picParams.rightMargin = DensityUtil.dip2px(getContext(), 20);
            pic.setLayoutParams(picParams);

            picsLayout.addView(pic);
            picViewsList.add(pic);

        }
    }

    private void removePostPicFromLayout(ImagePickerView pic) {
        if (null != picViewsList && picViewsList.size() > 1) {
            picsLayout.removeView(pic);
            picViewsList.remove(pic);

        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast(getString(R.string.publish_comment_successful));
                clearUserInput();
                setResult(RESULT_OK);

                finish();
            } else {
                showToast(message);
            }

            dimissProgressDialog();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish_comment, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.menu_item_publish_comment:
            if (TpqApplication.getInstance(getContext()).isLogon()) {
                checkCommentInput();
            } else {
                DialogUtils.showAlertDialog(getContext(), getString(R.string.no_logon_tips));
            }

            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearUserInput() {
        EditText contentEdt = findView(R.id.edt_comment_content);
        contentEdt.setText(null);
    }

    @Override
    public void finish() {
        final EditText contentEdt = findView(R.id.edt_comment_content);
        String content = contentEdt.getText().toString();
        if (TextUtils.isEmpty(content)) {
            super.finish();
        } else {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.cancel_comment_alert), getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    clearUserInput();
                    finish();
                }
            }, getString(R.string.no), null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != picViewsList && picViewsList.size() > 0) {
            picViewsList.get(picViewsList.size() - 1).dealPickPicData(requestCode, resultCode, data);
        }
    }
}
