package com.zcmedical.tangpangquan.activity;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.db.BloodSugar;
import com.zcmedical.common.db.DbUtils;
import com.zcmedical.common.utils.DateUtils;
import com.zcmedical.common.utils.DensityUtil;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;

public class BloodSugarActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

    private final static String TAG = "BloodSugarActivity";
    ViewPager viewpager;
    com.github.mikephil.charting.charts.LineChart lineChart;
    private int meal_type = 0;
    public static final int MEAL_1_BEFORE = 0;//早餐前
    public static final int MEAL_1_AFTER = 1;//早餐后
    public static final int MEAL_2_BEFORE = 2;//午餐前
    public static final int MEAL_2_AFTER = 3;//午餐后
    public static final int MEAL_3_BEFORE = 4;//晚餐前
    public static final int MEAL_3_AFTER = 5;//晚餐后
    public static final int MEAL_4_BEFORE = 6;//其他前
    public static final int MEAL_4_AFTER = 7;//其他后

    private List<BloodSugar> bloodSugars;
    private List<View> views = new ArrayList<View>();
    private DateTime currentDT;
    private MyAdapter adapter;
    private ImageView imageView;
    private ImageView[] imageViews;
    private LinearLayout llPoint;
    private Bundle state;
    private LineData mLineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_sugar);
        mDbUtils = DbUtils.getInstance(this);
        state = savedInstanceState;
        currentDT = DateTime.today(TimeZone.getDefault());
        initUI();
        initToolbar();
    }

    private void getBsDataByDateTime(DateTime dt) {
        bloodSugars = mDbUtils.queryOneDayBloodSugar(DateUtils.dateTime2HoleDayTimestamp(dt));
        Collections.sort(bloodSugars, new Comparator<BloodSugar>() {

            @Override
            public int compare(BloodSugar lhs, BloodSugar rhs) {
                if (lhs.getMeasure_time() > rhs.getMeasure_time()) {
                    return 1;
                } else if (lhs.getMeasure_time() < rhs.getMeasure_time()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (BloodSugar i : bloodSugars) {
            Log.d(TAG, "i: " + i.getBlood_sugar());
        }
    }

    private void initUI() {
        viewpager = findView(R.id.viewpager);
        lineChart = findView(R.id.lineChart);
        llPoint = findView(R.id.llPoint);
        for (int i = 0; i <= 3; i++) {
            views.add(getLayoutInflater().from(this).inflate(R.layout.item_blood_sugar, null));
        }

        adapter = new MyAdapter();
        viewpager.setAdapter(adapter);
        viewpager.setOnPageChangeListener(this);

        imageViews = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            imageView = new ImageView(BloodSugarActivity.this);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setMargins(20, 0, 0, 0);
            imageView.setLayoutParams(layout);
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i].setBackgroundResource(R.drawable.common_img_screenpoint_pre);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.common_img_screenpoint_nor);
            }
            llPoint.addView(imageViews[i]);
        }
    }

    private void changePointByScroll(int arg0) {
        for (int i = 0; i < imageViews.length; i++) {
            if (i == arg0) {
                imageViews[i].setBackgroundResource(R.drawable.common_img_screenpoint_pre);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.common_img_screenpoint_nor);
            }
        }
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
            actionBar.setTitle("血糖");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blood_sugar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_rili:
            showCaldroid();
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private CaldroidFragment dialogCaldroidFragment;
    //final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

    private void showCaldroid() {

        dialogCaldroidFragment = new CaldroidFragment();
        dialogCaldroidFragment.setCaldroidListener(listener);

        final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
        if (state != null) {
            dialogCaldroidFragment.restoreDialogStatesFromKey(getSupportFragmentManager(), state, "DIALOG_CALDROID_SAVED_STATE", dialogTag);
            Bundle args = dialogCaldroidFragment.getArguments();
            if (args == null) {
                args = new Bundle();
                dialogCaldroidFragment.setArguments(args);
            }
            args.putString(CaldroidFragment.DIALOG_TITLE, "请选择您要查看的日期");
        } else {
            // Setup arguments
            Bundle bundle = new Bundle();
            // Setup dialogTitle
            bundle.putString(CaldroidFragment.DIALOG_TITLE, "请选择您要查看的日期");
            dialogCaldroidFragment.setArguments(bundle);
        }

        dialogCaldroidFragment.show(getSupportFragmentManager(), dialogTag);

    }

    // Setup listener
    final CaldroidListener listener = new CaldroidListener() {

        @Override
        public void onSelectDate(Date date, View view) {
            //Toast.makeText(getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
            currentDT = DateUtils.timestamp2DateTime(date.getTime() / 1000);
            getBsDataByDateTime(currentDT);
            adapter.notifyDataSetChanged();
            mLineData = getLineData();
            showChart(lineChart, mLineData, Color.WHITE);
            dialogCaldroidFragment.dismiss();
        }

        @Override
        public void onChangeMonth(int month, int year) {
            //String text = "month: " + month + " year: " + year;
            //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLongClickDate(Date date, View view) {
            //Toast.makeText(getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCaldroidViewCreated() {

        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        getBsDataByDateTime(currentDT);
        adapter.notifyDataSetChanged();
        mLineData = getLineData();
        showChart(lineChart, mLineData, Color.WHITE);
    }

    private void showChart(LineChart lineChart, LineData lineData, int color) {
        lineChart.setDrawBorders(false); //是否在折线图上添加边框    
        lineChart.setDescription("");// 数据描述    
        lineChart.setNoDataTextDescription("当天没有记录数据~");
        lineChart.setDrawGridBackground(true); // 是否显示表格颜色    
        lineChart.setGridBackgroundColor(Color.WHITE); // 表格的的颜色，在这里是是给颜色设置一个透明度    
        lineChart.setTouchEnabled(true); // 设置是否可以触摸    
        lineChart.setDragEnabled(true);// 是否可以拖拽    
        lineChart.setScaleEnabled(true);// 是否可以缩放    
        lineChart.setPinchZoom(false);//     
        lineChart.setBackgroundColor(color);// 设置背景    
        lineChart.setData(lineData); // 设置数据    
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的    
        lineChart.getXAxis().setPosition(XAxisPosition.BOTTOM);
        lineChart.animateX(2500); // 立即执行的动画,x轴    
    }

    private LineData getLineData() {
        ArrayList<String> xValues = getXAxis();
        LineDataSet lineDataSet = new LineDataSet(getYValues(xValues), "一天内的血糖曲线");
        lineDataSet.setLineWidth(DensityUtil.dip2px(this, 3)); // 线宽    
        lineDataSet.setCircleSize(6f);// 显示的圆形大小    
        lineDataSet.setColor(Color.parseColor("#ff5400"));// 显示颜色    
        lineDataSet.setCircleColor(Color.parseColor("#ff5400"));// 圆形的颜色    
        lineDataSet.setCircleColorHole(Color.parseColor("#ff5400"));
        lineDataSet.setHighLightColor(Color.parseColor("#dcdcdc")); // 高亮的线的颜色    
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets    
        LineData lineData = new LineData(xValues, lineDataSets);
        return lineData;
    }

    private static final int XAXIS_LENGTH = 60 * 24;

    private ArrayList<String> getXAxis() {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < XAXIS_LENGTH; i++) {
            xValues.add(((i / 60) > 9 ? i / 60 : "0" + i / 60) + ":" + ((i % 60) > 9 ? i % 60 : "0" + i % 60));
        }
        return xValues;
    }

    private ArrayList<Entry> getYValues(ArrayList<String> xValues) {
        ArrayList<Entry> yValues = new ArrayList<Entry>();

        for (BloodSugar bs : bloodSugars) {
            String key = DateUtils.timestamp2DateTime(bs.getMeasure_time()).format("hh:mm");
            int index = xValues.indexOf(key);
            Log.d(TAG, "key : " + key + "  index : " + index);
            yValues.add(new Entry(bs.getBlood_sugar(), index));
        }
        if (BuildConfig.DEBUG) {
            for (Entry entry : yValues) {
                Log.d(TAG, "getYValues : " + entry.getXIndex() + "  v:" + entry.getVal());
            }
        }
        return yValues;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    private BloodSugar getBsByType(int type) {
        if (bloodSugars == null || bloodSugars.size() == 0) {
            return null;
        }
        for (BloodSugar bs : bloodSugars) {
            if (bs.getMeal_type() == type) {
                return bs;
            }
        }
        return null;
    }

    public class MyAdapter extends PagerAdapter {
        private TextView tvDate;
        private TextView tvMeal;
        private TextView tvBeforeData;
        private ImageView ivBeforeAdd;
        private TextView tvAfterData;
        private ImageView ivAfterAdd;
        private RelativeLayout rlMain;
        private BloodSugar beforeBs;
        private BloodSugar afterBs;

        private int mChildCount = 0;

        @Override
        public void notifyDataSetChanged() {
            mChildCount = getCount();
            super.notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE;
            }
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));

        }

        @Override
        public Object instantiateItem(View container, int position) {
            View view = views.get(position);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvMeal = (TextView) view.findViewById(R.id.tvMeal);
            tvBeforeData = (TextView) view.findViewById(R.id.tvBeforeData);
            ivBeforeAdd = (ImageView) view.findViewById(R.id.ivBeforeAdd);
            tvAfterData = (TextView) view.findViewById(R.id.tvAfterData);
            ivAfterAdd = (ImageView) view.findViewById(R.id.ivAfterAdd);
            tvDate.setText(currentDT.format("YYYY年MM月DD日"));
            ivBeforeAdd.setOnClickListener(new ClickListener(position));
            ivAfterAdd.setOnClickListener(new ClickListener(position));
            switch (position) {
            case 0:
                tvMeal.setText("早餐");
                rlMain.setBackgroundColor(getResources().getColor(R.color.meal1));
                beforeBs = getBsByType(MEAL_1_BEFORE);
                afterBs = getBsByType(MEAL_1_AFTER);
                break;
            case 1:
                rlMain.setBackgroundColor(getResources().getColor(R.color.meal2));
                tvMeal.setText("午餐");
                beforeBs = getBsByType(MEAL_2_BEFORE);
                afterBs = getBsByType(MEAL_2_AFTER);
                break;
            case 2:
                rlMain.setBackgroundColor(getResources().getColor(R.color.meal3));
                tvMeal.setText("晚餐");
                beforeBs = getBsByType(MEAL_3_BEFORE);
                afterBs = getBsByType(MEAL_3_AFTER);
                break;
            case 3:
                rlMain.setBackgroundColor(getResources().getColor(R.color.meal4));
                tvMeal.setText("其他");
                beforeBs = getBsByType(MEAL_4_BEFORE);
                afterBs = getBsByType(MEAL_4_AFTER);
                break;
            default:
                break;
            }
            tvBeforeData.setVisibility(beforeBs == null ? View.GONE : View.VISIBLE);
            tvBeforeData.setText(beforeBs == null ? "" : beforeBs.getBlood_sugar() + "");
            ivBeforeAdd.setVisibility(beforeBs == null ? View.VISIBLE : View.GONE);
            tvAfterData.setVisibility(afterBs == null ? View.GONE : View.VISIBLE);
            tvAfterData.setText(afterBs == null ? "" : afterBs.getBlood_sugar() + "");
            ivAfterAdd.setVisibility(afterBs == null ? View.VISIBLE : View.GONE);
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        private class ClickListener implements OnClickListener {

            private int position;

            private ClickListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.ivBeforeAdd:
                    switch (position) {
                    case 0:
                        meal_type = MEAL_1_BEFORE;
                        break;
                    case 1:
                        meal_type = MEAL_2_BEFORE;
                        break;
                    case 2:
                        meal_type = MEAL_3_BEFORE;
                        break;
                    case 3:
                        meal_type = MEAL_4_BEFORE;
                        break;
                    default:
                        break;
                    }
                    break;
                case R.id.ivAfterAdd:
                    switch (position) {
                    case 0:
                        meal_type = MEAL_1_AFTER;
                        break;
                    case 1:
                        meal_type = MEAL_2_AFTER;
                        break;
                    case 2:
                        meal_type = MEAL_3_AFTER;
                        break;
                    case 3:
                        meal_type = MEAL_4_AFTER;
                        break;
                    default:
                        break;
                    }
                    break;
                default:
                    break;
                }
                startActivity(new Intent(BloodSugarActivity.this, BloodSugarAddActivity.class).putExtra("timestamp", DateUtils.dateTime2Timestamp(currentDT)).putExtra("meal_type", meal_type));
            }
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        changePointByScroll(arg0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState, "DIALOG_CALDROID_SAVED_STATE");
        }
    }

}
