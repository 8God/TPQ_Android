package com.zcmedical.tangpangquan.fragment;

import hirondelle.date4j.DateTime;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.zcmedical.common.base.BaseFragment;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.db.DbUtils;
import com.zcmedical.common.db.Weight;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.common.utils.NotifyUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.BloodSugarActivity;
import com.zcmedical.tangpangquan.activity.ChatActivity;
import com.zcmedical.tangpangquan.activity.MedicalRecordActivity;
import com.zcmedical.tangpangquan.activity.HomeActivity;
import com.zcmedical.tangpangquan.activity.PlanActivity;
import com.zcmedical.tangpangquan.activity.PostsDetailActivity;
import com.zcmedical.tangpangquan.activity.RecordHeightActivity;
import com.zcmedical.tangpangquan.activity.RecordWeightActivity;
import com.zcmedical.tangpangquan.activity.WebViewActivity;
import com.zcmedical.tangpangquan.activity.WeightMainActivity;
import com.zcmedical.tangpangquan.adapter.HotPostsAdapter;
import com.zcmedical.tangpangquan.entity.Doctor;
import com.zcmedical.tangpangquan.entity.DoctorTeam;
import com.zcmedical.tangpangquan.entity.DoctorTeamView;
import com.zcmedical.tangpangquan.entity.Meals;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.push.AlarmReceiver;
import com.zcmedical.tangpangquan.view.ProgressView;
import com.zcmedical.tangpangquan.view.RoundedImageView;

public class HomeFragment extends BaseFragment implements OnClickListener {

    private final static String TAG = "HomeFragment";

    private View contentView;
    private TextView tvFunction1;//血糖
    private TextView tvFunction2;//体检记录
    private TextView tvFunction3;//计划
    private TextView tvFunction4;//知识课堂
    private TextView tvFunction5;//奇趣
    private TextView tvFunction6;//故事

    private RelativeLayout rlOcuppy;//有数据时的顶部布局
    private RelativeLayout rlNone;//没有数据时的顶部布局

    //名医推荐
    private RoundedImageView rvHead1;
    private RoundedImageView rvHead2;
    private RoundedImageView rvHead3;
    private RoundedImageView rvHead4;
    private RoundedImageView rvHead5;

    private TextView tvName1;
    private TextView tvName2;
    private TextView tvName3;
    private TextView tvName4;
    private TextView tvName5;

    //今日推荐更多
    private TextView tvMore;

    private ImageView ivToday1;
    private ImageView ivToday2;
    private ImageView ivToday3;

    //热帖推荐
    private TextView tvPostMore;//更多

    //记录BMI
    private Button btnRecord;

    private ImageView ivCheck1;
    private ImageView ivCheck2;
    private ImageView ivRc;
    private TextView tvbmi;
    private TextView tvb;

    private ProgressView pvView;

    private DbUtils mDbUtils;

    private AsyncHttpClient client;

    private RoundedImageView[] rvHeads;
    private TextView[] tvNames;

    private AsyncHttpClient clientRecommend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        spf = getActivity().getSharedPreferences(TpqApplication.class.getName(), 0);
        client = new AsyncHttpClient();
        clientRecommend = new AsyncHttpClient();
        mDbUtils = DbUtils.getInstance(getActivity());
        initUI(contentView);
        initNotify();
        return contentView;
    }

    private void initUI(View view) {
        tvb = findView(view, R.id.tvb);
        tvbmi = findView(view, R.id.tvbmi);
        pvView = findView(view, R.id.pvView);
        ivCheck1 = findView(view, R.id.ivCheck1);
        ivCheck2 = findView(view, R.id.ivCheck2);
        ivRc = findView(view, R.id.ivRc);

        tvFunction1 = findView(view, R.id.tvFunction1);
        tvFunction2 = findView(view, R.id.tvFunction2);
        tvFunction3 = findView(view, R.id.tvFunction3);
        tvFunction4 = findView(view, R.id.tvFunction4);
        tvFunction5 = findView(view, R.id.tvFunction5);
        tvFunction6 = findView(view, R.id.tvFunction6);

        rlOcuppy = findView(view, R.id.rlOcuppy);
        rlNone = findView(view, R.id.rlNone);

        rvHead1 = findView(view, R.id.rvHead1);
        rvHead2 = findView(view, R.id.rvHead2);
        rvHead3 = findView(view, R.id.rvHead3);
        rvHead4 = findView(view, R.id.rvHead4);
        rvHead5 = findView(view, R.id.rvHead5);
        tvName1 = findView(view, R.id.tvName1);
        tvName2 = findView(view, R.id.tvName2);
        tvName3 = findView(view, R.id.tvName3);
        tvName4 = findView(view, R.id.tvName4);
        tvName5 = findView(view, R.id.tvName5);

        tvMore = findView(view, R.id.tvMore);
        ivToday1 = findView(view, R.id.ivToday1);
        ivToday2 = findView(view, R.id.ivToday2);
        ivToday3 = findView(view, R.id.ivToday3);

        tvPostMore = findView(view, R.id.tvPostMore);

        btnRecord = findView(view, R.id.btnRecord);

        tvMore.setOnClickListener(this);
        tvPostMore.setOnClickListener(this);
        tvFunction1.setOnClickListener(this);
        tvFunction2.setOnClickListener(this);
        tvFunction3.setOnClickListener(this);
        tvFunction4.setOnClickListener(this);
        tvFunction5.setOnClickListener(this);
        tvFunction6.setOnClickListener(this);

        btnRecord.setOnClickListener(this);

        ivCheck1.setOnClickListener(this);
        ivCheck2.setOnClickListener(this);
        ivRc.setOnClickListener(this);

        fetchPostsList(0, 10);

        //        ivToday1.setImageResource(R.drawable.tuijian01);
        //        ivToday2.setImageResource(R.drawable.tuijian02);
        //        ivToday3.setImageResource(R.drawable.tuijian03);
        //        ivToday1.setOnClickListener(this);
        //        ivToday2.setOnClickListener(this);
        //        ivToday3.setOnClickListener(this);

        //        rvHead1.setImageResource(R.drawable.ystouxiang);
        //        tvName1.setText("魏子君医生");
        //        rvHead1.setOnClickListener(this);
        //        rvHead2.setOnClickListener(this);
        //        rvHead3.setOnClickListener(this);
        //        rvHead4.setOnClickListener(this);
        //        rvHead5.setOnClickListener(this);
        rvHead1.setVisibility(View.GONE);
        rvHead1.setVisibility(View.GONE);
        rvHead2.setVisibility(View.GONE);
        rvHead3.setVisibility(View.GONE);
        rvHead4.setVisibility(View.GONE);
        rvHead5.setVisibility(View.GONE);
        tvName2.setVisibility(View.GONE);
        tvName3.setVisibility(View.GONE);
        tvName4.setVisibility(View.GONE);
        tvName5.setVisibility(View.GONE);
        rvHeads = new RoundedImageView[] { rvHead1, rvHead2, rvHead3, rvHead4, rvHead5 };
        tvNames = new TextView[] { tvName1, tvName2, tvName3, tvName4, tvName5 };
        setDoctorRecommendUI();
        getTodayRecommend();
    }

    @Override
    public void onResume() {
        dependWeightDataChangeUI();
        super.onResume();
    }

    private void initNotify() {
        NotifyUtil.setTaskAlarm(getActivity(), CommonConstant.REMIND_WEIGHT, spf.getBoolean("mPushSlideSwitchView1", !AlarmReceiver.DEFAULT_STATUS));
        for (int i = 1; i <= 8; i++) {
            String key = "Remind_Bs_Time_" + i;
            int taskid = CommonConstant.REMIND_BLOOD_SUGAR_1 + i - 1;
            NotifyUtil.setTaskAlarm(getActivity(), taskid, spf.getBoolean("mPushSlideSwitchView1", !AlarmReceiver.DEFAULT_STATUS) ? spf.getBoolean(key, !AlarmReceiver.DEFAULT_STATUS) : false);
        }
        NotifyUtil.setTaskAlarm(getActivity(), CommonConstant.REMIND_EVERYDAY, spf.getBoolean("mPushSlideSwitchView3", !AlarmReceiver.DEFAULT_STATUS));
    }

    private void setDoctorRecommendUI() {
        RequestParams params = new RequestParams();
        params.add("is_recommended", "1");
        client.post(InterfaceConstant.DOCTOR_FETCH, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "DoctorJsonHttpResponseHandler.re : " + response.toString());
                Type listType = new TypeToken<LinkedList<Doctor>>() {
                }.getType();
                Gson gson = new Gson();
                final LinkedList<Doctor> doctors = gson.fromJson((JsonUtils.getOjectString((response.toString()), "doctors")), listType);
                if (doctors.size() > 0) {
                    for (int i = 0; i < doctors.size(); i++) {
                        final Doctor doctor = doctors.get(i);
                        tvNames[i].setVisibility(View.VISIBLE);
                        rvHeads[i].setVisibility(View.VISIBLE);
                        tvNames[i].setText(doctor.getNickname());
                        if (!TextUtils.isEmpty(doctor.getHead_pic())) {
                            Picasso.with(getActivity()).load(doctor.getHead_pic()).into(rvHeads[i]);
                        }
                        rvHeads[i].setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("userId", doctor.getId());
                                intent.putExtra("userName", doctor.getNickname());
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    // handler.sendEmptyMessage(HANDLER_ERROR);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Log.d(TAG, "JsonHttpResponseHandler.re : " + errorResponse.toString());
                Toast.makeText(getActivity(), "当前网络状况不好，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unused")
    private void dependWeightDataChangeUI() {
        List<Weight> weights = mDbUtils.getAllWeight();
        int height = spf.getInt("height", 0);
        int target_weight = spf.getInt("target_weight", 0);
        Log.d(TAG, "  height : " + height + "  target_weight * " + target_weight);
        Log.d(TAG, "  weights.size() : " + weights.size());
        if (weights == null || weights.size() == 0 || height == 0 || target_weight == 0) {
            rlNone.setVisibility(View.VISIBLE);
            rlOcuppy.setVisibility(View.GONE);
        } else {
            rlNone.setVisibility(View.GONE);
            rlOcuppy.setVisibility(View.VISIBLE);
            String todayString = DateTime.today(TimeZone.getDefault()).format("YYYYMMDDhhmmss");
            Weight weight = mDbUtils.queryOneWeight(todayString);
            if (weight != null) {
                Log.d("@@", "home.weight！null : " + weight.toString());
                ivCheck2.setVisibility(View.VISIBLE);
                ivCheck1.setVisibility(View.GONE);
                ivRc.setVisibility(View.GONE);
            } else {
                Log.d("@@", "home.weight.null : ");
                ivCheck2.setVisibility(View.GONE);
                ivCheck1.setVisibility(View.VISIBLE);
                ivRc.setVisibility(View.VISIBLE);
            }
            if (weight == null && weights != null && weights.size() > 0) {
                weight = weights.get(0);
            }
            if (weight.getWeight() > 10) {
                float bmi =  (float) (Math.round(((float) ((weight.getWeight() / (float) (height * height / 10000.0f)))) * 10) * 0.1);
                Log.d("@@", "home.bmi.00 : " + bmi + "current:" + weight.getWeight() );
                spf.edit().putFloat("bmi", bmi);
                dependBmiChangeUI(bmi);
                tvbmi.setText("" + bmi);
            } else {
                Log.d("@@", "home.weight.1 : ");
                float historyBMI = TpqApplication.sharedPreferences.getFloat("bmi", 0);
                tvbmi.setText(historyBMI + "");
                dependBmiChangeUI(historyBMI);
            }
        }
    }

    private void dependBmiChangeUI(float progress) {
        pvView.setData(getActivity(), progress);
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
        tvbmi.setText("" + progress);
        ((HomeActivity) getActivity()).changeToolbarBg(progress);
    }

    private void fetchPostsList(int offset) {
        Map<String, Object> postsListMap = TpqApplication.getInstance(getActivity()).getHotPostsListMap();
        if (null != postsListMap) {
            Object listOj = postsListMap.get(CommonConstant.KEY_DATA_LIST);
            if (null != listOj && listOj instanceof List<?>) {
                List<PostsEntity> postsList = (List<PostsEntity>) listOj;
                if (null != postsList && postsList.size() > 0) {
                    final HotPostsAdapter hotPostsAdapter = new HotPostsAdapter(getActivity(), postsList);
                    ListView postsListView = findView(contentView, R.id.lv_posts);
                    postsListView.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                            if (null != hotPostsAdapter) {
                                PostsEntity posts = hotPostsAdapter.getItem(clickIndex);
                                TpqApplication.getInstance(getActivity()).setShowingPostsEntity(posts);
                                Intent openPostsDetail = new Intent(getActivity(), PostsDetailActivity.class);
                                getActivity().startActivity(openPostsDetail);
                            }
                        }
                    });
                    //记录ScrollView目前的位置
                    ScrollView homeScrollview = findView(contentView, R.id.scrlv_home);
                    int scrollY = homeScrollview.getScrollY();

                    postsListView.setAdapter(hotPostsAdapter);
                    setTotalHeightofListView(postsListView);

                    homeScrollview.scrollTo(0, scrollY);
                }
            }
        }

        fetchPostsList(offset, 10);
    }

    private void fetchPostsList(int offset, int page_sizes) {
        CommonRequest fetchPostsList = new CommonRequest();
        fetchPostsList.setRequestApiName(InterfaceConstant.API_THREAD_FETCH);
        fetchPostsList.setRequestID(InterfaceConstant.REQUEST_ID_THREAD_FETCH);
        fetchPostsList.addRequestParam(APIKey.COMMON_OFFSET, offset);
        fetchPostsList.addRequestParam(APIKey.COMMON_PAGE_SIZE, page_sizes);
        fetchPostsList.addRequestParam(APIKey.COMMON_SORT_TYPES, "desc");
        fetchPostsList.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.THREAD_VIEWS_COUNT);
        fetchPostsList.addRequestParam(APIKey.THREAD_STATUS, APIKey.COMMON_STATUS_LEGAL);

        addRequestAsyncTask(contentView, fetchPostsList);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_THREAD_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> rawPostsList = TypeUtil.getList(resultMap.get(APIKey.THREADS));
                    if (null != rawPostsList && rawPostsList.size() > 0) {
                        List<PostsEntity> postsList = EntityUtils.getPostsEntityList(rawPostsList);
                        if (null != postsList) {
                            final HotPostsAdapter hotPostsAdapter = new HotPostsAdapter(getActivity(), postsList);
                            ListView postsListView = findView(contentView, R.id.lv_posts);
                            postsListView.setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    int clickIndex = position - ((ListView) parent).getHeaderViewsCount();
                                    if (null != hotPostsAdapter) {
                                        PostsEntity posts = hotPostsAdapter.getItem(clickIndex);
                                        TpqApplication.getInstance(getActivity()).setShowingPostsEntity(posts);
                                        Intent openPostsDetail = new Intent(getActivity(), PostsDetailActivity.class);
                                        getActivity().startActivity(openPostsDetail);
                                    }
                                }
                            });
                            //记录ScrollView目前的位置
                            ScrollView homeScrollview = findView(contentView, R.id.scrlv_home);
                            int scrollY = homeScrollview.getScrollY();

                            postsListView.setAdapter(hotPostsAdapter);
                            setTotalHeightofListView(postsListView);

                            homeScrollview.scrollTo(0, scrollY);

                            //                            TpqApplication.getInstance(getActivity()).setPostsList(postsList, toBeContinued);
                        }
                    }
                }
            }
        }
    }

    private void getTodayRecommend() {
        clientRecommend.post(InterfaceConstant.TODAY_RECOMMEND, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // TODO Auto-generated method stub
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "DoctorJsonHttpResponseHandler.re : " + response.toString());
                Type listType = new TypeToken<LinkedList<Meals>>() {
                }.getType();
                Gson gson = new Gson();
                final LinkedList<Meals> meals = gson.fromJson((JsonUtils.getOjectString((response.toString()), "meals")), listType);
                if (meals != null && meals.size() > 0) {
                    Picasso.with(getActivity()).load(meals.get(0).getMeals_pic()).into(ivToday1);
                    ivToday1.setVisibility(View.VISIBLE);
                    ivToday1.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/meals/detail?mealsId=" + meals.get(0).getId()).putExtra(
                                    "title", meals.get(0).getDescription()));
                        }
                    });
                    if (meals.size() > 1) {
                        ivToday2.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(meals.get(1).getMeals_pic()).into(ivToday2);
                        ivToday2.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/meals/detail?mealsId=" + meals.get(1).getId()).putExtra(
                                        "title", meals.get(1).getDescription()));
                            }
                        });
                    }
                    if (meals.size() > 2) {
                        ivToday3.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(meals.get(2).getMeals_pic()).into(ivToday3);
                        ivToday3.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/meals/detail?mealsId=" + meals.get(2).getId()).putExtra(
                                        "title", meals.get(2).getDescription()));
                            }
                        });
                    }
                }
            }

        });

    }

    private void setTotalHeightofListView(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            //mView.measure(0, 0);  
            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tvMore:
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/meals").putExtra("title", "膳食推荐"));
            break;

        case R.id.tvPostMore:
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).changeShowingFragment(1);
            }
            break;

        case R.id.tvFunction1:
            startActivity(new Intent(getActivity(), BloodSugarActivity.class));
            //showToast("正在开发中");
            break;
        case R.id.tvFunction2:
            Intent openBodyCheck = new Intent(getActivity(), MedicalRecordActivity.class);
            startActivity(openBodyCheck);
            break;
        case R.id.tvFunction3:
            Intent openPlan = new Intent(getActivity(), PlanActivity.class);
            startActivity(openPlan);
            break;
        case R.id.tvFunction4:
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/book").putExtra("title", "知识课堂"));
            break;
        case R.id.tvFunction5:
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/trolltech").putExtra("title", "奇趣"));
            break;
        case R.id.tvFunction6:
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "http://121.40.148.142/tpq/h5/story").putExtra("title", "故事"));
            break;
        case R.id.btnRecord:
            startActivity(new Intent(getActivity(), RecordHeightActivity.class).putExtra("init", true));
            break;
        case R.id.ivCheck1:
            startActivity(new Intent(getActivity(), WeightMainActivity.class));
            break;
        case R.id.ivCheck2:
            startActivity(new Intent(getActivity(), WeightMainActivity.class));
            break;
        case R.id.ivRc:
            startActivity(new Intent(getActivity(), RecordWeightActivity.class).putExtra("isTodayWeight", true));
            break;

        default:
            break;
        }
    }

}
