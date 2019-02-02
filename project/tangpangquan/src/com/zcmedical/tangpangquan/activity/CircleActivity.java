package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.AutoLoadMoreListView;
import com.zcmedical.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.CirclePostsListAdapter;
import com.zcmedical.tangpangquan.entity.CircleEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.view.BackToTopBtn;

public class CircleActivity extends BaseActivity implements OnClickListener {

    private String circleId;

    private CircleEntity mCircleEntity;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private int toBeContinued = 0;
    private CirclePostsListAdapter postsAdapter;
    private List<PostsEntity> postsList;

    private AutoLoadMoreListView postsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_circle);

        init();

        showProgressDialog();
        fetchPostsList(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initJoinCircleBtn();
    }

    private void init() {
        initData();
        initToolbar();
        initUI();
    }

    private void initData() {
        mCircleEntity = TpqApplication.getInstance(getContext()).getShowingCircleEntity();
        if (null != mCircleEntity) {
            circleId = mCircleEntity.getId();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setTitle(getString(R.string.circle_activity_title));
        }
    }

    private void initUI() {
        initRefreshBtn();
        initCirclePostsListView();
    }

    private void initJoinCircleBtn() {
        LinearLayout joinCircleLl = findView(R.id.ll_join_circle);
        joinCircleLl.setOnClickListener(this);

        if (TpqApplication.getInstance(getContext()).isLogon()) {
            boolean isFollowed = TpqApplication.getInstance(getContext()).isCircleFollowed(circleId);
            if (isFollowed) {
                joinCircleLl.setVisibility(View.GONE);

                return;
            } else {
                joinCircleLl.setVisibility(View.VISIBLE);
            }
            List<CircleEntity> myFollowCircleList = TpqApplication.getInstance(getContext()).getMyFollowCircle();
            if (null != myFollowCircleList && myFollowCircleList.size() > 0) {
                for (int i = 0; i < myFollowCircleList.size(); i++) {
                    CircleEntity circle = myFollowCircleList.get(i);
                    if (null != circle && circle.getId().equals(circleId)) {
                        joinCircleLl.setVisibility(View.GONE);
                    } else {
                        joinCircleLl.setVisibility(View.VISIBLE);
                    }
                }
                joinCircleLl.setClickable(true);
            } else {
                fetchMyFollowForumList();
            }
        }
    }

    private void initRefreshBtn() {
        RelativeLayout refreshDataRl = findView(R.id.rl_refresh_posts_data);
        refreshDataRl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                fetchPostsList(0);
            }
        });

    }

    private void initCirclePostsListView() {
        View circleInfoView = LayoutInflater.from(getContext()).inflate(R.layout.headview_circle_info, null);
        RelativeLayout circleInfoRl = findView(circleInfoView, R.id.rl_headview_circle_info);
        circleInfoRl.setOnClickListener(this);
        initCircleInfos(circleInfoView);

        postsListView = findView(R.id.almlv_circle_posts);
        postsListView.addHeaderView(circleInfoView, null, false);
        postsListView.setPullRefreshEnable(true);
        postsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                if (null != postsAdapter) {
                    PostsEntity posts = postsAdapter.getItem(clickIndex);
                    if (null != posts) {
                        TpqApplication.getInstance(getContext()).setShowingPostsEntity(posts);
                        Intent openPostsDetail = new Intent(getContext(), PostsDetailActivity.class);
                        getContext().startActivity(openPostsDetail);
                    }
                }
            }
        });
        postsListView.setXListViewListener(new IAutoLoadMoreListViewListener() {

            @Override
            public void onRefresh() {
                if (!isRefresh && !isLoadMore) {
                    isRefresh = true;

                    postsList = null;

                    fetchPostsList(0);
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
        });

        final BackToTopBtn backToTopButton = findView(R.id.btn_back_to_top);
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

    }

    private void initCircleInfos(View circleInfoView) {
        CircleEntity circle = TpqApplication.getInstance(getContext()).getShowingCircleEntity();

        if (null != circle) {
            setTextView(circleInfoView, R.id.tv_forum_title, circle.getTitle());
            setTextView(circleInfoView, R.id.tv_forum_posts_count, circle.getPostsCount() + "");
            setTextView(circleInfoView, R.id.tv_forum_member_count, circle.getUserCount() + "");
            setTextView(circleInfoView, R.id.tv_forum_description, circle.getDescription());

            setImageView(circleInfoView, R.id.cimv_forum_logo, circle.getForumLogoUrl());
        }
    }

    private void fetchPostsList(int offset) {
        fetchPostsList(offset, CommonConstant.MSG_PAGE_SIZE);
    }

    private void fetchPostsList(int offset, int pageSize) {
        CommonRequest fetchPostsList = new CommonRequest();
        fetchPostsList.setRequestApiName(InterfaceConstant.API_THREAD_FETCH);
        fetchPostsList.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_FETCH);
        fetchPostsList.addRequestParam(APIKey.THREAD_STATUS, APIKey.COMMON_STATUS_LEGAL);
        fetchPostsList.addRequestParam(APIKey.FORUM_ID, circleId);
        fetchPostsList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchPostsList.addRequestParam(APIKey.COMMON_PAGE_SIZE, pageSize);
        fetchPostsList.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.THREAD_TOP);
        fetchPostsList.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);

        addRequestAsyncTask(fetchPostsList);
    }

    @Override
    protected void onResponseAsyncTaskRender(Map<String, Object> result, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_FETCH.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
                if (APIKey.STATUS_SUCCESSFUL == status) {
                    //status标志为成功，即实例化postsList，后面dealResult函数根据postsList是否为空判断是请求网络失败还是没帖子数据两种情况
                    if (null == postsList) {
                        postsList = new ArrayList<PostsEntity>();
                        postsAdapter = null;
                    }
                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                    if (null != resultMap) {
                        toBeContinued = TypeUtil.getInteger(resultMap.get(APIKey.COMMON_TO_BE_CONTINUED), 0);
                        List<Map<String, Object>> threads = TypeUtil.getList(resultMap.get(APIKey.THREADS));
                        if (null != threads && threads.size() > 0) {

                            List<PostsEntity> tmpPostsList = EntityUtils.getPostsEntityList(threads);

                            if (null != tmpPostsList && tmpPostsList.size() > 0) {
                                postsList.addAll(tmpPostsList);
                            }
                        }
                    }
                } else {
                    showToast(message);
                }
            }
            dimissProgressDialog();
            dealResult();
        } else if (InterfaceConstant.REQUEST_ID_FORUM_USER_FETCH.equals(requestID)) {
            int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), 1);
            String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
            if (0 == status) {
                Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.FORUM_USERS));
                if (null != resultMap) {
                    List<Map<String, Object>> forumUsers = TypeUtil.getList(resultMap.get(APIKey.FORUM_USERS));
                    if (null != forumUsers && forumUsers.size() > 0) {
                        List<CircleEntity> myFollowCircleList = getMyFollowCircleList(forumUsers);
                        if (null != myFollowCircleList && myFollowCircleList.size() > 0) {
                            TpqApplication.getInstance(getContext()).setMyFollowCircle(myFollowCircleList);

                            initJoinCircleBtn();
                        }
                    }
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_FORUM_USER_CREATE.equals(requestID)) {
            int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), 1);
            String message = TypeUtil.getString(result.get(APIKey.COMMON_MESSAGE), "");
            if (0 == status) {
                TpqApplication.getInstance(getContext()).setCircleFollowed(circleId, true);
                initJoinCircleBtn();
                showToast(getString(R.string.follow_successful));
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        }
    }

    private void dealResult() {
        RelativeLayout refreshRl = findView(R.id.rl_no_posts_data);
        RelativeLayout dataListRl = findView(R.id.rl_data_list);

        if (null == postsList) { //请求网络有问题
            refreshRl.setVisibility(View.VISIBLE);
            dataListRl.setVisibility(View.GONE);
        } else {
            refreshRl.setVisibility(View.GONE);
            dataListRl.setVisibility(View.VISIBLE);
            showPostsList();
        }
    }

    private void showPostsList() {

        if (null == postsAdapter) {
            postsAdapter = new CirclePostsListAdapter(getContext(), postsList);
            postsListView.setAdapter(postsAdapter);
        } else {
            postsAdapter.notifyDataSetChanged();
        }

        setListCanLoadMore();
    }

    private void setListCanLoadMore() {
        isRefresh = false;
        isLoadMore = false;
        if (null != postsListView) {
            postsListView.stopLoadMore();
            postsListView.stopRefresh();
            if (toBeContinued == 1) {
                postsListView.setPullLoadEnable(true);
            } else {
                postsListView.setPullLoadEnable(false);
            }
        }

    }

    private void fetchMyFollowForumList() {
        if (TpqApplication.getInstance(getContext()).isLogon()) {
            String userId = TpqApplication.getInstance(getContext()).getUserId();
            if (Validator.isIdValid(userId)) {
                CommonRequest loadMyForum = new CommonRequest();
                loadMyForum.setRequestApiName(InterfaceConstant.API_FORUM_USER_FETCH);
                loadMyForum.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_USER_FETCH);
                loadMyForum.addRequestParam(APIKey.FORUM_STATUS, 1);
                loadMyForum.addRequestParam(APIKey.USER_ID, userId);
                loadMyForum.addRequestParam(APIKey.COMMON_OFFSET, 0);
                loadMyForum.addRequestParam(APIKey.COMMON_PAGE_SIZE, 100);

                addRequestAsyncTask(loadMyForum);
            }

        }
    }

    private List<CircleEntity> getMyFollowCircleList(List<Map<String, Object>> forumUsers) {
        List<CircleEntity> myFollowCircleList = new ArrayList<CircleEntity>();

        for (int i = 0; i < forumUsers.size(); i++) {
            Map<String, Object> forumUser = forumUsers.get(i);
            if (null != forumUser) {
                Map<String, Object> forum = TypeUtil.getMap(forumUser.get(APIKey.FORUM));
                if (null != forum) {
                    CircleEntity circle = EntityUtils.getCircleEntity(forum);
                    if (null != circle) {
                        myFollowCircleList.add(circle);
                    }
                }
            }
        }

        return myFollowCircleList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.menu_bbs_addpost:
            Intent openPublishPost = new Intent(getContext(), PublishPostActivity.class);
            openPublishPost.putExtra(PublishPostActivity.KEY_FORUM_ID, mCircleEntity.getId());
            startActivity(openPublishPost);
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_headview_circle_info:
            Intent openCircleInfos = new Intent(getContext(), CircleInfoActivity.class);
            startActivity(openCircleInfos);
            break;
        case R.id.ll_join_circle:
            followCircle();
            break;
        default:
            break;
        }
    }

    private void followCircle() {
        boolean isLogon = TpqApplication.getInstance(getContext()).isLogon();
        if (isLogon) {
            String userId = TpqApplication.getInstance(getContext()).getUserId();
            CommonRequest followRequest = new CommonRequest();
            followRequest.setRequestApiName(InterfaceConstant.API_FORUM_USER_CREATE);
            followRequest.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_USER_CREATE);
            followRequest.addRequestParam(APIKey.USER_ID, userId);
            followRequest.addRequestParam(APIKey.FORUM_ID, circleId);

            addRequestAsyncTask(followRequest);

            showProgressDialog(getString(R.string.following_circle));
        } else {
            BasicDialog.Builder builder = new BasicDialog.Builder(getContext());
            builder.setMessage(getString(R.string.no_logon_follow_circle_tips));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

}
