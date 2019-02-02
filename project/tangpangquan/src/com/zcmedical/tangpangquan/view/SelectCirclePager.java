package com.zcmedical.tangpangquan.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zcmedical.common.base.BasePager;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.CircleEntity;

public class SelectCirclePager extends BasePager {

    private OnCircleSelectorListener onCircleSelectorListener;
    private ListView selectCircleListView;
    private List<CircleEntity> allCircleList;

    public SelectCirclePager(Context context) {
        super(context);

        initLayout();
    }

    private void initLayout() {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.view_select_circle, null);

        selectCircleListView = findView(layout, R.id.lv_select_circle);
        selectCircleListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int clickPosition = position - ((ListView) parent).getHeaderViewsCount();
                if (null != allCircleList) {
                    CircleEntity selectCircle = allCircleList.get(clickPosition);
                    if (null != onCircleSelectorListener && selectCircle != null) {
                        onCircleSelectorListener.onCircleSelected(selectCircle);
                    }
                }
            }
        });
        allCircleList = TpqApplication.getInstance(getContext()).getAllCircleList();
        if (null != allCircleList && allCircleList.size() > 0) {
            SelectCirclesAdapter selectCirclesAdapter = new SelectCirclesAdapter(getContext(), allCircleList);
            selectCircleListView.setAdapter(selectCirclesAdapter);
        } else {
            fetchAllCircleList(0);
        }

        addView(layout);
    }

    private void fetchAllCircleList(int offset) {

        CommonRequest fetchCircleList = new CommonRequest();
        fetchCircleList.setRequestApiName(InterfaceConstant.API_FORUM_FETCH);
        fetchCircleList.setRequestID(InterfaceConstant.REQUEST_ID_FORUM_FETCH);
        fetchCircleList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchCircleList.addRequestParam(APIKey.COMMON_PAGE_SIZE, 100);
        fetchCircleList.addRequestParam(APIKey.FORUM_STATUS, 1);

        addRequestAsyncTask(fetchCircleList);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_FORUM_FETCH.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                List<Map<String, Object>> forums = TypeUtil.getList(resultMap.get(APIKey.FORUMS));
                if (null != forums && forums.size() > 0) {
                    allCircleList = EntityUtils.getCircleEntityList(forums);
                    if (null != allCircleList && allCircleList.size() > 0) {
                        TpqApplication.getInstance(getContext()).setAllCircleList(allCircleList);

                        SelectCirclesAdapter selectCirclesAdapter = new SelectCirclesAdapter(getContext(), allCircleList);
                        selectCircleListView.setAdapter(selectCirclesAdapter);
                    }
                }
            }
        }
    }

    public void setOnCircleSelectorListener(OnCircleSelectorListener onCircleSelectorListener) {
        this.onCircleSelectorListener = onCircleSelectorListener;
    }

    public interface OnCircleSelectorListener {
        void onCircleSelected(CircleEntity selectCircle);
    }

    class SelectCirclesAdapter extends CommonListAdapter<CircleEntity> {

        public SelectCirclesAdapter(Context context, List<CircleEntity> dataList) {
            super(context, dataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_select_circle);

            CircleEntity circle = getItem(position);
            if (null != circle) {
                bindItemView(holder, circle);
            }

            return holder.getConvertView();
        }

        private void bindItemView(CommonViewHolder holder, CircleEntity circle) {
            holder.setTextView(R.id.tv_circle_title, circle.getTitle());
        }

    }

}
