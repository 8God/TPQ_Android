package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class CircleMembersAdapter extends CommonListAdapter<UserEntity> {

    public CircleMembersAdapter(Context context, List<UserEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_circle_member);

        UserEntity user = getItem(position);
        if (null != user) {
            bindView(holder, user);
        }

        return holder.getConvertView();
    }

    private void bindView(CommonViewHolder holder, UserEntity user) {
        holder.setTextView(R.id.tv_user_name, user.getNickname());
        holder.setTextView(R.id.tv_user_level, "Lv." + user.getLevel());
        holder.setTextView(R.id.tv_user_posts_count, CounterUtils.format(user.getPostsCount()) + " 发布");
        holder.setTextView(R.id.tv_user_fans_count, CounterUtils.format(user.getFansCount()) + " 粉丝");

        holder.setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);
    }

}
