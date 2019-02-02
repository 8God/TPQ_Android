package com.zcmedical.tangpangquan.fragment;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.base.BaseFragment;
import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.WindowUtils;
import com.zcmedical.common.view.scrollablelayout.CanScrollVerticallyDelegate;
import com.zcmedical.common.view.scrollablelayout.ScrollableLayout;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.BbsSearchActivity;
import com.zcmedical.tangpangquan.activity.PublishPostActivity;
import com.zcmedical.tangpangquan.adapter.BbsBannerAdapter;
import com.zcmedical.tangpangquan.adapter.CommonPagerAdapter;
import com.zcmedical.tangpangquan.entity.BannerEntity;
import com.zcmedical.tangpangquan.view.BbsBannerPager;
import com.zcmedical.tangpangquan.view.CircleListPager;
import com.zcmedical.tangpangquan.view.HotPostsListPager;

@SuppressLint("NewApi")
public class BbsFragment extends BaseFragment implements OnClickListener {
    private final int SCROLL_DELAY = 3000;
    private final int DOT_MARGIN = 12;
    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";

    private int currentPagerPosition = 0;
    private int updateIndex = 0;
    private boolean isFinishInit = false;
    private List<BbsBannerPager> bannerList;
    private List<BasePager> bbsContentPagers;
    private ImageView[] dotArray;

    private View contentView;
    private ViewPager bbsBannerVp;
    private ViewPager contentVp;

    Handler updateHandler = new Handler();
    Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            bbsBannerVp.setCurrentItem(++updateIndex, true);

            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMenuVisibility(true);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_bbs, null);

        init();

        return contentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ScrollableLayout scrollableLayout = findView(contentView, R.id.scrollable_layout_bbs);
        outState.putInt(ARG_LAST_SCROLL_Y, scrollableLayout.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != bannerList && bannerList.size() > 1) {
            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != bannerList && bannerList.size() > 1) {
            updateHandler.removeCallbacks(updateRunnable);
        }
    }

    private void init() {
        initBannerList();
        initUI();
        isFinishInit = true;//结束初始化UI标志为true
    }

    private void initUI() {
        initScrollableLayout();
        //        initBannerViewPager();
        initBbsContentViewPager();
    }

    private void initScrollableLayout() {
        ScrollableLayout scrollableLayout = findView(contentView, R.id.scrollable_layout_bbs);
        RelativeLayout bbsTabRl = findView(contentView, R.id.rl_bbs_tab);
        scrollableLayout.setDraggableView(bbsTabRl);
        scrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {

            @Override
            public boolean canScrollVertically(int direction) {
                boolean isCanListScrollVertically = false;
                if (null != bbsContentPagers) {
                    isCanListScrollVertically = ((BasePager) bbsContentPagers.get(contentVp.getCurrentItem())).canListScrollVertically(direction);
                }
                return isCanListScrollVertically;
            }
        });

    }

    private void initBannerList() {
        //        bannerList = new ArrayList<BbsBannerPager>();
        //
        //        BannerEntity be1 = new BannerEntity();
        //        BannerEntity be2 = new BannerEntity();
        //        BannerEntity be3 = new BannerEntity();
        //        BannerEntity be4 = new BannerEntity();
        //
        //        be1.setBannerBgUrl("http://h.hiphotos.baidu.com/image/w%3D400/sign=4d234f34daf9d72a1764111de42b282a/4a36acaf2edda3cc2bb6932b02e93901213f926e.jpg");
        //        be2.setBannerBgUrl("http://h.hiphotos.baidu.com/image/w%3D400/sign=e19c7908b4003af34dbadd60052ac619/2e2eb9389b504fc2022d2904e7dde71190ef6d45.jpg");
        //        be3.setBannerBgUrl("http://d.hiphotos.baidu.com/image/w%3D400/sign=85c1c8fcaa773912c4268461c8188675/908fa0ec08fa513d5555e29f3e6d55fbb3fbd9f0.jpg");
        //        be4.setBannerBgUrl("http://c.hiphotos.baidu.com/image/w%3D400/sign=3d704a35af345982c58ae4923cf5310b/95eef01f3a292df54a537254be315c6035a873db.jpg");
        //
        //        BbsBannerPager bbp1 = new BbsBannerPager(getActivity(), be1);
        //        BbsBannerPager bbp2 = new BbsBannerPager(getActivity(), be2);
        //        BbsBannerPager bbp3 = new BbsBannerPager(getActivity(), be3);
        //        BbsBannerPager bbp4 = new BbsBannerPager(getActivity(), be4);
        //
        //        bannerList.add(bbp1);
        //        bannerList.add(bbp2);
        //        bannerList.add(bbp3);
        //        bannerList.add(bbp4);

        bannerList = TpqApplication.getInstance().getBannerList();
        if (null != bannerList && bannerList.size() > 0) {
            initBannerViewPager();
        } else {
            CommonRequest fetchBannerRequest = new CommonRequest();
            fetchBannerRequest.setRequestApiName(InterfaceConstant.API_FORUM_BANNER_FETCH);
            fetchBannerRequest.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_BANNER_FETCH);
            fetchBannerRequest.addRequestParam(APIKey.COMMON_STATUS, APIKey.COMMON_STATUS_LEGAL);
            fetchBannerRequest.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.COMMON_CREATED_AT);
            fetchBannerRequest.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_DESC);
            fetchBannerRequest.addRequestParam(APIKey.COMMON_OFFSET, 0);
            fetchBannerRequest.addRequestParam(APIKey.COMMON_PAGE_SIZE, 4);

            addRequestAsyncTask(contentView, fetchBannerRequest);
        }

    }

    private void initBannerViewPager() {
        bbsBannerVp = findView(contentView, R.id.vp_bbs_banner);
        BbsBannerAdapter bbsBannerAdapter = new BbsBannerAdapter(getActivity(), bannerList);
        bbsBannerVp.setAdapter(bbsBannerAdapter);
        bbsBannerVp.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int selectedPosition) {
                updateIndex = selectedPosition;
                selectedPosition %= bannerList.size();
                changeDotState(currentPagerPosition, selectedPosition);

                currentPagerPosition = selectedPosition;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        initDotView();

        bbsBannerVp.setCurrentItem(0);

        if (null != bannerList && bannerList.size() > 1) {
            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);
        }
    }

    private int indicatorOffset = 0;
    private int instance = 0;

    private void initBbsContentViewPager() {
        /*************************** init indicator ***************************/
        final View indicator = new View(getActivity());
        indicator.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        final RelativeLayout.LayoutParams indicatorParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(getActivity(), 72), DensityUtil.dip2px(getActivity(), 4));
        indicatorParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        final TextView tabPopularTv = findView(contentView, R.id.tv_tab_popular);
        tabPopularTv.setOnClickListener(this);
        ViewTreeObserver popularTvObserver = tabPopularTv.getViewTreeObserver();
        popularTvObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabPopularTv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] location = new int[2];
                tabPopularTv.getLocationOnScreen(location);

                indicatorOffset = location[0];

                RelativeLayout tabRelativeLayout = findView(contentView, R.id.rl_bbs_tab);
                tabRelativeLayout.addView(indicator);

            }
        });

        final TextView tabCircleTv = findView(contentView, R.id.tv_tab_circle);
        tabCircleTv.setOnClickListener(this);
        ViewTreeObserver circleTvObserver = tabCircleTv.getViewTreeObserver();
        circleTvObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabCircleTv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int[] location = new int[2];
                tabCircleTv.getLocationOnScreen(location);

                instance = location[0] - indicatorOffset;

                initIndicatorOffset(indicator);

                ScrollableLayout scrollableLayout = findView(contentView, R.id.scrollable_layout_bbs);
                bbsBannerVp = findView(contentView, R.id.vp_bbs_banner);
                scrollableLayout.setMaxScrollY(bbsBannerVp.getMeasuredHeight());
            }

            /**
             * 根据viewpager显示第几页显示指示器的位置
             * 
             * @param indicator
             */
            private void initIndicatorOffset(View indicator) {
                int currentItem = contentVp.getCurrentItem();
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

        final TextView[] tabBtnArray = new TextView[] { tabPopularTv, tabCircleTv };

        contentVp = findView(contentView, R.id.vp_bbs_content);
        HotPostsListPager postsListPager = new HotPostsListPager(getActivity());
        CircleListPager circleListPager = new CircleListPager(getActivity());
        bbsContentPagers = new ArrayList<BasePager>();
        bbsContentPagers.add(postsListPager);
        bbsContentPagers.add(circleListPager);

        CommonPagerAdapter commonPagerAdapter = new CommonPagerAdapter(getActivity(), bbsContentPagers);
        contentVp.setAdapter(commonPagerAdapter);
        contentVp.setOnPageChangeListener(new OnPageChangeListener() {

            private int currentItem = 0;

            @Override
            public void onPageSelected(int arg0) {
                tabBtnArray[currentItem].setTextColor(getResources().getColor(R.color.micro_gray));
                tabBtnArray[arg0].setTextColor(getResources().getColor(R.color.colorPrimary));

                currentItem = arg0;

                bbsContentPagers.get(arg0).getDataList();
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
        contentVp.setLayoutParams(vpParams);
    }

    @SuppressLint("NewApi")
    private void initDotView() {
        LinearLayout dot_ll = findView(contentView, R.id.ll_banner_dot);

        dotArray = new ImageView[bannerList.size()];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(DOT_MARGIN, DOT_MARGIN, DOT_MARGIN, DOT_MARGIN);

        for (int i = 0; i < bannerList.size(); i++) {
            ImageView dotView = new ImageView(getActivity());
            dotView.setBackground(getResources().getDrawable(R.drawable.selector_dot));
            dotView.setLayoutParams(params);
            dotArray[i] = dotView;
            dotView.setEnabled(false);

            dot_ll.addView(dotView, i);
        }
        bbsBannerVp = findView(contentView, R.id.vp_bbs_banner);
        if (bbsBannerVp != null) {
            if (!isFinishInit) {
                dotArray[0].setEnabled(true);
            }
        }
    }

    private void changeDotState(int currentPagerPosition, int selectedPosition) {
        dotArray[currentPagerPosition].setEnabled(false);
        dotArray[selectedPosition].setEnabled(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bbs_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bbs_search:
                Intent openBbsSearch = new Intent(getActivity(), BbsSearchActivity.class);
                startActivity(openBbsSearch);
                break;
            case R.id.menu_bbs_addpost:
                Intent openPublishPost = new Intent(getActivity(), PublishPostActivity.class);
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
            case R.id.tv_tab_popular:
                contentVp.setCurrentItem(0, true);
                break;
            case R.id.tv_tab_circle:
                contentVp.setCurrentItem(1, true);
                break;
            default:
                break;
        }
    }

    private int caculateMinContentLayoutHeight() {
        int statusBarHeight = WindowUtils.getStatusBarHeight(getActivity());
        int actionBarHeight = WindowUtils.getActionBarHeight(getActivity());
        int stickyHeaderViewHeight = 0;
        View tabs = findView(contentView, R.id.rl_bbs_tab);
        if (null != tabs) {
            stickyHeaderViewHeight = WindowUtils.getMeasureHeightOfView(tabs);
        }

        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

        int screenHeight = outMetrics.heightPixels;

        return screenHeight - statusBarHeight - actionBarHeight - stickyHeaderViewHeight;
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_FORUM_BANNER_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> bannerMapList = TypeUtil.getList(resultMap.get(APIKey.FORUM_BANNERS));
                    if (null != bannerMapList && bannerMapList.size() > 0) {
                        List<BannerEntity> bannerEntityList = EntityUtils.getBannerEntityList(bannerMapList);
                        if (null != bannerEntityList && bannerEntityList.size() > 0) {
                            bannerList = new ArrayList<BbsBannerPager>();
                            for (int i = 0; i < bannerEntityList.size(); i++) {
                                BannerEntity bannerEntity = bannerEntityList.get(i);
                                if (null != bannerEntity) {
                                    BbsBannerPager bannerPager = new BbsBannerPager(getActivity(), bannerEntity);
                                    bannerList.add(bannerPager);
                                }
                            }
                            initBannerViewPager();
                            ProgressBar loadingBannerPb = findView(contentView, R.id.pb_loading_banners);
                            loadingBannerPb.setVisibility(View.GONE);
                        }
                    }

                }
            }
        }
    }
}
