package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.CircleEntity;

public class CircleAdapter extends CommonListAdapter<CircleEntity> {

    public CircleAdapter(Context context, List<CircleEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_circle);

        CircleEntity circle = getItem(position);
        if (null != circle) {
            holder.setTextView(R.id.tv_forum_title, circle.getTitle());
            holder.setTextView(R.id.tv_forum_description, circle.getDescription());
            holder.setTextView(R.id.tv_forum_posts_count, CounterUtils.format(circle.getPostsCount()));
            holder.setTextView(R.id.tv_forum_member_count, CounterUtils.format(circle.getUserCount()));

            holder.setImageView(R.id.cimv_forum_logo, circle.getForumLogoUrl());
        }

        return holder.getConvertView();
    }

}
