package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zcmedical.common.base.BaseFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragmentList;

    public HomePagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public BaseFragment getItem(int position) {
        BaseFragment item = null;
        if (null != fragmentList && fragmentList.size() > 0 && position < fragmentList.size()) {
            item = fragmentList.get(position);
        } 
        return item;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (null != fragmentList) {
            count = fragmentList.size();
        }
        return count;
    }
}
