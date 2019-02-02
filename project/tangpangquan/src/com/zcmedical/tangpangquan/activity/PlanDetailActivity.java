package com.zcmedical.tangpangquan.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.CalendarUtil;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.adapter.PlanDetailsAdapter;
import com.zcmedical.tangpangquan.entity.PlanDetailEntity;
import com.zcmedical.tangpangquan.entity.PlanEntity;

public class PlanDetailActivity extends BaseActivity implements OnClickListener {

    private PlanEntity plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plan_detail);

        init();
    }

    private void init() {
        initToolbar("计划");
        initData();
        initUI();
        fetchPlanFollow();
        showPlanDetailList();
    }

    private void initData() {
        plan = TpqApplication.getInstance().getShowingPlanEntity();
    }

    private void initUI() {
        setTextView(R.id.tv_plan_title, plan.getTitle());
        setTextView(R.id.tv_day_count, plan.getDayCount() + "");
        setTextView(R.id.tv_follow_count, plan.getFollowCount() + "");
        setTextView(R.id.tv_plan_good, plan.getPros());
        setTextView(R.id.tv_plan_bad, plan.getCons());
        setTextView(R.id.tv_plan_desc, plan.getDescription());

        setViewClickListener(R.id.btn_plan_follow, this);
    }

    /**
     * 获取每一天的详情
     */
    private void fetchPlanDetail() {
        if (null != plan) {
            CommonRequest fetchPlanDetail = new CommonRequest();
            fetchPlanDetail.setRequestApiName(InterfaceConstant.API_PLAN_DETAIL_FETCH);
            fetchPlanDetail.setRequestID(InterfaceConstant.REQUEST_ID_PLAN_DETAIL_FETCH);
            fetchPlanDetail.addRequestParam(APIKey.PLAN_ID, plan.getId());
            fetchPlanDetail.addRequestParam(APIKey.COMMON_OFFSET, 0);
            fetchPlanDetail.addRequestParam(APIKey.COMMON_PAGE_SIZE, plan.getDayCount());
            fetchPlanDetail.addRequestParam(APIKey.COMMON_SORT_FIELDS, APIKey.PLAN_DAY);
            fetchPlanDetail.addRequestParam(APIKey.COMMON_SORT_TYPES, APIKey.SORT_ASC);

            addRequestAsyncTask(fetchPlanDetail);
        }
    }

    /**
     * 获取每一天详情的列表
     */
    private void showPlanDetailList() {
        if (null != plan) {
            List<PlanDetailEntity> planDetailEntityList = plan.getPlanDetailList();
            if (null != planDetailEntityList && planDetailEntityList.size() > 0) {
                ListView planDetailListView = findView(R.id.lv_plan_day_detail);
                //记录ScrollView目前的位置
                ScrollView planDetailScrollview = findView(R.id.scrlv_plan_detail);
                int scrollY = planDetailScrollview.getScrollY();

                PlanDetailsAdapter planDetailsAdapter = new PlanDetailsAdapter(getContext(), planDetailEntityList);
                planDetailListView.setAdapter(planDetailsAdapter);

                setTotalHeightofListView(planDetailListView);

                planDetailScrollview.scrollTo(0, scrollY);
            } else {
                fetchPlanDetail();
            }
        }
    }

    /**
     * 判断是否执行该计划
     */
    private void fetchPlanFollow() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchPlanFollow = new CommonRequest();
        fetchPlanFollow.setRequestApiName(InterfaceConstant.API_PLAN_FOLLOW_FETCH);
        fetchPlanFollow.setRequestID(InterfaceConstant.REQUEST_ID_PLAN_FOLLOW_FETCH);
        fetchPlanFollow.addRequestParam(APIKey.USER_ID, userId);
        fetchPlanFollow.addRequestParam(APIKey.PLAN_ID, plan.getId());

        addRequestAsyncTask(fetchPlanFollow);
    }

    /**
     * 执行该计划
     * 
     * @param createdAt
     *            计划开始时间
     */
    private void createPlanFollow(String createdAt) {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest createPlanFollow = new CommonRequest();
        createPlanFollow.setRequestApiName(InterfaceConstant.API_PLAN_FOLLOW_CREATE);
        createPlanFollow.setRequestID(InterfaceConstant.REQUEST_ID_PLAN_FOLLOW_CREATE);
        createPlanFollow.addRequestParam(APIKey.USER_ID, userId);
        createPlanFollow.addRequestParam(APIKey.PLAN_ID, plan.getId());
        createPlanFollow.addRequestParam(APIKey.COMMON_CREATED_AT, createdAt);

        addRequestAsyncTask(createPlanFollow);

        showProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_plan_follow:
                DialogUtils.showAlertDialog(getContext(), getString(R.string.dialog_msg_plan_follow_create), "马上开始", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String createdAt = CommonConstant.serverTimeFormat.format(new Date());
                        createPlanFollow(createdAt);

                        dialog.dismiss();
                    }
                }, "明天开始", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_YEAR, +1);
                        String createdAt = CommonConstant.serverTimeFormat.format(calendar.getTime());
                        createPlanFollow(createdAt);

                        dialog.dismiss();
                    }
                });
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);

        if (InterfaceConstant.REQUEST_ID_PLAN_FOLLOW_FETCH.equals(requestID)) {
            boolean isHasFollow = false;
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> planFollowList = TypeUtil.getList(resultMap.get(APIKey.PLAN_FOLLOWS));
                    if (null != planFollowList && planFollowList.size() > 0) {
                        Map<String, Object> planFollow = planFollowList.get(0);
                        if (null != planFollow) {
                            Map<String, Object> planMap = TypeUtil.getMap(planFollow.get(APIKey.PLAN));
                            String createdAt = TypeUtil.getString(planFollow.get(APIKey.COMMON_CREATED_AT), "");
                            if (null != planMap) {
                                String id = TypeUtil.getId(planMap.get(APIKey.COMMON_ID));
                                String planId = plan.getId();
                                if (Validator.isIdValid(id) && Validator.isIdValid(planId) && planId.equals(id)) {
                                    isHasFollow = true;
                                    resetButton(createdAt);
                                }
                            }
                        }
                    }
                }
            }
            Button planFollowButton = findView(R.id.btn_plan_follow);
            planFollowButton.setClickable(!isHasFollow);
        } else if (InterfaceConstant.REQUEST_ID_PLAN_FOLLOW_CREATE.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    Map<String, Object> planFollowMap = TypeUtil.getMap(resultMap.get(APIKey.PLAN_FOLLOW));
                    if (null != planFollowMap) {
                        String createdAt = TypeUtil.getString(planFollowMap.get(APIKey.COMMON_CREATED_AT), "");
                        resetButton(createdAt);
                    }
                }
            }
            dimissProgressDialog();
        } else if (InterfaceConstant.REQUEST_ID_PLAN_DETAIL_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> planDetailMapList = TypeUtil.getList(resultMap.get(APIKey.PLAN_DETAILS));
                    if (null != planDetailMapList && planDetailMapList.size() > 0) {
                        List<PlanDetailEntity> planDetailEntityList = EntityUtils.getPlanDetailEntityList(planDetailMapList);
                        if (null != planDetailEntityList && planDetailEntityList.size() > 0) {
                            if (null != plan) {
                                plan.setPlanDetailList(planDetailEntityList);

                                showPlanDetailList();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 用户已关注该计划，重置按钮
     */
    private void resetButton(String createdAt) {
        Button planFollowButton = findView(R.id.btn_plan_follow);
        planFollowButton.setEnabled(false);

        try {
            Date planFollowDate = CommonConstant.serverTimeFormat.parse(createdAt);
            int distanceInDaysWithToday = CalendarUtil.getDistanceInDaysWithToday(planFollowDate);
            Log.i("cth", "distanceInDaysWithToday = " + distanceInDaysWithToday);

            if (distanceInDaysWithToday == 0) {
                planFollowButton.setText("计划今天开始执行");
            } else if (distanceInDaysWithToday < 0) {
                distanceInDaysWithToday = Math.abs(distanceInDaysWithToday);
                if (null != plan) {
                    int planDayCount = plan.getDayCount();
                    if (distanceInDaysWithToday <= planDayCount) {
                        planFollowButton.setText("计划执行到第" + distanceInDaysWithToday + "天");
                    } else {
                        planFollowButton.setText("计划已完成");
                    }
                }

            } else if (distanceInDaysWithToday > 0) {

                if (distanceInDaysWithToday == 1) {
                    planFollowButton.setText("计划明天开始执行");
                } else if (distanceInDaysWithToday > 1) {
                    planFollowButton.setText("计划还有" + distanceInDaysWithToday + "天开始执行");
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            planFollowButton.setText("计划执行到第1天");
        }
    }

    /**
     * 动态设置ListView的高度
     * 
     * @param listView
     */
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
}
