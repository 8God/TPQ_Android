package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GuidePagerAdapter extends PagerAdapter {

    private Context context;
    private List<View> pagerList;

    public GuidePagerAdapter(Context context, List<View> pagerList) {
        super();
        this.context = context;
        this.pagerList = pagerList;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != pagerList) {
            count = pagerList.size();
        }
        return count;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (null != pagerList && position < pagerList.size()) {
            View pager = pagerList.get(position);
            if (null != pager) {
                container.addView(pager);

                return pager;
            }
        }

        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (null != pagerList && position < pagerList.size()) {
            View pager = pagerList.get(position);
            if (null != pager) {
                container.removeView(pager);
            }
        }
    }

}
