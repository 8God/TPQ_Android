package com.zcmedical.tangpangquan.view;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.PicEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class PostsDetailPager extends BasePager {

    private PostsEntity postsEntity;
    private View layout;

    public PostsDetailPager(Context context, PostsEntity postsEntity) {
        super(context);
        this.postsEntity = postsEntity;

        initLayout();
    }

    private void initLayout() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.headview_posts, null);
        //        layout = View.inflate(getContext(), R.layout.headview_posts, null);

        addView(layout);

        if (null != postsEntity) {
            String createdAt = postsEntity.getCreatedAt();
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
            setTextView(layout, R.id.tv_posts_title, postsEntity.getTitle());
            setTextView(layout, R.id.tv_posts_content, postsEntity.getContent());
            setTextView(layout, R.id.tv_posts_likes_count, postsEntity.getLikesCount() + "");
            setTextView(layout, R.id.tv_posts_date, createdAt);

            Log.i("cth", "postsEntity.getTitle() =" + postsEntity.getTitle());
            Log.i("cth", "postsEntity.getContent() =" + postsEntity.getContent());
            Log.i("cth", "postsEntity.getLikesCount() =" + postsEntity.getLikesCount());
            Log.i("cth", "createdAt =" + createdAt);
            UserEntity user = postsEntity.getUser();
            if (null != user) {
                setTextView(layout, R.id.tv_user_name, user.getNickname());
                setImageView(layout, R.id.cimv_user_head, user.getHeadPic());
            }

            initPostsPics();
        }

    }

    private void initPostsPics() {
        List<PicEntity> postsPicUrls = postsEntity.getPostsPicUrls();
        if (null != postsPicUrls && postsPicUrls.size() > 0) {
            LinearLayout postsPicsLl = findView(layout, R.id.ll_posts_pics);

            LinearLayout.LayoutParams imvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imvParams.topMargin = DensityUtil.dip2px(getContext(), 12);

            for (int i = 0; i < postsPicUrls.size(); i++) {
                ImageView imv = new ImageView(getContext());
                imv.setScaleType(ScaleType.FIT_XY);
                imv.setLayoutParams(imvParams);
                imv.setId(1000 + i);

                postsPicsLl.addView(imv, i);

                PicEntity pic = postsPicUrls.get(i);
                if (null != pic) {
                    setImageView(layout, 1000 + i, pic.getPicUrl());
                }
            }
        }

    }

}
