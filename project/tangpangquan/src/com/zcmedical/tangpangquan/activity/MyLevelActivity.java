package com.zcmedical.tangpangquan.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.utils.LevelUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class MyLevelActivity extends BaseActivity {

    private UserEntity user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_level);

        init();
    }

    private void init() {
        initData();
        initToolbar("我的等级");
        initUI();
    }

    private void initData() {
        user = TpqApplication.getInstance().getUser();
    }

    private void initUI() {
        if (null != user) {
            initUserInfo();
            initLevelProgressBar();
        }
    }

    private void initUserInfo() {
        setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);
        setTextView(R.id.tv_nickname, user.getNickname());
        setTextView(R.id.tv_my_level, "Lv." + user.getLevel());
    }

    private void initLevelProgressBar() {
        final ProgressBar myIntegralPb = findView(R.id.pb_my_integral);

        int level = user.getLevel();
        int nextLevel = level + 1;
        final int myIntegral = user.getIntegral();
        final int nextIntegral = LevelUtils.getNextLevelIntegral(getContext(), level);
        if (nextLevel >= 9) { //9级是最大等级
            level = 8;
            nextLevel = 9;
        }
        setTextView(R.id.tv_my_current_level, "Lv." + level);
        setTextView(R.id.tv_my_next_level, "Lv." + nextLevel);
        setTextView(R.id.tv_current_integral, myIntegral + "");
        setTextView(R.id.tv_next_integral, nextIntegral + "");

        myIntegralPb.setMax(nextIntegral);
        myIntegralPb.setProgress(myIntegral);

        //        int width = WindowUtils.getMeasureWidthOfView(myIntegralPb);
        //        Log.i("cth", "width = " + width);
        //        LinearLayout levelInfoLayout = findView(R.id.ll_level_info);
        //        levelInfoLayout.setX(width);

        myIntegralPb.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                myIntegralPb.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int width = myIntegralPb.getMeasuredWidth();
                Log.i("cth", "width = " + width);
                LinearLayout levelInfoLayout = findView(R.id.ll_level_info);
                width = myIntegral * width / nextIntegral;
                levelInfoLayout.setX(width);
            }
        });
    }
}
