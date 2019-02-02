package com.zcmedical.tangpangquan.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import com.zcmedical.common.base.BaseListFragment;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.activity.CircleActivity;
import com.zcmedical.tangpangquan.adapter.CircleAdapter;
import com.zcmedical.tangpangquan.entity.CircleEntity;

public class MyCircleListFragment extends BaseListFragment<CircleAdapter, CircleEntity> {

    @Override
    protected void init() {
        super.init();

        setNoDataTextTip("您暂无加入任何圈子喔");
        
    }

    @Override
    protected void dealItemClick(int clickPosition, CircleEntity circle) {
        if (null != circle) {
            TpqApplication.getInstance(getActivity()).setShowingCircleEntity(circle);
            Intent openCircle = new Intent(getActivity(), CircleActivity.class);
            getActivity().startActivity(openCircle);
        }
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        String userId = TpqApplication.getInstance().getUserId();

        CommonRequest fetchMyCircleList = new CommonRequest();
        fetchMyCircleList.setRequestApiName(InterfaceConstant.API_FORUM_USER_FETCH);
        fetchMyCircleList.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_USER_FETCH);
        fetchMyCircleList.addRequestParam(APIKey.USER_ID, userId);

        addRequestAsyncTask(contentView, fetchMyCircleList);

    }

    @Override
    protected void onResponse(int status, String message, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_FORUM_USER_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> tmpCircleList = TypeUtil.getList(resultMap.get(APIKey.FORUM_USERS));
                    if (null != tmpCircleList && tmpCircleList.size() > 0) {
                        List<CircleEntity> myFollowCircleList = EntityUtils.getMyFollowCircleList(tmpCircleList);
                        if (null == dataList) {
                            dataList = new ArrayList<CircleEntity>();
                            adapter = null;
                        }
                        dataList.addAll(myFollowCircleList);
                    }
                }

            } else {
                showToast(message);
            }
            showDataList();
        }
    }

    @Override
    protected CircleAdapter initAdapter(Context context, List<CircleEntity> dataList) {
        CircleAdapter adapter = new CircleAdapter(getActivity(), dataList);
        return adapter;
    }

}
