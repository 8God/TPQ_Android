package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zcmedical.common.component.CommonListAdapter;
import com.zcmedical.common.component.CommonViewHolder;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.PlanDetailEntity;

public class PlanDetailsAdapter extends CommonListAdapter<PlanDetailEntity> {

    public PlanDetailsAdapter(Context context, List<PlanDetailEntity> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder holder = CommonViewHolder.getViewHolder(context, position, convertView, parent, R.layout.item_plan_detail);

        PlanDetailEntity planDetailEntity = getItem(position);
        if (null != planDetailEntity) {
            bindItemView(holder, planDetailEntity);
        }

        return holder.getConvertView();
    }

    private void bindItemView(CommonViewHolder holder, PlanDetailEntity planDetailEntity) {
        holder.setTextView(R.id.tv_plan_day, planDetailEntity.getDay() + "");
        holder.setTextView(R.id.tv_plan_content, planDetailEntity.getContent());
    }

}
