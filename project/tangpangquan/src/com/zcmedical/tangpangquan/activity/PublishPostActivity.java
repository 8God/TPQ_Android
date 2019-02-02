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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.widget.ImagePickerView;
import com.zcmedical.common.widget.ImagePickerView.OnImageChangedListener;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.CircleEntity;
import com.zcmedical.tangpangquan.view.SelectCirclePager;
import com.zcmedical.tangpangquan.view.SelectCirclePager.OnCircleSelectorListener;

public class PublishPostActivity extends BaseActivity implements OnClickListener {
    private static final int MAX_PIC_COUNT = 4;
    public static final String KEY_FORUM_ID = "KEY_FORUM_ID";

    private int picCount = 0; //选择图片张数
    private String circleId;
    private String circleTitle;

    private List<ImagePickerView> picViewsList;
    private LinearLayout picsLayout;
    private BasicDialog selectCircleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_publish_post);

        init();
    }

    private void init() {
        circleId = getIntent().getStringExtra(KEY_FORUM_ID);
        picsLayout = findView(R.id.ll_post_pics);
        picViewsList = new ArrayList<ImagePickerView>();

        initToolbar();
        initUI();
        addPostPicInLayout();
    }

    private void initUI() {
        if (TextUtils.isEmpty(circleId)) {

            final TextView selectCircleTv = findView(R.id.tv_select_circle);
            SelectCirclePager selectCirclePager = new SelectCirclePager(getContext());

            selectCircleDialog = new BasicDialog.Builder(getContext()).setTitle(getString(R.string.select_circle_dialog_title)).setContentView(selectCirclePager).create();

            selectCirclePager.setOnCircleSelectorListener(new OnCircleSelectorListener() {

                @Override
                public void onCircleSelected(CircleEntity selectCircle) {
                    if (null != selectCircle) {
                        circleId = selectCircle.getId();
                        circleTitle = selectCircle.getTitle();
                        selectCircleTv.setText(circleTitle);
                    }
                    if (null != selectCircleDialog && selectCircleDialog.isShowing()) {
                        selectCircleDialog.dismiss();
                    }
                }
            });

            selectCircleTv.setOnClickListener(this);
        } else {
            RelativeLayout selectCircleLayout = findView(R.id.rl_select_circle);
            selectCircleLayout.setVisibility(View.GONE);
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
            actionBar.setTitle(getString(R.string.title_publish_post_activity));
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_publish_post:
            boolean isLogon = TpqApplication.getInstance(getContext()).isLogon();
            if (isLogon) {
                publishPost();
            } else {
                showAlertDialog(getString(R.string.no_logon_tips));
            }
            break;
        case android.R.id.home:
            finish();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishPost() {
        EditText postTitleEdt = findView(R.id.edt_post_title);
        EditText postContentEdt = findView(R.id.edt_post_content);

        String title = postTitleEdt.getText().toString();
        String content = postContentEdt.getText().toString();

        boolean isTitleValid = checkTitleIsValid(title);
        if (!isTitleValid)
            return;
        boolean isContentValid = checkCotentIsValid(content);
        if (!isContentValid)
            return;
        boolean isForumIdValid = checkForumIdIsValid();
        if (!isForumIdValid)
            return;

        if (isTitleValid & isContentValid & isForumIdValid) {
            List<String> attachPicList = new ArrayList<String>();
            for (int i = 0; i < picViewsList.size(); i++) {
                ImagePickerView picView = picViewsList.get(i);
                if (null != picView) {
                    String imagePath = picView.getImagePath();
                    if (!TextUtils.isEmpty(imagePath)) {
                        attachPicList.add(imagePath);
                    }
                }
            }

            String userId = TpqApplication.getInstance(getContext()).getUserId();
            CommonRequest publishPost = new CommonRequest();
            publishPost.setRequestApiName(InterfaceConstant.API_THREAD_CREATE);
            publishPost.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_CREATE);
            publishPost.addRequestParam(APIKey.THREAD_TITLE, title);
            publishPost.addRequestParam(APIKey.THREAD_CONTENT, content);
            publishPost.addRequestParam(APIKey.USER_ID, userId);
            publishPost.addRequestParam(APIKey.FORUM_ID, circleId);
            if (null != attachPicList && attachPicList.size() > 0) {
                publishPost.addRequestParam(APIKey.PIC_THREAD_PIC, attachPicList);
            }

            addRequestAsyncTask(publishPost);

            showProgressDialog(getString(R.string.publish_post_submitting));
            checkCircleIsFollow();
        }
    }

    private boolean checkForumIdIsValid() {
        if (circleId == null) {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.no_forum_tips), getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != selectCircleDialog && !selectCircleDialog.isShowing()) {
                        selectCircleDialog.show();
                    }
                    dialog.dismiss();
                }
            });
        }
        return circleId != null;
    }

    private boolean checkTitleIsValid(String title) {
        boolean isValid = true;

        if (TextUtils.isEmpty(title)) {
            showAlertDialog(getString(R.string.no_title_tips));

            isValid = false;
        } else if (title.length() < CommonConstant.POST_TITLE_MIN_LENGTH) {
            showAlertDialog(getString(R.string.title_length_error));
            isValid = false;
        }

        return isValid;
    }

    private boolean checkCotentIsValid(String content) {
        boolean isValid = true;

        if (TextUtils.isEmpty(content)) {
            showAlertDialog(getString(R.string.no_content_tips));
            isValid = false;
        } else if (content.length() > CommonConstant.POST_CONTENT_MAX_LENGTH) {
            showAlertDialog(getString(R.string.content_length_too_long));
            isValid = false;
        }

        return isValid;
    }

    private void checkCircleIsFollow() {
        boolean isFolowed = TpqApplication.getInstance(getContext()).isCircleFollowed(circleId);
        if (!isFolowed) {
            List<CircleEntity> myFollowCircleList = TpqApplication.getInstance(getContext()).getMyFollowCircle();
            if (null != myFollowCircleList && myFollowCircleList.size() > 0) {
                for (int i = 0; i < myFollowCircleList.size(); i++) {
                    CircleEntity circle = myFollowCircleList.get(i);
                    if (null != circle) {
                        if (circle.getId().equals(circleId)) {
                            isFolowed = true;
                            break;
                        }
                    }
                }
                Log.i("cth", "checkCircleIsFollow:isFollowed = " + isFolowed);
                if (!isFolowed) {
                    followCircle();

                    DialogUtils.showAlertDialog(getContext(), getString(R.string.you_had_followed) + "“" + circleTitle + "”");
                }
            }
        }
    }

    private void followCircle() {
        String userId = TpqApplication.getInstance(getContext()).getUserId();
        CommonRequest followRequest = new CommonRequest();
        followRequest.setRequestApiName(InterfaceConstant.API_FORUM_USER_CREATE);
        followRequest.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_USER_CREATE);
        followRequest.addRequestParam(APIKey.USER_ID, userId);
        followRequest.addRequestParam(APIKey.FORUM_ID, circleId);

        addRequestAsyncTask(followRequest);

        TpqApplication.getInstance(getContext()).setCircleFollowed(circleId, true);
    }

    private void showAlertDialog(String message) {
        BasicDialog.Builder alertNoTitleBuilder = new BasicDialog.Builder(getContext());
        alertNoTitleBuilder.setMessage(message);
        alertNoTitleBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertNoTitleBuilder.create().show();
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast(getString(R.string.publish_successful));

                clearInput();
                
                finish();
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        }
    }

    private void clearInput() {
        EditText postTitleEdt = findView(R.id.edt_post_title);
        EditText postContentEdt = findView(R.id.edt_post_content);

        postTitleEdt.setText(null);
        postContentEdt.setText(null);

        for (int i = picViewsList.size() - 1; i >= 0; i--) {
            ImagePickerView picView = picViewsList.get(i);

            if (null != picView) {
                picView.clearImageView();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != picViewsList && picViewsList.size() > 0) {

            picViewsList.get(picViewsList.size() - 1).dealPickPicData(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        final EditText postContentEdt = findView(R.id.edt_post_content);
        String content = postContentEdt.getText().toString();
        if (TextUtils.isEmpty(content)) {
            super.finish();
        } else {
            DialogUtils.showAlertDialog(getContext(), getString(R.string.cancel_publish_post_alert), getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    postContentEdt.setText("");
                    finish();
                }
            }, getString(R.string.no), null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_select_circle:
            if (null != selectCircleDialog && !selectCircleDialog.isShowing()) {
                selectCircleDialog.show();
            }
            break;

        default:
            break;
        }
    }
}
