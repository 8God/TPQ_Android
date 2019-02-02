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
import com.zcmedical.tangpangquan.activity.PostsDetailActivity;
import com.zcmedical.tangpangquan.adapter.MyPostsCollectionAdapter;
import com.zcmedical.tangpangquan.entity.PostCollectionEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;

public class MyPostsCollectionFragment extends BaseListFragment<MyPostsCollectionAdapter, PostCollectionEntity> {

    @Override
    protected void init() {
        super.init();

        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
        setNoDataTextTip("暂无收藏记录");
    }

    @Override
    protected void dealItemClick(int clickPosition, PostCollectionEntity entity) {
        if (null != entity) {
            //            if (adapter.isDeleteMode()) {
            //                boolean isSelectToDelete = entity.isSelectToDelete();
            //                entity.setSelectToDelete(!isSelectToDelete);
            //                adapter.notifyDataSetChanged();
            //            } else {
            PostsEntity post = entity.getPost();
            if (null != post) {
                TpqApplication.getInstance().setShowingPostsEntity(post);
                startActivity(new Intent(getActivity(), PostsDetailActivity.class));
            }
            //            }
        }
    }

    @Override
    protected void fetchDataList(int offset, int page_sizes) {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchPostCollection = new CommonRequest();
        fetchPostCollection.setRequestApiName(InterfaceConstant.API_THREAD_COLLECTION_FETCH);
        fetchPostCollection.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_COLLECTION_FETCH);
        fetchPostCollection.addRequestParam(APIKey.USER_ID, userId);
        fetchPostCollection.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchPostCollection.addRequestParam(APIKey.COMMON_PAGE_SIZE, Integer.MAX_VALUE);
        fetchPostCollection.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.COMMON_CREATED_AT);
        fetchPostCollection.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);

        addRequestAsyncTask(contentView, fetchPostCollection);

    }

    @Override
    protected void onResponse(int status, String message, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        this.toBeContinued = 0;
        if (InterfaceConstant.REQUEST_ID_THREAD_COLLECTION_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> tmpPostCollectionMapList = TypeUtil.getList(resultMap.get(APIKey.THREAD_COLLECTIONS));
                    if (null != tmpPostCollectionMapList && tmpPostCollectionMapList.size() > 0) {
                        if (null == dataList) {
                            dataList = new ArrayList<PostCollectionEntity>();
                            adapter = null;
                        }
                        List<PostCollectionEntity> postCollectionList = EntityUtils.getPostCollectionEntityList(tmpPostCollectionMapList);
                        if (null != postCollectionList && postCollectionList.size() > 0) {
                            dataList.addAll(postCollectionList);
                        }

                    }
                }
            } else {
                showToast(message);
            }
            showDataList();
        }

    }

    @Override
    protected MyPostsCollectionAdapter initAdapter(Context context, List<PostCollectionEntity> dataList) {
        adapter = new MyPostsCollectionAdapter(context, dataList);
        return adapter;
    }

    public void reloadList() {
        dataList = null;
        fetchDataList(0);
    }

    public MyPostsCollectionAdapter getMyPostsCollectionAdapter() {
        return adapter;
    }

    public List<PostCollectionEntity> getPostCollectionEntityList() {
        return dataList;
    }

}
