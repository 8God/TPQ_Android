package com.zcmedical.tangpangquan.activity;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.db.DbUtils;
import com.zcmedical.common.db.Weight;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.view.ProgressView;

public class WeightMainActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "WeightMainActivity";
    private TextView tvbmi;
    private TextView tvb;
    private RelativeLayout rlOcuppy;
    private ProgressView pvView;
    private Toolbar toolbar;
    private TextView tvEdit;
    private TextView tvDw;
    private TextView tvTargetWeight;
    private TextView tvCurrentWeight;
    private TextView tvSuggestWeight;
    private TextView tvCurrentHeight;
    private RelativeLayout rvRecord;
    private List<Weight> weightList;
    private Weight currentWeight;
    private com.github.mikephil.charting.charts.LineChart lineChart;
    private LineData mLineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_mai);
        mDbUtils = DbUtils.getInstance(this);
        initToolbar();
        initUI();
    }

    private void initUI() {
        lineChart = findView(R.id.lineChart);
        tvEdit = findView(R.id.tvEdit);
        tvDw = findView(R.id.tvDw);
        tvTargetWeight = findView(R.id.tvTargetWeight);
        tvCurrentWeight = findView(R.id.tvCurrentWeight);
        tvSuggestWeight = findView(R.id.tvSuggestWeight);
        tvCurrentHeight = findView(R.id.tvCurrentHeight);
        rvRecord = findView(R.id.rvRecord);
        tvb = findView(R.id.tvb);
        tvbmi = findView(R.id.tvbmi);
        pvView = findView(R.id.pvView);
        rlOcuppy = findView(R.id.rlOcuppy);
        rvRecord.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
    }

    private void dependWeightDataChangeUI() {
        int height = spf.getInt("height", 0);
        int target_weight = spf.getInt("target_weight", 0);
        int suggestWeightMin = 0, suggestWeightMax = 0;
        if (height != 0) {
            suggestWeightMin = (int) (18.5 * height * height / 10000);
            suggestWeightMax = (int) (24.9 * height * height / 10000);
        }
        weightList = mDbUtils.getAllWeight();
        if (BuildConfig.DEBUG) {
            for (Weight i : weightList) {
                Log.d("@@", i.toString());
            }
        }
        //设置UI
        tvCurrentHeight.setText(height + "");
        tvSuggestWeight.setText(suggestWeightMin + "~" + suggestWeightMax);
        tvTargetWeight.setText(target_weight + "");
        if (weightList != null && weightList.size() > 0) {
            currentWeight = weightList.get(weightList.size() - 1);
            tvDw.setText((currentWeight.getWeight() - target_weight) + "kg");
            tvCurrentWeight.setText("" + currentWeight.getWeight());
        }
        Log.d(TAG, "  height : " + height + "  target_weight * " + target_weight);
        if (currentWeight != null && height > 10 && currentWeight.getWeight() > 10) {
            float bmi =  (float) (Math.round(((float) ((currentWeight.getWeight() / (float) (height * height / 10000.0f)))) * 10) * 0.1);
            Log.d(TAG, "home.bmi.0 : " + bmi + "home.weight : " + currentWeight.toString());
            spf.edit().putFloat("bmi", bmi);
            dependBmiChangeUI(bmi);
            tvbmi.setText("" + bmi);
        } else {
            Log.d(TAG, "home.weight.1 : ");
            float historyBMI = TpqApplication.sharedPreferences.getFloat("bmi", 0);
            tvbmi.setText(historyBMI + "");
            dependBmiChangeUI(historyBMI);
        }
    }

    private void dependBmiChangeUI(float progress) {
        pvView.setData(this, progress);
        pvView.bringToFront();
        int color = Color.parseColor("#29B6F6");
        if (progress <= 18.5f) {
            tvb.setText("当前BMI\n偏瘦体重");
            color = Color.parseColor("#29B6F6");
        } else if (progress <= 25.0f && progress > 18.5) {
            tvb.setText("当前BMI\n健康体重");
            color = Color.parseColor("#37DCA2");
        } else if (progress <= 30.0f && progress > 25.0f) {
            tvb.setText("当前BMI\n偏胖体重");
            color = Color.parseColor("#FFD600");
        } else if (progress <= 35.0f && progress > 30.0f) {
            tvb.setText("当前BMI\n一级肥胖");
            color = Color.parseColor("#FFA000");
        } else if (progress <= 40.0f && progress > 35.0f) {
            tvb.setText("当前BMI\n二级肥胖");
            color = Color.parseColor("#FF6181");
        } else if (progress > 40.0f) {
            tvb.setText("当前BMI\n三级肥胖");
            color = Color.parseColor("#C2185B");
        }
        rlOcuppy.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
        tvbmi.setText("" + progress);
    }

    private void initToolbar() {
        toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setTitle("BMI");
        }
    }

    @Override
    protected void onResume() {
        dependWeightDataChangeUI();
        mLineData = getLineData();
        showChart(lineChart, mLineData, Color.WHITE);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rvRecord:
            startActivity(new Intent(this, RecordWeightActivity.class).putExtra("isTodayWeight", true));
            break;

        case R.id.tvEdit:
            startActivity(new Intent(this, EditHeightAndTargetWeightActivity.class));

        default:
            break;
        }
    }

    private void showChart(LineChart lineChart, LineData lineData, int color) {
        lineChart.setDrawBorders(false); //是否在折线图上添加边框    

        // no description text    
        lineChart.setDescription("");// 数据描述    
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview    
        lineChart.setNoDataTextDescription("您最近还没有记录过体重数据~");

        // enable / disable grid background    
        lineChart.setDrawGridBackground(true); // 是否显示表格颜色    
        lineChart.setGridBackgroundColor(Color.WHITE); // 表格的的颜色，在这Color.parseColor("#dcdcdc")里是是给颜色设置一个透明度    

        // enable touch gestures    
        lineChart.setTouchEnabled(true); // 设置是否可以触摸    

        // enable scaling and dragging    
        lineChart.setDragEnabled(true);// 是否可以拖拽    
        lineChart.setScaleEnabled(true);// 是否可以缩放    

        // if disabled, scaling can be done on x- and y-axis separately    
        lineChart.setPinchZoom(false);//     

        lineChart.setBackgroundColor(color);// 设置背景    

        // add data    
        lineChart.setData(lineData); // 设置数据    

        // get the legend (only possible after setting data)    
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的    
        lineChart.getXAxis().setPosition(XAxisPosition.BOTTOM);
        //mLegend.setTextColor(Color.parseColor("#dcdcdc"));
        // modify the legend ...    
        //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);  
        //mLegend.setForm(LegendForm.LINE);// 样式    
        //mLegend.setFormSize(6f);// 字体    
        //mLegend.setTextColor(Color.WHITE);// 颜色    
        //mLegend.setTypeface(mTf);// 字体    

        lineChart.animateX(2500); // 立即执行的动画,x轴    
    }

    private static final int XAXIS_LENGTH = 12;

    private ArrayList<String> getXAxis() {
        DateTime dtToday = DateTime.today(TimeZone.getDefault());
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 1; i <= XAXIS_LENGTH; i++) {
            xValues.add(dtToday.minusDays(XAXIS_LENGTH - i).format("MM/DD"));
        }
        if (BuildConfig.DEBUG) {
            for (String string : xValues) {
                Log.d(TAG, "getXAxis : " + string);
            }
        }
        return xValues;
    }

    private ArrayList<Entry> getYValues() {
        DateTime dtToday = DateTime.today(TimeZone.getDefault());
        ArrayList<Entry> yValues = new ArrayList<Entry>();
        for (int i = 1; i <= XAXIS_LENGTH; i++) {
            Weight weight = mDbUtils.queryOneWeight(dtToday.minusDays(XAXIS_LENGTH - i).format("YYYYMMDDhhmmss"));
            float value = 0;
            if (weight != null) {
                value = weight.getWeight();
            }
            yValues.add(new Entry(value, i - 1));
        }
        if (BuildConfig.DEBUG) {
            for (Entry entry : yValues) {
                Log.d(TAG, "getYValues : " + entry.getXIndex() + "  v:" + entry.getVal());
            }
        }
        return yValues;
    }

    private LineData getLineData() {
        LineDataSet lineDataSet = new LineDataSet(getYValues(), "近期12天的体重数据" /*显示在比例图上*/);
        // mLineDataSet.setFillAlpha(110);    
        // mLineDataSet.setFillColor(Color.RED);    

        lineDataSet.setLineWidth(DensityUtil.dip2px(this, 3)); // 线宽    
        lineDataSet.setCircleSize(6f);// 显示的圆形大小    
        lineDataSet.setColor(Color.parseColor("#ff5400"));// 显示颜色    
        lineDataSet.setCircleColor(Color.parseColor("#ff5400"));// 圆形的颜色    
        lineDataSet.setCircleColorHole(Color.parseColor("#ff5400"));
        lineDataSet.setHighLightColor(Color.parseColor("#dcdcdc")); // 高亮的线的颜色    

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets    

        LineData lineData = new LineData(getXAxis(), lineDataSets);

        return lineData;
    }
}
