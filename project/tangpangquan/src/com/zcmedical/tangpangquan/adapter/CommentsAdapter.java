package com.zcmedical.tangpangquan.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonAsyncConnector;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.network.IConnectorToRenderListener;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.OpenFileUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.BbsUserInfoActivity;
import com.zcmedical.tangpangquan.entity.CommentEntity;
import com.zcmedical.tangpangquan.entity.PicEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class CommentsAdapter extends CommonListAdapter<CommentEntity> {

    public CommentsAdapter(Context context, List<CommentEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_comment);

        CommentEntity comment = getItem(position);
        if (null != comment) {
            String createdAt = comment.getCreatedAt();
            if (null != createdAt) {
                Date createdDate = null;
                try {
                    createdDate = CommonConstant.serverTimeFormat.parse(createdAt);
                } catch (ParseException e) {
                    e.printStackTrace();
                    createdDate = new Date();
                }
                createdAt = CommonConstant.refreshTimeFormat.format(createdDate);
            }

            holder.setTextView(R.id.tv_comment_content, comment.getContent());
            holder.setTextView(R.id.tv_comment_date, createdAt);

            final UserEntity user = comment.getUser();
            if (null != user) {
                holder.setTextView(R.id.tv_user_name, user.getNickname());
                holder.setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);

                holder.getView(R.id.cimv_user_head).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TpqApplication.getInstance(context).setShowingBbsUserEntity(user);
                        Intent openBbsUserInfo = new Intent(context, BbsUserInfoActivity.class);
                        context.startActivity(openBbsUserInfo);
                    }
                });
            }

            addListener(holder, comment);
            initParentComment(holder, comment);
            initCommentPics(holder, comment);
        }

        return holder.getConvertView();
    }

    private void initParentComment(CommonViewHolder holder, CommentEntity comment) {
        CommentEntity parentComment = comment.getParentComment();
        LinearLayout parentCommentLl = holder.getView(R.id.rl_parent_comment);
        if (null != parentComment) {
            parentCommentLl.setVisibility(View.VISIBLE);

            UserEntity user = parentComment.getUser();
            if (null != user) {
                holder.setTextView(R.id.tv_parent_comment_user_name, user.getNickname());
            }
            holder.setTextView(R.id.tv_parent_comment_content, parentComment.getContent());

            String createdAt = parentComment.getCreatedAt();
            try {
                Date createdAtDate = CommonConstant.serverTimeFormat.parse(createdAt);
                createdAt = CommonConstant.refreshTimeFormat.format(createdAtDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.setTextView(R.id.tv_parent_comment_date, createdAt);

        } else {
            parentCommentLl.setVisibility(View.GONE);
        }
    }

    private void addListener(final CommonViewHolder holder, final CommentEntity comment) {
        initLikesButton(holder, comment);

        ImageButton likeImvBtn = holder.getView(R.id.imvbtn_likes);
        likeImvBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                commentLikeEvent(holder, comment);
            }
        });
    }

    private void commentLikeEvent(final CommonViewHolder holder, final CommentEntity comment) {
        boolean isCommentLike = TpqApplication.getInstance(context).isPostsCommentLike(comment.getId());
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

        final String requestID = commentLikeRequest.getRequestID();
        CommonAsyncConnector connector = new CommonAsyncConnector(context);
        connector.setToRenderListener(new IConnectorToRenderListener() {

            @Override
            public void toRender(Map<String, Object> result) {
                if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_LIKE_CREATE.equals(requestID)) {
                    int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                    String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");

                    if (APIKey.STATUS_SUCCESSFUL == status) {
                        TpqApplication.getInstance(context).setPostsCommentLike(comment.getId(), true);
                        comment.setCommentLikeCount(comment.getCommentLikeCount() + 1);
                        initLikesButton(holder, comment);
                    } else {
                        showToast(message);
                    }
                } else if (InterfaceConstant.REQUEST_ID_THREAD_COMMENT_LIKE_REMOVE.equals(requestID)) {
                    int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                    String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");

                    if (APIKey.STATUS_SUCCESSFUL == status) {
                        TpqApplication.getInstance(context).setPostsCommentLike(comment.getId(), false);
                        comment.setCommentLikeCount(comment.getCommentLikeCount() - 1);
                        initLikesButton(holder, comment);
                    } else {
                        showToast(message);
                    }
                }

            }
        });

        connector.execute(commentLikeRequest);
    }

    private void initLikesButton(CommonViewHolder holder, CommentEntity comment) {
        ImageButton likeImvBtn = holder.getView(R.id.imvbtn_likes);
        boolean isCommentLike = TpqApplication.getInstance(context).isPostsCommentLike(comment.getId());
        if (!isCommentLike) {
            likeImvBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.bbs_btn_like));
        } else {
            likeImvBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.bbs_btn_like_pre));
        }
        holder.setTextView(R.id.tv_comment_likes_count, CounterUtils.format(comment.getCommentLikeCount()));
    }

    private void initCommentPics(CommonViewHolder holder, CommentEntity comment) {
        List<PicEntity> commentPicUrls = comment.getCommentPics();
        LinearLayout commentPicsLl = holder.getView(R.id.ll_comment_pics);
        if (null != commentPicUrls && commentPicUrls.size() > 0) {
            commentPicsLl.setVisibility(View.VISIBLE);
            commentPicsLl.removeAllViews();
            //            LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(context, 120), DensityUtil.dip2px(context, 120));
            imvParams.topMargin = DensityUtil.dip2px(context, 12);

            for (int i = 0; i < commentPicUrls.size(); i++) {
                ImageView imv = new ImageView(context);
                imv.setScaleType(ScaleType.CENTER_CROP);
                imv.setLayoutParams(imvParams);
                //                imv.setId(2000 + i);

                commentPicsLl.addView(imv, i);

                final PicEntity pic = commentPicUrls.get(i);
                if (null != pic) {
                    Log.i("cth", " holder.setImageView id = " + imv.getId() + ",pic.getPicUrl() = " + pic.getPicUrl());
                    //                    holder.setImageView(imv.getId(), pic.getPicUrl(), R.drawable.loading_bg);
                    holder.setImageView(imv, pic.getPicUrl(), R.drawable.loading_bg, false, null);
                }

                imv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        OpenFileUtil.openFile(context, pic.getPicUrl());
                    }
                });
            }
        } else {
            commentPicsLl.setVisibility(View.GONE);
        }

    }

}
