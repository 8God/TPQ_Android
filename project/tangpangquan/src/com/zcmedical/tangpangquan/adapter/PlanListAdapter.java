package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.PlanEntity;

public class PlanListAdapter extends CommonListAdapter<PlanEntity> {

    public PlanListAdapter(Context context, List<PlanEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_plan);

        PlanEntity plan = getItem(position);
        if (null != plan) {
            bindItemView(plan, holder);
        }
        return holder.getConvertView();
    }

    private void bindItemView(PlanEntity plan, CommonViewHolder holder) {
        holder.setTextView(R.id.tv_plan_title, plan.getTitle());
    }

}
