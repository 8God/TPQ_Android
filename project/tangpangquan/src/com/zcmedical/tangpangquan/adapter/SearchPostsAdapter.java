package com.zcmedical.tangpangquan.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.utils.CalendarUtil;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class SearchPostsAdapter extends CommonListAdapter<PostsEntity> {

    public SearchPostsAdapter(Context context, List<PostsEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = null;

        PostsEntity post = getItem(position);
        if (null != post) {
            holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_posts);
            bindPostsItemView(holder, post);
        } else {
            holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_posts);
        }

        return holder.getConvertView();

    }

    private void bindPostsItemView(CommonViewHolder holder, PostsEntity post) {
        holder.setTextView(R.id.tv_posts_title, post.getTitle());
        holder.setTextView(R.id.tv_posts_view_count, CounterUtils.format(post.getViewsCount()));
        holder.setTextView(R.id.tv_posts_comment_count, CounterUtils.format(post.getCommentCount()));

        String createdAt = post.getCreatedAt();
        try {
            Date targetDate = CommonConstant.serverTimeFormat.parse(createdAt);
            createdAt = CalendarUtil.getPastTimeDistance(new Date(), targetDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.setTextView(R.id.tv_posts_date, createdAt);

        UserEntity user = post.getUser();
        if (null != user) {
            String nickName = user.getNickname();
            if (!TextUtils.isEmpty(nickName)) {
                holder.setTextView(R.id.tv_user_name, nickName);
            }
        }

        ImageView essenceImv = holder.getView(R.id.imv_essence_icon);
        ImageView hotImv = holder.getView(R.id.imv_hot_icon);

        essenceImv.setVisibility(post.isEssence() ? View.VISIBLE : View.GONE);
        hotImv.setVisibility(post.isHot() ? View.VISIBLE : View.GONE);

    }

}
