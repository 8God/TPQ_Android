package com.zcmedical.tangpangquan.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class BbsUserInfoPager extends BasePager {
    private View contentView;
    private UserEntity user;

    public BbsUserInfoPager(Context context, UserEntity user) {
        super(context);
        this.user = user;

        initLayout();
    }

    private void initLayout() {
        contentView = LayoutInflater.from(getContext()).inflate(R.layout.view_bbs_user_info, null);

        addView(contentView);

        initUI();
    }

    private void initUI() {
        if (null != user) {
            String locale = user.getCity();
            if (TextUtils.isEmpty(locale)) {
                locale = getContext().getString(R.string.unknown);
            }
            String constellation = null;
            if (TextUtils.isEmpty(constellation)) {
                constellation = getContext().getString(R.string.unknown);
            }

            String marriageStatus = null;
            int maritalStatus = user.getMaritalStatus();
            switch (maritalStatus) {
            case APIKey.MARITAL_STATUS_UNMARRIED:
                marriageStatus = getContext().getString(R.string.unmarried);
                break;
            case APIKey.MARITAL_STATUS_MARRIED:
                marriageStatus = getContext().getString(R.string.married);
                break;
            default:
                marriageStatus = getContext().getString(R.string.unknown);
                break;
            }

            setTextView(contentView, R.id.tv_user_locale, locale);
            setTextView(contentView, R.id.tv_user_constellation, constellation);
            setTextView(contentView, R.id.tv_user_marriage_status, marriageStatus);
        }
    }
}
