package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.Date;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.AutoLoadMoreListView;
import com.zcmedical.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.CircleMembersAdapter;
import com.zcmedical.tangpangquan.entity.CircleEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class CircleInfoActivity extends BaseActivity implements OnClickListener {

    private String circleId;
    private CircleEntity mCircleEntity;

    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private boolean isFollowed = false;
    private boolean isAdmin = false;

    private int toBeContinued = 0;
    private CircleMembersAdapter membersAdapter;
    private List<UserEntity> memberList;

    private Menu mMenu;

    private AutoLoadMoreListView memberListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_circle);

        initData();

        if (null != mCircleEntity) {
            init();

            showProgressDialog();
            fetchMembersList(0);
        }

    }

    private void initData() {
        mCircleEntity = TpqApplication.getInstance(getContext()).getShowingCircleEntity();
        if (null != mCircleEntity) {
            circleId = mCircleEntity.getId();

            String userId = TpqApplication.getInstance(getContext()).getUserId();
            String adminUserId = null;
            UserEntity admin = mCircleEntity.getCircleAdmin();
            if (null != admin) {
                adminUserId = admin.getId();
            }

            if (Validator.isIdValid(userId) && Validator.isIdValid(adminUserId) && userId.equals(adminUserId)) {
                isAdmin = true;
            }
        }

    }

    private void init() {

        initToolbar();
        initUI();

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
            actionBar.setTitle(mCircleEntity.getTitle());
        }
    }

    private void initUI() {
        initJoinCircleBtn();
        initMemberListView();
    }

    private void initJoinCircleBtn() {
        LinearLayout joinCircleLl = findView(R.id.ll_join_circle);
        joinCircleLl.setOnClickListener(this);

        if (TpqApplication.getInstance(getContext()).isLogon()) {
            isFollowed = TpqApplication.getInstance(getContext()).isCircleFollowed(circleId);
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
                        isFollowed = true;

                        break;
                    } else {
                        joinCircleLl.setVisibility(View.VISIBLE);
                    }
                }
                joinCircleLl.setClickable(true);
            }
        }
    }

    private void initMemberListView() {
        View circleInfoView = initHeadView();

        memberListView = findView(R.id.almlv_circle_posts);
        memberListView.setPullRefreshEnable(true);
        memberListView.setPullLoadEnable(false);
        memberListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                if (null != membersAdapter) {
                    UserEntity user = membersAdapter.getItem(clickIndex);
                    if (null != user) {
                        TpqApplication.getInstance(getContext()).setShowingBbsUserEntity(user);
                        Intent openBbsUserInfo = new Intent(getContext(), BbsUserInfoActivity.class);
                        startActivity(openBbsUserInfo);
                    }
                }
            }
        });
        memberListView.setXListViewListener(new IAutoLoadMoreListViewListener() {

            @Override
            public void onRefresh() {
                if (!isRefresh && !isLoadMore) {
                    isRefresh = true;

                    memberList = null;

                    fetchMembersList(0);
                }
            }

            @Override
            public void onLoadMore() {
                if (!isRefresh && !isLoadMore) {
                    isLoadMore = true;

                    if (null != memberList && memberList.size() > 0) {
                        fetchMembersList(memberList.size(), CommonConstant.MSG_PAGE_SIZE);
                    } else {
                        fetchMembersList(0);
                    }
                }
            }

        });
        memberListView.addHeaderView(circleInfoView, null, false);

    }

    private View initHeadView() {
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.headview_circle_detail_info, null);

        setTextView(headView, R.id.tv_circle_member_count, mCircleEntity.getUserCount() + " 名");

        final UserEntity admin = mCircleEntity.getCircleAdmin();
        if (null != admin) {
            setTextView(headView, R.id.tv_user_name, admin.getNickname());
            setTextView(headView, R.id.tv_user_level, "Lv." + admin.getLevel());
            setTextView(headView, R.id.tv_user_fans_count, CounterUtils.format(admin.getFansCount()) + getString(R.string.fans_title));
            setTextView(headView, R.id.tv_user_posts_count, CounterUtils.format(admin.getFollowsCount()) + getString(R.string.posts_count_title));
            setImageView(headView, R.id.cimv_user_head, admin.getHeadPic(), R.drawable.common_icon_default_user_head);

            findView(headView, R.id.cimv_user_head).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    TpqApplication.getInstance(getContext()).setShowingBbsUserEntity(admin);
                    Intent openBbsUserInfo = new Intent(getContext(), BbsUserInfoActivity.class);
                    startActivity(openBbsUserInfo);
                }
            });
        }
        return headView;
    }

    private void fetchMembersList(int offset) {
        fetchMembersList(0, CommonConstant.MSG_PAGE_SIZE);
    }

    private void fetchMembersList(int offset, int pageSize) {
        CommonRequest fetchMemberRequest = new CommonRequest();
        fetchMemberRequest.setRequestApiName(InterfaceConstant.API_FORUM_USER_FETCH);
        fetchMemberRequest.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_USER_FETCH);
        fetchMemberRequest.addRequestParam(APIKey.FORUM_ID, circleId);
        fetchMemberRequest.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.COMMON_CREATED_AT);
        fetchMemberRequest.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);

        addRequestAsyncTask(fetchMemberRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ll_join_circle:
            followCircle();
            break;
        default:
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_item_exit_circle:
            DialogUtils.showAlertDialog(getContext(), getString(R.string.exit_circle_alert), getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelFollowCircle();
                    dialog.dismiss();
                }
            }, getString(R.string.no), null);
            break;
        case android.R.id.home:
            finish();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {
        mMenu.clear();

        if (isFollowed && !isAdmin) {
            getMenuInflater().inflate(R.menu.menu_exit_circle, mMenu);
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

    private void cancelFollowCircle() {
        boolean isLogon = TpqApplication.getInstance(getContext()).isLogon();
        if (isLogon) {
            String userId = TpqApplication.getInstance(getContext()).getUserId();
            CommonRequest cancelFollowRequest = new CommonRequest();
            cancelFollowRequest.setRequestApiName(InterfaceConstant.API_FORUM_USER_REMOVE);
            cancelFollowRequest.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_USER_REMOVE);
            cancelFollowRequest.addRequestParam(APIKey.USER_ID, userId);
            cancelFollowRequest.addRequestParam(APIKey.FORUM_ID, circleId);

            addRequestAsyncTask(cancelFollowRequest);

            showProgressDialog(getString(R.string.cancel_following_circle));
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_FORUM_USER_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                isFollowed = true;
                dealFollowCircleResult();

                showToast(getString(R.string.follow_successful));
            } else {
                showToast(message);
            }
            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_FORUM_USER_REMOVE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                isFollowed = false;
                dealFollowCircleResult();

                showToast(getString(R.string.cancel_follow_successful));
            } else {
                showToast(message);
            }
            dimissProgressDialog();

            finish();

        } else if (InterfaceConstant.REQUEST_ID_FORUM_USER_FETCH.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    this.toBeContinued = toBeContinued;
                    List<Map<String, Object>> rawUserList = TypeUtil.getList(resultMap.get(APIKey.FORUM_USERS));
                    if (null != rawUserList && rawUserList.size() > 0) {
                        if (null == memberList) {
                            memberList = new ArrayList<UserEntity>();

                            membersAdapter = null;
                        }

                        List<UserEntity> tmpUserList = EntityUtils.getCircleMemberEntityList(rawUserList);
                        memberList.addAll(tmpUserList);

                        memberListView.setRefreshTime(CommonConstant.refreshTimeFormat.format(new Date()));
                    }
                }
            } else {
                showToast(message);
            }
            dimissProgressDialog();
            dealFetchCircleMemberResult();
        }
    }

    private void dealFetchCircleMemberResult() {
        if (null == memberList || memberList.size() == 0) {
            memberList = new ArrayList<UserEntity>();
        }

        showPostsList();
    }

    private void dealFollowCircleResult() {
        TpqApplication.getInstance(getContext()).setCircleFollowed(circleId, isFollowed);
        initJoinCircleBtn();
        updateMenu();

        memberList = null;
        showProgressDialog();
        fetchMembersList(0);
    }

    private void showPostsList() {
        if (null == membersAdapter) {
            membersAdapter = new CircleMembersAdapter(getContext(), memberList);
            memberListView.setAdapter(membersAdapter);
        } else {
            membersAdapter.notifyDataSetChanged();
        }

        setListCanLoadMore();
    }

    private void setListCanLoadMore() {
        isRefresh = false;
        isLoadMore = false;

        memberListView.stopLoadMore();
        memberListView.stopRefresh();
        if (toBeContinued == 1) {
            memberListView.setPullLoadEnable(true);
        } else {
            memberListView.setPullLoadEnable(false);
        }

    }

}
