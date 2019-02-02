package com.zcmedical.tangpangquan.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.CircleEntity;
import com.zcmedical.tangpangquan.entity.PostCollectionEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class MyPostsCollectionAdapter extends CommonListAdapter<PostCollectionEntity> {

    private boolean isDeleteMode = false;

    public MyPostsCollectionAdapter(Context context, List<PostCollectionEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_my_post_collection);

        PostCollectionEntity postCollection = getItem(position);
        if (null != postCollection) {
            initItemView(holder, postCollection);
        }

        return holder.getConvertView();
    }

    private void initItemView(final CommonViewHolder holder, final PostCollectionEntity postCollection) {
        PostsEntity post = postCollection.getPost();

        if (null != post) {
            CircleEntity circle = post.getCircle();
            if (null != circle) {
                holder.setImageView(R.id.cimv_user_head, circle.getForumLogoUrl(), R.drawable.signin_local_gallry);
            }
            holder.setTextView(R.id.tv_post_title, post.getTitle());

            UserEntity user = post.getUser();
            if (null != user) {
                holder.setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_nickname, user.getNickname());
            }
        }

        String createdAt = postCollection.getCreatedAt();
        try {
            Date createdDate = CommonConstant.serverTimeFormat.parse(createdAt);
            createdAt = CommonConstant.refreshTimeFormat.format(createdDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.setTextView(R.id.tv_created_at, createdAt);

        //判断是否是删除模式
        if (isDeleteMode) {
            holder.getView(R.id.cimv_user_head).setVisibility(View.GONE);
            holder.getView(R.id.cb_delete).setVisibility(View.VISIBLE);

            CheckBox deleteCb = holder.getView(R.id.cb_delete);
            deleteCb.setChecked(postCollection.isSelectToDelete());
            deleteCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    postCollection.setSelectToDelete(isChecked);
                }
            });
        } else {
            holder.getView(R.id.cimv_user_head).setVisibility(View.VISIBLE);
            holder.getView(R.id.cb_delete).setVisibility(View.GONE);
        }
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
    }

}
