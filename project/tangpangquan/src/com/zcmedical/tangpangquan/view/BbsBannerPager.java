package com.zcmedical.tangpangquan.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.component.SimpleWebViewActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.BannerEntity;

public class BbsBannerPager extends BasePager implements OnClickListener {

    private BannerEntity bannerEntity;

    public BbsBannerPager(Context context, BannerEntity bannerEntity) {
        super(context);
        this.bannerEntity = bannerEntity;

        setDefaultImage(R.drawable.loading_bg);

        initLayout();
    }

    private void initLayout() {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.pager_bbs_banner, null);

        setImageView(layout, R.id.imv_banner_bg, bannerEntity.getBannerBgUrl());
        setViewClickListener(layout, R.id.imv_banner_bg, this);

        addView(layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_banner_bg:
                if (null != bannerEntity) {
                    if (!TextUtils.isEmpty(bannerEntity.getContentUrl())) {
                        Intent openContent = new Intent(getContext(), SimpleWebViewActivity.class);
                        openContent.putExtra(SimpleWebViewActivity.WEB_URL_KEY, bannerEntity.getContentUrl());
                        openContent.putExtra(SimpleWebViewActivity.ACTION_BAR_TITLE_KEY, bannerEntity.getBannerTitle());
                        getContext().startActivity(openContent);
                    }
                }
                break;

            default:
                break;
        }
    }

}
