package com.zcmedical.tangpangquan.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.zcmedical.common.base.BaseListFragment;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.PlanDetailActivity;
import com.zcmedical.tangpangquan.adapter.PlanListAdapter;
import com.zcmedical.tangpangquan.entity.PlanEntity;

public class PlanListFragment extends BaseListFragment<PlanListAdapter, PlanEntity> {

    @Override
    protected void init() {
        super.init();

        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.headview_plan_list, null);
        listView.addHeaderView(headView);
        //        listView.setDividerHeight(1);
    }

    @Override
    protected void dealItemClick(int clickPosition, PlanEntity entity) {
        TpqApplication.getInstance().setShowingPlanEntity(entity);
        Intent openPlanDetail = new Intent(getActivity(), PlanDetailActivity.class);
        startActivity(openPlanDetail);
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        CommonRequest fetchPlanList = new CommonRequest();
        fetchPlanList.setRequestApiName(InterfaceConstant.API_PLAN_FETCH);
        fetchPlanList.setRequestID(InterfaceConstant.REQUEST_ID_PLAN_FETCH);
        fetchPlanList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchPlanList.addRequestParam(APIKey.COMMON_PAGE_SIZE, page_sizes);

        addRequestAsyncTask(contentView, fetchPlanList);
    }

    @Override
    protected void onResponse(int status, String message, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_PLAN_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> rawPlanList = TypeUtil.getList(resultMap.get(APIKey.PLANS));
                    if (null != rawPlanList && rawPlanList.size() > 0) {
                        List<PlanEntity> planList = EntityUtils.getPlanEntityList(rawPlanList);
                        if (null == dataList) {
                            dataList = new ArrayList<PlanEntity>();
                            adapter = null;
                        }
                        dataList.addAll(planList);
                    }
                }

            } else {
                showToast(message);
            }
            showDataList();
        }
    }

    @Override
    protected PlanListAdapter initAdapter(Context context, List<PlanEntity> dataList) {
        PlanListAdapter planListAdapter = new PlanListAdapter(getActivity(), dataList);
        return planListAdapter;
    }

}
