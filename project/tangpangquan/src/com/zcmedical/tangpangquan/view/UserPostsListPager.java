package com.zcmedical.tangpangquan.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.AutoLoadMoreListView;
import com.zcmedical.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.PostsDetailActivity;
import com.zcmedical.tangpangquan.adapter.UserPostsAdapter;
import com.zcmedical.tangpangquan.entity.PostsEntity;

public class UserPostsListPager extends BasePager {

    private String userId;
    private int toBeContinued = 0;
    private UserPostsAdapter postsAdapter;
    private List<PostsEntity> postsList;

    private View layout;
    private AutoLoadMoreListView postsListView;

    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    public UserPostsListPager(Context context, String userId) {
        super(context);
        this.userId = userId;

        initLayout();
        fetchPostsList(0);
    }

    @SuppressLint("InflateParams")
    private void initLayout() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.xlistview_main, null);
        postsListView = findView(layout, R.id.xlistview_main_list_xlv);
        postsListView.setPullRefreshEnable(false);
        postsListView.setXListViewListener(new ListViewUpdateListener());
        postsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                if (null != postsAdapter) {
                    PostsEntity posts = postsAdapter.getItem(clickIndex);
                    TpqApplication.getInstance(getContext()).setShowingPostsEntity(posts);
                    Intent openPostsDetail = new Intent(getContext(), PostsDetailActivity.class);
                    getContext().startActivity(openPostsDetail);
                }
            }
        });
        final BackToTopBtn backToTopButton = findView(layout, R.id.btn_back_to_top);
        RelativeLayout.LayoutParams backToTopButtonParams = ((RelativeLayout.LayoutParams) backToTopButton.getLayoutParams());
        backToTopButtonParams.bottomMargin = DensityUtil.dip2px(getContext(), 70); // HomeActivity 底部tabbar的高度
        backToTopButton.setLayoutParams(backToTopButtonParams);
        backToTopButton.bindListView(postsListView);
        postsListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                backToTopButton.onVisibilityChanged(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });

        addView(layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.bottomMargin = DensityUtil.dip2px(getContext(), 48); // HomeActivity 底部tabbar的高度
        postsListView.setLayoutParams(params);
    }

    class ListViewUpdateListener implements IAutoLoadMoreListViewListener {

        @Override
        public void onRefresh() {
            if (!isRefresh && !isLoadMore) {
                isRefresh = true;
                postsList = null;

                fetchPostsList(0, CommonConstant.MSG_PAGE_SIZE);
            }
        }

        @Override
        public void onLoadMore() {
            if (!isRefresh && !isLoadMore) {
                isLoadMore = true;
                if (null != postsList && postsList.size() > 0) {
                    fetchPostsList(postsList.size(), CommonConstant.MSG_PAGE_SIZE);
                } else {
                    fetchPostsList(0);
                }
            }

        }

    }

    private void fetchPostsList(int offset) {
        fetchPostsList(offset, CommonConstant.MSG_PAGE_SIZE);
    }

    private void fetchPostsList(int offset, int page_sizes) {
        CommonRequest fetchPostsList = new CommonRequest();
        fetchPostsList.setRequestApiName(InterfaceConstant.API_THREAD_FETCH);
        fetchPostsList.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_FETCH);
        fetchPostsList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchPostsList.addRequestParam(APIKey.COMMON_PAGE_SIZE, page_sizes);
        fetchPostsList.addRequestParam(APIKey.COMMON_SORT_TYPES, "desc");
        fetchPostsList.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.THREAD_VIEWS_COUNT);
        fetchPostsList.addRequestParam(APIKey.THREAD_STATUS, APIKey.COMMON_STATUS_LEGAL);
        fetchPostsList.addRequestParam(APIKey.USER_ID, userId);

        addRequestAsyncTask(fetchPostsList);
    }

    @Override
    protected void onResponseAsyncTaskRender(Map<String, Object> result, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_FETCH.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                    if (null != resultMap) {
                        toBeContinued = TypeUtil.getInteger(resultMap.get(APIKey.COMMON_TO_BE_CONTINUED), 0);
                        List<Map<String, Object>> rawPostsList = TypeUtil.getList(resultMap.get(APIKey.THREADS));
                        if (null != rawPostsList && rawPostsList.size() > 0) {
                            List<PostsEntity> tmpPostsList = EntityUtils.getPostsEntityList(rawPostsList);
                            if (null != tmpPostsList) {
                                if (null == postsList) {
                                    postsList = new ArrayList<PostsEntity>();
                                    postsAdapter = null;
                                }
                                postsList.addAll(tmpPostsList);
                            }
                        }
                    }

                } else {
                    showToast(message);
                }
            }
            showPostsList();
        }
    }

    private void showPostsList() {
        RelativeLayout loadingRl = findView(layout, R.id.xlistview_main_loading_container_rl);
        loadingRl.setVisibility(View.GONE);
        if (null == postsAdapter) {
            postsAdapter = new UserPostsAdapter(getContext(), postsList);
            postsListView.setAdapter(postsAdapter);
        } else {
            postsAdapter.notifyDataSetChanged();
        }

        TextView noRecordTv = findView(layout, R.id.xlistview_main_no_record_tv);
        if (null != postsList && postsList.size() > 0) {
            noRecordTv.setVisibility(View.GONE);
            postsListView.setVisibility(View.VISIBLE);
        } else {
            noRecordTv.setVisibility(View.VISIBLE);
            postsListView.setVisibility(View.GONE);
        }

        if (toBeContinued == 1) {
            postsListView.setPullLoadEnable(true);
        } else {
            postsListView.setPullLoadEnable(false);
        }

        isRefresh = false;
        isLoadMore = false;
        postsListView.stopRefresh();
        postsListView.stopLoadMore();
    }

    @Override
    public boolean canListScrollVertically(int direction) {
        return null != postsListView && postsListView.canScrollVertically(direction);
    }
}
