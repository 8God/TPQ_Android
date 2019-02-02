package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.base.BasePager;

public class CommonPagerAdapter extends PagerAdapter {

    private Context context;
    private List<BasePager> pagerList;

    public CommonPagerAdapter(Context context, List<BasePager> pagerList) {
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
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (null != pagerList) {
            if (0 <= position && position < pagerList.size()) {
                container.removeView(pagerList.get(position));
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (null != pagerList) {
            if (0 <= position && position < pagerList.size()) {
                View page = pagerList.get(position);
                if (null != page) {
                    container.addView(page);
                    return page;
                }
            }
        }
        return super.instantiateItem(container, position);
    }

}
