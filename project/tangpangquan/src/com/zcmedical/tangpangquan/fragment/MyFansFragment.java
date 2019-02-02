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
import com.zcmedical.tangpangquan.activity.BbsUserInfoActivity;
import com.zcmedical.tangpangquan.adapter.CircleMembersAdapter;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class MyFansFragment extends BaseListFragment<CircleMembersAdapter, UserEntity> {

    @Override
    protected void init() {
        super.init();

        setNoDataTextTip("您暂无任何粉丝喔");
    }

    @Override
    protected void dealItemClick(int clickPosition, UserEntity user) {
        if (null != user) {
            TpqApplication.getInstance(getActivity()).setShowingBbsUserEntity(user);
            Intent openBbsUserInfo = new Intent(getActivity(), BbsUserInfoActivity.class);
            startActivity(openBbsUserInfo);
        }

    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        String userId = TpqApplication.getInstance().getUserId();

        CommonRequest fetchMyFansList = new CommonRequest();
        fetchMyFansList.setRequestApiName(InterfaceConstant.API_USER_FOLLOW_FETCH);
        fetchMyFansList.setRequestID(InterfaceConstant.REQUEST_ID_USER_FOLLOW_FETCH);
        fetchMyFansList.addRequestParam(APIKey.USER_ID, userId);

        addRequestAsyncTask(contentView, fetchMyFansList);

    }

    @Override
    protected void onResponse(int status, String message, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_USER_FOLLOW_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> tmpUserList = TypeUtil.getList(resultMap.get(APIKey.USER_FOLLOWERS));
                    if (null != tmpUserList && tmpUserList.size() > 0) {
                        List<UserEntity> userList = EntityUtils.getMyFansEntityList(tmpUserList);
                        if (null == dataList) {
                            dataList = new ArrayList<UserEntity>();
                            adapter = null;
                        }
                        dataList.addAll(userList);
                    }
                }

            } else {
                showToast(message);
            }
            showDataList();
        }
    }

    @Override
    protected CircleMembersAdapter initAdapter(Context context, List<UserEntity> dataList) {
        CircleMembersAdapter adapter = new CircleMembersAdapter(getActivity(), dataList);
        return adapter;
    }

}
