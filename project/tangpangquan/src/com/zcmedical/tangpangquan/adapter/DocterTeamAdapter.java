package com.zcmedical.tangpangquan.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.DoctorTeamView;

public class DocterTeamAdapter extends BaseAdapter {

    public static final int ITEM_TITLE = 0;
    public static final int ITEM_CONTENT = 1;
    private static final int TYPE_MAX_COUNT = ITEM_CONTENT + 1;

    private List<DoctorTeamView> doctorTeamViews;
    private Context context;

    private LayoutInflater mInflater;

    public DocterTeamAdapter(Context context, List<DoctorTeamView> doctorTeamViews) {
        this.context = context;
        this.doctorTeamViews = doctorTeamViews;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (doctorTeamViews != null && doctorTeamViews.get(position) != null) {
            return doctorTeamViews.get(position).getItem_type();
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return doctorTeamViews.size();
    }

    @Override
    public Object getItem(int position) {
        return doctorTeamViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
            case ITEM_TITLE:
                convertView = mInflater.inflate(R.layout.layout_doctor_title, null);
                holder.tvTeam = (TextView) convertView.findViewById(R.id.tvTeam);
                break;
            case ITEM_CONTENT:
                convertView = mInflater.inflate(R.layout.layout_doctor_item, null);
                holder.ivHead = (ImageView) convertView.findViewById(R.id.ivHead);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvLevel = (TextView) convertView.findViewById(R.id.tvLevel);
                holder.tvHospital = (TextView) convertView.findViewById(R.id.tvHospital);
                holder.tvScore = (TextView) convertView.findViewById(R.id.tvScore);
                holder.tvClass = (TextView) convertView.findViewById(R.id.tvClass);
                holder.tvGood = (TextView) convertView.findViewById(R.id.tvGood);
                break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (type) {
        case ITEM_TITLE:
            holder.tvTeam.setText(doctorTeamViews.get(position).getTitle());
            break;
        case ITEM_CONTENT:
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getHead_pic())) {
                Picasso.with(context).load(doctorTeamViews.get(position).getDoctor().getHead_pic()).into(holder.ivHead);
            }else{
                holder.ivHead.setImageResource(R.drawable.default_avatar);
            }
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getNickname())) {
                holder.tvName.setText(doctorTeamViews.get(position).getDoctor().getNickname());
            }
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getJob_title())) {
                holder.tvLevel.setText(doctorTeamViews.get(position).getDoctor().getJob_title());
            }
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getHospital())) {
                holder.tvHospital.setText(doctorTeamViews.get(position).getDoctor().getHospital());
            }
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getRecommended())) {
                holder.tvScore.setText(doctorTeamViews.get(position).getDoctor().getRecommended());
            }
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getDepartment())) {
                holder.tvClass.setText(doctorTeamViews.get(position).getDoctor().getDepartment());
            }
            if (!TextUtils.isEmpty(doctorTeamViews.get(position).getDoctor().getSkill())) {
                holder.tvGood.setText(doctorTeamViews.get(position).getDoctor().getSkill());
            }
            break;
        default:
            break;
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView ivHead;
        TextView tvName;
        TextView tvLevel;
        TextView tvHospital;
        TextView tvScore;
        TextView tvClass;
        TextView tvGood;
        ////////title//////////
        TextView tvTeam;
    }

}
