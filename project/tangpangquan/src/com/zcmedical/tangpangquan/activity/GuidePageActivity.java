package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.GuidePagerAdapter;

public class GuidePageActivity extends BaseActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide_page);

        initUI();
    }

    private void initUI() {
        ViewPager guidePageVp = findView(R.id.vp_guide);

        View page1 = LayoutInflater.from(getContext()).inflate(R.layout.view_guide_page1, null);
        View page2 = LayoutInflater.from(getContext()).inflate(R.layout.view_guide_page2, null);
        View page3 = LayoutInflater.from(getContext()).inflate(R.layout.view_guide_page3, null);
        View page4 = LayoutInflater.from(getContext()).inflate(R.layout.view_guide_page4, null);

        List<View> pagerList = new ArrayList<View>();
        pagerList.add(page1);
        pagerList.add(page2);
        pagerList.add(page3);
        pagerList.add(page4);

        GuidePagerAdapter guidePagerAdapter = new GuidePagerAdapter(getContext(), pagerList);
        guidePageVp.setAdapter(guidePagerAdapter);

        findView(page4, R.id.btn_start_logon).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_logon:
                startActivity(new Intent(getContext(), LogonActivity.class));

                finish();
                break;

            default:
                break;
        }
    }
}
