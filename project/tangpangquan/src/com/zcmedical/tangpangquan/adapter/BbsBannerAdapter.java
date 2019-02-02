package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.tangpangquan.view.BbsBannerPager;

public class BbsBannerAdapter extends PagerAdapter {

    private Context context;
    private List<BbsBannerPager> pagers;

    public BbsBannerAdapter(Context context, List<BbsBannerPager> pagers) {
        super();
        this.context = context;
        this.pagers = pagers;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position % pagers.size();
        View pager = pagers.get(index);
        if (null != pager) {
            if (null != pager.getParent()) {
                ViewGroup parentView = (ViewGroup) pager.getParent();
                parentView.removeView(pager);
            }
            ((ViewPager) container).addView(pager);

            return pager;
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //        int index = position % pagers.size();
        //
        //        View pager = pagers.get(index);
        //        if (null != pager) {
        //            ((ViewPager) container).removeView(pager);
        //        }

    }

}
