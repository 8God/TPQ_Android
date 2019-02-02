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

public class CirclePostsListAdapter extends CommonListAdapter<PostsEntity> {

    private static final int ITEM_TYPE_TOP_POSTS = 0;
    private static final int ITEM_TYPE_POSTS = 1;

    private int topPostsCount = 0;

    public CirclePostsListAdapter(Context context, List<PostsEntity> dataList) {
        super(context, dataList);

        getTopPostsCount();
    }

    private void getTopPostsCount() {
        for (int i = 0; i < dataList.size(); i++) {
            PostsEntity posts = dataList.get(i);
            if (null != posts && posts.isTop()) {
                topPostsCount++;
            } else {
                break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        int itemViewType = ITEM_TYPE_POSTS;

        if (topPostsCount > 0 && position < topPostsCount) {
            itemViewType = ITEM_TYPE_TOP_POSTS;
        }

        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        int viewTypeCount = 1;

        if (topPostsCount > 0) {
            viewTypeCount = 2;
        }

        return viewTypeCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = null;

        PostsEntity post = getItem(position);
        if (null != post) {
            switch (getItemViewType(position)) {
            case ITEM_TYPE_TOP_POSTS:
                holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_top_posts);
                bindTopPostsItemView(holder, post, position);
                break;
            case ITEM_TYPE_POSTS:
                holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_posts);
                bindPostsItemView(holder, post);
                break;

            default:
                break;
            }
        } else {
            holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_posts);
        }

        return holder.getConvertView();

    }

    private void bindTopPostsItemView(CommonViewHolder holder, PostsEntity post, int position) {
        View divider = holder.getView(R.id.line_top_posts_divider);
        if (position == topPostsCount - 1) {
            divider.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.GONE);
        }

        holder.setTextView(R.id.tv_posts_title, post.getTitle());

        ImageView essenceImv = holder.getView(R.id.imv_essence_icon);
        ImageView hotImv = holder.getView(R.id.imv_hot_icon);

        essenceImv.setVisibility(post.isEssence() ? View.VISIBLE : View.GONE);
        hotImv.setVisibility(post.isHot() ? View.VISIBLE : View.GONE);
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
