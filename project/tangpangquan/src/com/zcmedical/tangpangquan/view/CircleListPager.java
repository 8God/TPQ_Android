package com.zcmedical.tangpangquan.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.zcmedical.tangpangquan.activity.CircleActivity;
import com.zcmedical.tangpangquan.adapter.CircleAdapter;
import com.zcmedical.tangpangquan.entity.CircleEntity;

public class CircleListPager extends BasePager {

    private int toBeContinued = 0;
    private CircleAdapter circleAdapter;
    private List<CircleEntity> circleList;

    private View layout;
    private AutoLoadMoreListView circleListView;

    private boolean isLoadMore = false;
    private boolean isRefresh = false;

    public CircleListPager(Context context) {
        super(context);

        initLayout();

    }

    @Override
    public void getDataList() {
        super.getDataList();

        if (null == circleList) {
            List<CircleEntity> allCircleList = TpqApplication.getInstance(getContext()).getAllCircleList();
            if (null != allCircleList && allCircleList.size() > 0) {
                circleList = new ArrayList<CircleEntity>();
                circleList.addAll(allCircleList);

                showCircleList();
            } else {
                fetchCircleList(0);
            }
        }
    }

    @SuppressLint("InflateParams")
    private void initLayout() {
        layout = LayoutInflater.from(getContext()).inflate(R.layout.xlistview_main, null);
        circleListView = findView(layout, R.id.xlistview_main_list_xlv);
        circleListView.setPullRefreshEnable(true);
        circleListView.setXListViewListener(new ListViewUpdateListener());
        circleListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                if (null != circleAdapter) {
                    CircleEntity circle = circleAdapter.getItem(clickIndex);
                    if (null != circle) {
                        TpqApplication.getInstance(getContext()).setShowingCircleEntity(circle);
                        Intent openCircle = new Intent(getContext(), CircleActivity.class);
                        getContext().startActivity(openCircle);
                    }
                }
            }
        });
        addView(layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.bottomMargin = DensityUtil.dip2px(getContext(), 48); // HomeActivity 底部tabbar的高度
        circleListView.setLayoutParams(params);
    }

    class ListViewUpdateListener implements IAutoLoadMoreListViewListener {

        @Override
        public void onRefresh() {
            if (!isRefresh && !isLoadMore) {
                isRefresh = true;
                circleList = null;

                fetchCircleList(0, CommonConstant.MSG_PAGE_SIZE);
            }
        }

        @Override
        public void onLoadMore() {
            if (!isRefresh && !isLoadMore) {
                isLoadMore = true;
                if (null != circleList && circleList.size() > 0) {
                    fetchCircleList(circleList.size(), CommonConstant.MSG_PAGE_SIZE);
                } else {
                    fetchCircleList(0);
                }
            }

        }

    }

    private void fetchCircleList(int offset) {
        Log.i("cth", "fetchCircleList(int offset)");
        Map<String, Object> circleListMap = TpqApplication.getInstance(getContext()).getCircleListMap();
        if (null != circleListMap) {
            toBeContinued = TypeUtil.getInteger(circleListMap.get(CommonConstant.KEY_TO_BE_CONTINUED), 0);
            Object listOj = circleListMap.get(CommonConstant.KEY_DATA_LIST);
            if (null != listOj && listOj instanceof List<?>) {
                circleList = (List<CircleEntity>) listOj;
                if (null != circleList && circleList.size() > 0) {
                    showCircleList();

                    return;
                }
            }
        }
        Log.i("cth", "fetchCircleList(offset, CommonConstant.MSG_PAGE_SIZE);");
        fetchCircleList(offset, CommonConstant.MSG_PAGE_SIZE);
    }

    private void fetchCircleList(int offset, int page_sizes) {
        CommonRequest fetchCircleList = new CommonRequest();
        fetchCircleList.setRequestApiName(InterfaceConstant.API_FORUM_FETCH);
        fetchCircleList.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_FETCH);
        fetchCircleList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchCircleList.addRequestParam(APIKey.COMMON_PAGE_SIZE, page_sizes);
        fetchCircleList.addRequestParam(APIKey.FORUM_STATUS, 1);

        addRequestAsyncTask(fetchCircleList);
    }

    @Override
    protected void onResponseAsyncTaskRender(Map<String, Object> result, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_FORUM_FETCH.equals(requestID)) {
            if (null != result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), -1);
                String message = TypeUtil.getString(result.get(APIKey.COMMON_RESULT));
                if (status == APIKey.STATUS_SUCCESSFUL) {
                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                    if (null != resultMap) {
                        toBeContinued = TypeUtil.getInteger(resultMap.get(APIKey.COMMON_TO_BE_CONTINUED), 0);
                        List<Map<String, Object>> rawCircleList = TypeUtil.getList(resultMap.get(APIKey.FORUMS));
                        if (null != rawCircleList && rawCircleList.size() > 0) {
                            List<CircleEntity> tmpCircleList = EntityUtils.getCircleEntityList(rawCircleList);
                            if (null != tmpCircleList) {
                                if (null == circleList) {
                                    circleList = new ArrayList<CircleEntity>();
                                    circleAdapter = null;
                                }
                                circleList.addAll(tmpCircleList);
                                TpqApplication.getInstance(getContext()).setCircleList(circleList, toBeContinued);
                            }
                        }
                    }

                } else {
                    showToast(message);
                }
            }
            showCircleList();
        }
    }

    private void showCircleList() {
        RelativeLayout loadingRl = findView(layout, R.id.xlistview_main_loading_container_rl);
        loadingRl.setVisibility(View.GONE);
        if (null == circleAdapter) {
            circleAdapter = new CircleAdapter(getContext(), circleList);
            circleListView.setAdapter(circleAdapter);
        } else {
            circleAdapter.notifyDataSetChanged();
        }

        TextView noRecordTv = findView(layout, R.id.xlistview_main_no_record_tv);
        if (null != circleList && circleList.size() > 0) {
            noRecordTv.setVisibility(View.GONE);
            circleListView.setVisibility(View.VISIBLE);
        } else {
            noRecordTv.setVisibility(View.VISIBLE);
            circleListView.setVisibility(View.GONE);
        }

        if (toBeContinued == 1) {
            circleListView.setPullLoadEnable(true);
        } else {
            circleListView.setPullLoadEnable(false);
        }

        isRefresh = false;
        isLoadMore = false;
        circleListView.stopRefresh();
        circleListView.stopLoadMore();
    }

    @Override
    public boolean canListScrollVertically(int direction) {
        return null != circleListView && circleListView.canScrollVertically(direction);
    }
}
