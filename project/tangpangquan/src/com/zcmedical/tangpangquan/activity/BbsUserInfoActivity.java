package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.WindowUtils;
import com.zcmedical.common.view.scrollablelayout.CanScrollVerticallyDelegate;
import com.zcmedical.common.view.scrollablelayout.OnScrollChangedListener;
import com.zcmedical.common.view.scrollablelayout.ScrollableLayout;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.CommonPagerAdapter;
import com.zcmedical.tangpangquan.entity.UserEntity;
import com.zcmedical.tangpangquan.view.BbsUserInfoPager;
import com.zcmedical.tangpangquan.view.UserPostsListPager;

public class BbsUserInfoActivity extends BaseActivity implements OnClickListener {
    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";

    private UserEntity user;

    private List<BasePager> bbsUserInfoPagers;
    private ViewPager userInfoVp;

    private ColorDrawable blue;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bbs_user_info);

        init();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ScrollableLayout scrollableLayout = findView(R.id.scrollable_layout_user_info);
        outState.putInt(ARG_LAST_SCROLL_Y, scrollableLayout.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("NewApi")
    private void init() {
        initData();

        if (TpqApplication.getInstance(getContext()).isLogon()) {
            fetchFollowUserList();
        }

        String title = "个人资料";
        if (null != user) {
            title = user.getNickname();
        }
        Toolbar toolbar = initToolbar(title);

        blue = new ColorDrawable(getResources().getColor(R.color.colorPrimary)); //设置Toolbar的背景为主题色，但是全透明
        blue.setAlpha(0);
        toolbar.setBackground(blue);

        if (null != user) {
            initUI();
        }
    }

    private void initData() {
        user = TpqApplication.getInstance(getContext()).getShowingBbsUserEntity();
    }

    private void initUI() {
        initScrollableLayout();

        String sex = "男";
        switch (user.getSex()) {
        case APIKey.SEX_MALE:
            sex = "男";
            break;
        case APIKey.SEX_FEMALE:
            sex = "女";
            break;
        default:
            break;
        }

        setTextView(R.id.tv_user_name, user.getNickname());
        setTextView(R.id.tv_user_level, "Lv." + user.getLevel());
        setTextView(R.id.tv_user_sex, sex);
        setTextView(R.id.tv_user_follow_count, CounterUtils.format(user.getFollowsCount() < 0 ? 0 : user.getFollowsCount()));
        setTextView(R.id.tv_user_fans_count, CounterUtils.format(user.getFansCount() < 0 ? 0 : user.getFansCount()));
        setTextView(R.id.tv_user_follow_circle_count, CounterUtils.format(user.getCircleCount() < 0 ? 0 : user.getCircleCount()));

        setImageView(R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);

        initUserInfoViewPager();
    }

    private int indicatorOffset = 0;
    private int instance = 0;

    private void initUserInfoViewPager() {
        /*************************** init indicator ***************************/
        final View indicator = new View(getContext());
        indicator.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        final RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getContext(), 72), DensityUtil.dip2px(getContext(), 4));
        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        final TextView tabTrendsTv = findView(R.id.tv_bbs_user_info_tab_trends);
        tabTrendsTv.setOnClickListener(this);
        ViewTreeObserver popularTvObserver = tabTrendsTv.getViewTreeObserver();
        popularTvObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabTrendsTv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] location = new int[2];
                tabTrendsTv.getLocationOnScreen(location);

                indicatorOffset = location[0];

                RelativeLayout tabRelativeLayout = findView(R.id.rl_bbs_user_info_tab);
                tabRelativeLayout.addView(indicator);

            }
        });

        final TextView tabInfoTv = findView(R.id.tv_bbs_user_info_tab_info);
        tabInfoTv.setOnClickListener(this);
        ViewTreeObserver circleTvObserver = tabInfoTv.getViewTreeObserver();
        circleTvObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabInfoTv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] location = new int[2];
                tabInfoTv.getLocationOnScreen(location);

                instance = location[0] - indicatorOffset;

                initIndicatorOffset(indicator);

            }

            /**
             * 根据viewpager显示第几页显示指示器的位置
             * 
             * @param indicator
             */
            private void initIndicatorOffset(View indicator) {
                int currentItem = userInfoVp.getCurrentItem();
                switch (currentItem) {
                case 0:
                    indicatorParams.leftMargin = indicatorOffset;
                    break;
                case 1:
                    indicatorParams.leftMargin = indicatorOffset + instance;
                    break;
                default:
                    break;
                }

                indicator.setLayoutParams(indicatorParams);
            }
        });
        /****************************************************************************/
        final TextView[] tabBtnArray = new TextView[] { tabTrendsTv, tabInfoTv };
        userInfoVp = findView(R.id.vp_bbs_user_info);
        UserPostsListPager userPostsListPager = new UserPostsListPager(getContext(), user.getId());
        BbsUserInfoPager bbsUserInfoPager = new BbsUserInfoPager(getContext(), user);
        bbsUserInfoPagers = new ArrayList<BasePager>();
        bbsUserInfoPagers.add(userPostsListPager);
        bbsUserInfoPagers.add(bbsUserInfoPager);

        CommonPagerAdapter commonPagerAdapter = new CommonPagerAdapter(getContext(), bbsUserInfoPagers);
        userInfoVp.setAdapter(commonPagerAdapter);
        userInfoVp.setOnPageChangeListener(new OnPageChangeListener() {

            private int currentItem = 0;

            @Override
            public void onPageSelected(int arg0) {
                tabBtnArray[currentItem].setTextColor(getResources().getColor(R.color.micro_gray));
                tabBtnArray[arg0].setTextColor(getResources().getColor(R.color.colorPrimary));

                currentItem = arg0;

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                indicatorParams.leftMargin = (int) (indicatorOffset + instance * (arg0 + arg1));
                indicator.setLayoutParams(indicatorParams);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        int vpHeight = caculateMinContentLayoutHeight();
        FrameLayout.LayoutParams vpParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, vpHeight);
        userInfoVp.setLayoutParams(vpParams);
    }

    private void initScrollableLayout() {
        ScrollableLayout scrollableLayout = findView(R.id.scrollable_layout_user_info);
        RelativeLayout bbsTabRl = findView(R.id.rl_bbs_user_info_tab);
        scrollableLayout.setDraggableView(bbsTabRl);
        scrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {

            @Override
            public boolean canScrollVertically(int direction) {
                boolean isCanListScrollVertically = false;
                if (null != bbsUserInfoPagers) {
                    isCanListScrollVertically = ((BasePager) bbsUserInfoPagers.get(userInfoVp.getCurrentItem())).canListScrollVertically(direction);
                }
                return isCanListScrollVertically;
            }
        });
        scrollableLayout.setOnScrollChangedListener(new OnScrollChangedListener() {

            @Override
            public void onScrollChanged(int y, int oldY, int maxY) {
                float alpha = (float) y / (float) maxY;
                blue.setAlpha((int) (alpha * 255));
            }
        });
    }

    /**
     * 判断该用户是否关注过
     */
    private void fetchFollowUserList() {
        String userId = TpqApplication.getInstance(getContext()).getUserId();
        CommonRequest userFollowFetch = new CommonRequest();
        userFollowFetch.setRequestApiName(InterfaceConstant.API_USER_FOLLOW_FETCH);
        userFollowFetch.setRequestID(InterfaceConstant.REQUEST_ID_USER_FOLLOW_FETCH);
        userFollowFetch.addRequestParam(APIKey.USER_ID, user.getId());
        userFollowFetch.addRequestParam(APIKey.FOLLOWER, userId);

        addRequestAsyncTask(userFollowFetch);
    }

    private void updateMenu() {
        mMenu.clear();
        TpqApplication tpqApplication = TpqApplication.getInstance(getContext());
        if (tpqApplication.isLogon()) {
            //            if (tpqApplication.isLogon() && !tpqApplication.getUserId().equals(user.getId())) {
            if (tpqApplication.isUserFollowed(user.getId())) {
                getMenuInflater().inflate(R.menu.menu_cancel_follow_user, mMenu);
            } else {
                getMenuInflater().inflate(R.menu.menu_follow_user, mMenu);
            }
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
        case android.R.id.home:
            finish();
            break;
        case R.id.menu_item_follow_user:
            followUser();
            break;
        case R.id.menu_item_cancel_follow_user:
            cancelFollowUser();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void followUser() {
        if (TpqApplication.getInstance(getContext()).isLogon()) {
            String userId = TpqApplication.getInstance(getContext()).getUserId();
            CommonRequest followUserRequest = new CommonRequest();
            followUserRequest.setRequestApiName(InterfaceConstant.API_USER_FOLLOW_CREATE);
            followUserRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_FOLLOW_CREATE);
            followUserRequest.addRequestParam(APIKey.USER_ID, user.getId());
            followUserRequest.addRequestParam(APIKey.FOLLOWER, userId);

            addRequestAsyncTask(followUserRequest);
        } else {
            new BasicDialog.Builder(getContext()).setMessage(getString(R.string.no_logon_tips)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    private void cancelFollowUser() {
        if (TpqApplication.getInstance(getContext()).isLogon()) {
            String userId = TpqApplication.getInstance(getContext()).getUserId();
            CommonRequest cancelFollowUserRequest = new CommonRequest();
            cancelFollowUserRequest.setRequestApiName(InterfaceConstant.API_USER_FOLLOW_REMOVE);
            cancelFollowUserRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_FOLLOW_REMOVE);
            cancelFollowUserRequest.addRequestParam(APIKey.USER_ID, user.getId());
            cancelFollowUserRequest.addRequestParam(APIKey.FOLLOWER, userId);

            addRequestAsyncTask(cancelFollowUserRequest);
        } else {
            new BasicDialog.Builder(getContext()).setMessage(getString(R.string.no_logon_tips)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_bbs_user_info_tab_trends:
            userInfoVp.setCurrentItem(0);
            break;
        case R.id.tv_bbs_user_info_tab_info:
            userInfoVp.setCurrentItem(1);
            break;
        default:
            break;
        }

    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_USER_FOLLOW_FETCH.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    List<Map<String, Object>> followersList = TypeUtil.getList(resultMap.get(APIKey.USER_FOLLOWERS));
                    if (null != followersList && followersList.size() > 0) {
                        TpqApplication.getInstance(getContext()).setUserFollowed(user.getId(), true);
                    } else {
                        TpqApplication.getInstance(getContext()).setUserFollowed(user.getId(), false);
                    }
                    updateMenu();
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_USER_FOLLOW_CREATE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    Map<String, Object> userFollow = TypeUtil.getMap(resultMap.get(APIKey.USER_FOLLOWER));
                    if (null != userFollow) {
                        showToast("关注成功");
                        TpqApplication.getInstance(getContext()).setUserFollowed(user.getId(), true);
                        updateMenu();
                    } else {
                        showToast("关注失败");
                    }
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_USER_FOLLOW_REMOVE.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                showToast("取消关注成功");
                TpqApplication.getInstance(getContext()).setUserFollowed(user.getId(), false);
                updateMenu();
            } else {
                showToast("取消关注失败");
            }
        }
    }

    private int caculateMinContentLayoutHeight() {
        int statusBarHeight = WindowUtils.getStatusBarHeight(getContext());
        int actionBarHeight = WindowUtils.getActionBarHeight(getContext());
        //        int stickyHeaderViewHeight = DensityUtil.dip2px(getContext(), 36);
        int stickyHeaderViewHeight = 0;
        View tabs = findView(R.id.rl_bbs_user_info_tab);
        if (null != tabs) {
            stickyHeaderViewHeight = WindowUtils.getMeasureHeightOfView(tabs);
        }

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        int screenHeight = outMetrics.heightPixels;

        //        return screenHeight - statusBarHeight - actionBarHeight - stickyHeaderViewHeight;
        return screenHeight - statusBarHeight - stickyHeaderViewHeight;
    }
}
