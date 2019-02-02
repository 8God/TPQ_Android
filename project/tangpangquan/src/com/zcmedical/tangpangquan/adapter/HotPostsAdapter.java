package com.zcmedical.tangpangquan.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.utils.CalendarUtil;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.BbsUserInfoActivity;
import com.zcmedical.tangpangquan.entity.PicEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class HotPostsAdapter extends CommonListAdapter<PostsEntity> {

    private int[] imvArray = new int[] { R.id.imv_posts_pic1, R.id.imv_posts_pic2, R.id.imv_posts_pic3 };

    public HotPostsAdapter(Context context, List<PostsEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_hot_posts);

        PostsEntity posts = getItem(position);
        if (null != posts) {
            final UserEntity user = posts.getUser();
            holder.setTextView(R.id.tv_posts_title, posts.getTitle());
            holder.setTextView(R.id.tv_posts_content, posts.getContent());
            holder.setTextView(R.id.tv_view_count, CounterUtils.format(posts.getViewsCount()));
            holder.setTextView(R.id.tv_comment_count, CounterUtils.format(posts.getCommentCount()));

            String createdAt = posts.getCreatedAt();
            try {
                Date targetDate = CommonConstant.serverTimeFormat.parse(createdAt);
                createdAt = CalendarUtil.getPastTimeDistance(new Date(), targetDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.setTextView(R.id.tv_posts_date, createdAt);

            setPostPics(holder, posts);
            if (user != null) {
                holder.setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);
                holder.setTextView(R.id.tv_user_name, user.getNickname());

                holder.getView(R.id.cimv_user_head).setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        TpqApplication.getInstance(context).setShowingBbsUserEntity(user);
                        Intent openBbsUserInfo = new Intent(context, BbsUserInfoActivity.class);
                        context.startActivity(openBbsUserInfo);
                    }
                });
            }
        }

        return holder.getConvertView();
    }

    /**
     * 判断该帖子是否有照片，有最多显示三张，隐藏内容摘要，没有显示内容摘要
     * 
     * @param holder
     * @param posts
     */
    private void setPostPics(CommonViewHolder holder, PostsEntity posts) {
        List<PicEntity> picUrls = posts.getPostsPicUrls();
        LinearLayout picsLayout = holder.getView(R.id.ll_posts_pics);
        TextView postsContentTv = holder.getView(R.id.tv_posts_content);

        if (null != picUrls && picUrls.size() > 0) {
            picsLayout.setVisibility(View.VISIBLE);
            postsContentTv.setVisibility(View.GONE);

            picUrls = picUrls.size() > 3 ? picUrls.subList(0, 2) : picUrls;

            for (int show = 0, i = 0; i < 3; i++) {
                ImageView imv_pic = holder.getView(imvArray[i]);
                if (show < picUrls.size()) {
                    imv_pic.setVisibility(View.VISIBLE);
                    PicEntity pic = picUrls.get(i);
                    if (null != pic) {
                        holder.setImageView(imvArray[i], pic.getPicUrl(), R.drawable.loading_bg);
                    }
                } else {
                    imv_pic.setVisibility(View.GONE);
                }
                show++;
            }
        } else {
            picsLayout.setVisibility(View.GONE);
            postsContentTv.setVisibility(View.VISIBLE);
        }
    }
}
