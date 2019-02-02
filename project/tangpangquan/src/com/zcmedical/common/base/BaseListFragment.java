package com.zcmedical.common.base;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zcmedical.common.component.AutoLoadMoreListView;
import com.zcmedical.common.component.AutoLoadMoreListView.IAutoLoadMoreListViewListener;
import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.view.BackToTopBtn;

public abstract class BaseListFragment<A extends CommonListAdapter<E>, E> extends BaseFragment {

    protected int toBeContinued = 0;
    protected A adapter;
    protected List<E> dataList;

    protected View contentView;
    protected AutoLoadMoreListView listView;

    protected boolean isLoadingMore = false;
    protected boolean isRefreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.xlistview_main, null);

        init();

        return contentView;
    }

    protected void init() {
        initUI();
        fetchDataList(0);
    }

    @SuppressLint("InflateParams")
    private void initUI() {
        listView = findView(contentView, R.id.xlistview_main_list_xlv);
        listView.setPullRefreshEnable(true);
        listView.setXListViewListener(new ListViewUpdateListener());
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickPosition = position - ((ListView) parent).getHeaderViewsCount();
                if (null != adapter && dataList != null && clickPosition < dataList.size()) {
                    E entity = adapter.getItem(clickPosition);
                    if (null != entity) {
                        dealItemClick(clickPosition, entity);
                    }
                }
            }
        });
        final BackToTopBtn backToTopButton = findView(contentView, R.id.btn_back_to_top);
        backToTopButton.bindListView(listView);
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                backToTopButton.onVisibilityChanged(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    class ListViewUpdateListener implements IAutoLoadMoreListViewListener {

        @Override
        public void onRefresh() {
            if (!isRefreshing && !isLoadingMore) {
                isRefreshing = true;
                dataList = null;

                fetchDataList(0, CommonConstant.MSG_PAGE_SIZE);
            }
        }

        @Override
        public void onLoadMore() {
            if (!isRefreshing && !isLoadingMore) {
                isLoadingMore = true;
                if (null != dataList && dataList.size() > 0) {
                    fetchDataList(dataList.size(), CommonConstant.MSG_PAGE_SIZE);
                } else {
                    fetchDataList(0);
                }
            }

        }

    }

    protected void fetchDataList(int offset) {
        fetchDataList(offset, CommonConstant.MSG_PAGE_SIZE);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        this.toBeContinued = toBeContinued;
        onResponse(status, message, resultMap, requestID, additionalArgsMap);
    }

    protected void showDataList() {
        RelativeLayout loadingRl = findView(contentView, R.id.xlistview_main_loading_container_rl);
        loadingRl.setVisibility(View.GONE);
        if (null == adapter) {
            adapter = initAdapter(getActivity(), dataList);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        TextView noRecordTv = findView(contentView, R.id.xlistview_main_no_record_tv);
        if (null != dataList && dataList.size() > 0) {
            noRecordTv.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            noRecordTv.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        setListCanLoadMore();

        isRefreshing = false;
        isLoadingMore = false;
        listView.stopRefresh();
        listView.stopLoadMore();
    }

    protected void setNoDataTextTip(String text) {
        TextView noRecordTv = findView(contentView, R.id.xlistview_main_no_record_tv);
        noRecordTv.setText(text);
    }

    private void setListCanLoadMore() {
        if (toBeContinued == 1) {
            listView.setPullLoadEnable(true);
        } else {
            listView.setPullLoadEnable(false);
        }
    }

    protected abstract void dealItemClick(int clickPosition, E entity);

    protected abstract void fetchDataList(int offset, int page_sizes);

    protected abstract void onResponse(int status, String message, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap);

    protected abstract A initAdapter(Context context, List<E> dataList);
}
