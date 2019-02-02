package com.zcmedical.tangpangquan.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.PostsEntity;

public class UserPostsAdapter extends CommonListAdapter<PostsEntity> {

    public UserPostsAdapter(Context context, List<PostsEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_user_posts);

        PostsEntity post = getItem(position);
        if (null != post) {
            bindItemView(holder, post);
        }

        return holder.getConvertView();
    }

    private void bindItemView(CommonViewHolder holder, PostsEntity post) {
        holder.setTextView(R.id.tv_posts_title, post.getTitle());
        holder.setTextView(R.id.tv_posts_content, post.getContent());
        String createdAt = post.getCreatedAt();
        try {
            Date createdAtDate = CommonConstant.serverTimeFormat.parse(createdAt);
            createdAt = CommonConstant.refreshTimeFormat.format(createdAtDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        holder.setTextView(R.id.tv_posts_title, createdAt);
        
    }
}
