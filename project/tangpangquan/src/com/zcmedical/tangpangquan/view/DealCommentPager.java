package com.zcmedical.tangpangquan.view;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.ClipboardUtils;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.BbsReportActivity;
import com.zcmedical.tangpangquan.activity.PostCommentActivity;
import com.zcmedical.tangpangquan.activity.PostsDetailActivity;
import com.zcmedical.tangpangquan.entity.CommentEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class DealCommentPager extends BasePager implements OnClickListener {

    private boolean isMyComment = false;
    private String myUserId;
    private CommentEntity comment;

    private View layout;

    private OnItemSelectedListener onItemSelectedListener;

    public DealCommentPager(Context context, CommentEntity comment) {
        super(context);
        this.comment = comment;

        if (null != comment && TpqApplication.getInstance(getContext()).isLogon()) {
            myUserId = TpqApplication.getInstance(getContext()).getUserId();
            String commentUserId = null;
            UserEntity user = comment.getUser();
            if(null != user) {
                commentUserId = user.getId();
            }

            if (Validator.isIdValid(commentUserId) && Validator.isIdValid(myUserId) && commentUserId.equals(myUserId)) {
                isMyComment = true;
            }
        }

        initLayout();
    }

    private void initLayout() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.view_deal_comment, null);
        addView(layout);

        LinearLayout likeLayout = findView(layout, R.id.ll_comment_like);
        LinearLayout replyLayout = findView(layout, R.id.ll_comment_reply);
        LinearLayout copyLayout = findView(layout, R.id.ll_copy_comment);
        LinearLayout reportLayout = findView(layout, R.id.ll_report_comment);
        LinearLayout deleteLayout = findView(layout, R.id.ll_delete_comment);
        likeLayout.setOnClickListener(this);
        replyLayout.setOnClickListener(this);
        copyLayout.setOnClickListener(this);
        reportLayout.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);

        Log.i("cth", "isMyComment = " + isMyComment);
        Log.i("cth", "myUserId = " + myUserId);
        Log.i("cth", "commentId = " + comment.getId());

        if (isMyComment) {
            replyLayout.setVisibility(View.GONE);
            reportLayout.setVisibility(View.GONE);
        } else {
            deleteLayout.setVisibility(View.GONE);
        }

        checkCommentLike();
    }

    private void checkCommentLike() {
        ImageView likeImv = findView(layout, R.id.imv_comment_like);
        TextView likeTv = findView(layout, R.id.tv_comment_like);

        boolean isLike = TpqApplication.getInstance(getContext()).isPostsCommentLike(comment.getId());
        if (isLike) {
            likeImv.setEnabled(false);
            likeTv.setText(getContext().getString(R.string.has_like_comment));
        } else {
            likeImv.setEnabled(true);
            likeTv.setText(getContext().getString(R.string.like_comment));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_comment_like:
                commentLikeEvent();
                break;
            case R.id.ll_comment_reply:
                replyComment();
                break;
            case R.id.ll_copy_comment:
                ClipboardUtils.copy(getContext(), comment.getContent());
                showToast("复制成功");
                break;
            case R.id.ll_report_comment:
                Intent openReport = new Intent(getContext(), BbsReportActivity.class);
                openReport.putExtra(BbsReportActivity.KEY_REPORT_TYPE, BbsReportActivity.REPORT_COMMENT);
                openReport.putExtra(BbsReportActivity.KEY_REPORT_COMMENT_ID, comment.getId());
                openReport.putExtra(BbsReportActivity.KEY_REPORT_COMMENT_CONTENT, comment.getContent());
                getContext().startActivity(openReport);
                break;
            case R.id.ll_delete_comment:
                deleteComment();
                break;
            default:
                break;
        }
        if (null != onItemSelectedListener) {
            onItemSelectedListener.onHasSelected(v.getId());
        }
    }

    private void commentLikeEvent() {
        boolean isCommentLike = TpqApplication.getInstance(getContext()).isPostsCommentLike(comment.getId());
        CommonRequest commentLikeRequest = new CommonRequest();
        if (!isCommentLike) {
            commentLikeRequest.setRequestApiName(InterfaceConstant.API_THREAD_COMMENT_LIKE_CREATE);
            commentLikeRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COMMENT_LIKE_CREATE);
        } else {
            commentLikeRequest.setRequestApiName(InterfaceConstant.API_THREAD_COMMENT_LIKE_REMOVE);
            commentLikeRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COMMENT_LIKE_REMOVE);
        }
        commentLikeRequest.addRequestParam(APIKey.THREAD_COMMENT_ID, comment.getId());
        UserEntity user = comment.getUser();
        if (null != user) {
            commentLikeRequest.addRequestParam(APIKey.USER_ID, user.getId());
        }

        addRequestAsyncTask(commentLikeRequest);
    }

    private void deleteComment() {
        String userId = TpqApplication.getInstance(getContext()).getUserId();
        CommonRequest deleteCommentRequest = new CommonRequest();
        deleteCommentRequest.setRequestApiName(InterfaceConstant.API_THREAD_COMMENT_REMOVE);
        deleteCommentRequest.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COMMENT_REMOVE);
        deleteCommentRequest.addRequestParam(APIKey.USER_ID, userId);
        deleteCommentRequest.addRequestParam(APIKey.COMMON_ID, comment.getId());

        addRequestAsyncTask(deleteCommentRequest);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_LIKE_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                TpqApplication.getInstance(getContext()).setPostsCommentLike(comment.getId(), true);
                comment.setCommentLikeCount(comment.getCommentLikeCount() + 1);
                checkCommentLike();

                if (null != onItemSelectedListener) {
                    onItemSelectedListener.onLikeComment(comment, true);
                }
            } else {
                showToast(message);
            }
        } else if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_LIKE_REMOVE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                TpqApplication.getInstance(getContext()).setPostsCommentLike(comment.getId(), false);
                comment.setCommentLikeCount(comment.getCommentLikeCount() - 1);
                checkCommentLike();

                if (null != onItemSelectedListener) {
                    onItemSelectedListener.onLikeComment(comment, false);
                }
            } else {
                showToast(message);
            }
        } else if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_REMOVE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("删除成功");
                if (null != onItemSelectedListener) {
                    onItemSelectedListener.onDeleteComent(comment);
                }
            } else {
                showToast(message);
            }
        }
    }

    private void replyComment() {
        String postId = null;
        PostsEntity posts = comment.getPosts();
        if (null != posts) {
            postId = posts.getId();
        }
        Intent openComment = new Intent(getContext(), PostCommentActivity.class);
        openComment.putExtra(PostCommentActivity.COMMENT_TYPE, PostCommentActivity.COMMENT_TYPE_REPLY_COMMENT);
        if (Validator.isIdValid(postId)) {
            openComment.putExtra(PostCommentActivity.POST_ID, postId);
        }
        openComment.putExtra(PostCommentActivity.COMMENT_ID, comment.getId());
        openComment.putExtra(PostCommentActivity.COMMENT_CONTENT, comment.getContent());

        ((BaseActivity) getContext()).startActivityForResult(openComment, PostsDetailActivity.UPDATE_POST_LIST);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onLikeComment(CommentEntity comment, boolean isLike);

        void onDeleteComent(CommentEntity comment);

        void onHasSelected(int selectedItemId);
    }

}
