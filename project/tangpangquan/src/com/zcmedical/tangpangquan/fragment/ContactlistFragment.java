/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zcmedical.tangpangquan.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zcmedical.common.base.BaseFragment;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.huanxin.ContactAdapter;
import com.zcmedical.huanxin.InviteMessgeDao;
import com.zcmedical.huanxin.User;
import com.zcmedical.huanxin.UserDao;
import com.zcmedical.tangpangquan.BuildConfig;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.ChatActivity;
import com.zcmedical.tangpangquan.activity.ChatHistoryActivity;
import com.zcmedical.tangpangquan.activity.DoctorDetailsActivity;
import com.zcmedical.tangpangquan.activity.HomeActivity;
import com.zcmedical.tangpangquan.adapter.BbsBannerAdapter;
import com.zcmedical.tangpangquan.adapter.DocterTeamAdapter;
import com.zcmedical.tangpangquan.entity.BannerEntity;
import com.zcmedical.tangpangquan.entity.Doctor;
import com.zcmedical.tangpangquan.entity.DoctorTeam;
import com.zcmedical.tangpangquan.entity.DoctorTeamView;
import com.zcmedical.tangpangquan.view.BbsBannerPager;

/**
 * 问答fragment
 * 
 */
public class ContactlistFragment extends BaseFragment implements OnClickListener {
    private static final String TAG = "ContactlistFragment";
    private ContactAdapter adapter;
    private DocterTeamAdapter teamAdapter;
    private List<DoctorTeamView> doctorTeamViews = new ArrayList<DoctorTeamView>();
    private List<User> contactList;
    private com.zcmedical.tangpangquan.view.ListViewForScrollView listView;
    private boolean hidden;
    private InputMethodManager inputMethodManager;
    private List<String> blackList;
    public MenuItem menuItem;
    public static String menuTitle = "提问历史";
    private AsyncHttpClient client;

    private ViewPager bbsBannerVp;
    private int updateIndex = 0;
    private int currentPagerPosition = 0;
    private List<BbsBannerPager> bannerList;
    private ImageView[] dotArray;
    private boolean isFinishInit = false;
    private final int SCROLL_DELAY = 3000;
    private final int DOT_MARGIN = 12;
    private List<Doctor> doctors = new ArrayList<Doctor>();
    Handler updateHandler = new Handler();
    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            bbsBannerVp.setCurrentItem(++updateIndex, true);
            updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setMenuVisibility(true);
        setHasOptionsMenu(true);
        initBannerList();
        initDotView();
        initBannerViewPager();
        getView().findViewById(R.id.btnAsk).setOnClickListener(this);
        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        client = new AsyncHttpClient();
        getDoctorTeamData();
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        listView = (com.zcmedical.tangpangquan.view.ListViewForScrollView) getView().findViewById(R.id.list);
        //黑名单列表
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();

        // 设置adapter
        teamAdapter = new DocterTeamAdapter(getActivity(), doctorTeamViews);
        listView.setAdapter(teamAdapter);
        adapter = new ContactAdapter(getActivity(), R.layout.row_contact, contactList);
        //listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String username = adapter.getItem(position).getUsername();
                Doctor doctor = ((DoctorTeamView) teamAdapter.getItem(position)).getDoctor();
                if (doctor == null) {
                    return;
                }
                startActivity(new Intent(getActivity(), DoctorDetailsActivity.class).putExtra("doctorId", doctor.getId()));
            }
        });
        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        registerForContextMenu(listView);
        isFinishInit = true;
        ScrollView svMain = (ScrollView) getView().findViewById(R.id.svMain);
        svMain.smoothScrollTo(0, 0);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 长按前两个不弹menu
        if (((AdapterContextMenuInfo) menuInfo).position > 1) {
            getActivity().getMenuInflater().inflate(R.menu.context_contact_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            User tobeDeleteUser = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            // 删除此联系人
            deleteContact(tobeDeleteUser);
            // 删除相关的邀请消息
            InviteMessgeDao dao = new InviteMessgeDao(getActivity());
            dao.deleteMessage(tobeDeleteUser.getUsername());
            return true;
        } else if (item.getItemId() == R.id.add_to_blacklist) {
            User user = adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
            moveToBlacklist(user.getUsername());
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //		MobclickAgent.onPause(getActivity());
        //		MobclickAgent.onPageStart("ContactlistFragment");
        updateHandler.postDelayed(updateRunnable, SCROLL_DELAY);
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        //		MobclickAgent.onPageEnd("ContactlistFragment");
        //		MobclickAgent.onPause(getActivity());
        updateHandler.removeCallbacks(updateRunnable);
    }

    /**
     * 删除联系人
     * 
     * @param ：toDeleteUser
     */
    public void deleteContact(final User tobeDeleteUser) {
        String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
                    // 删除db和内存中此用户的数据
                    UserDao dao = new UserDao(getActivity());
                    dao.deleteContact(tobeDeleteUser.getUsername());
                    TpqApplication.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            adapter.remove(tobeDeleteUser);
                            adapter.notifyDataSetChanged();

                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

            }
        }).start();

    }

    /**
     * 把user移入到黑名单
     */
    private void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st1 = getResources().getString(R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //加入到黑名单
                    EMContactManager.getInstance().addUserToBlackList(username, false);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st3, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();
                    if (EMChatManager.getInstance().getUnreadMsgsCount() > 0) {
                        ContactlistFragment.menuTitle = "消息(" + EMChatManager.getInstance().getUnreadMsgsCount() + "条未读)";
                    } else {
                        ContactlistFragment.menuTitle = "提问历史";
                    }
                    menuItem.setTitle(menuTitle);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
        contactList.clear();
        //获取本地好友列表
        Map<String, User> users = TpqApplication.getInstance().getContactList();
        Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, User> entry = iterator.next();
            if (!entry.getKey().equals(CommonConstant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(CommonConstant.GROUP_USERNAME) && !blackList.contains(entry.getKey()))
                contactList.add(entry.getValue());
        }
        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUsername().compareTo(rhs.getUsername());
            }
        });
    }

    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TODO
        super.onSaveInstanceState(outState);
        //	    if(((HomeActivity)getActivity()).isConflict){
        //	    	outState.putBoolean("isConflict", true);
        //	    }else if(((HomeActivity)getActivity()).getCurrentAccountRemoved()){
        //	    	outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        //	    }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_doctor_team_fragment, menu);
        menuItem = menu.getItem(0);
        menuItem.setTitle(menuTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_history:
            Intent openBbsSearch = new Intent(getActivity(), ChatHistoryActivity.class);
            startActivity(openBbsSearch);
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDoctorTeamData() {
        client.post(InterfaceConstant.DOCTOR_TEAM_FETCH, mJsonHttpResponseHandler);
    }

    JsonHttpResponseHandler mJsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Log.d(TAG, "DoctorTeam.re : " + response.toString());
            Type listType = new TypeToken<LinkedList<DoctorTeam>>() {
            }.getType();
            Gson gson = new Gson();
            LinkedList<DoctorTeam> doctorTeam = gson.fromJson((JsonUtils.getOjectString((response.toString()), "doctor_teams")), listType);

            if (BuildConfig.DEBUG) {
                Log.d(TAG, doctorTeam.size() + "");
            }
            doctorTeamViews.clear();
            doctors.clear();
            if (doctorTeam.size() > 0) {
                for (DoctorTeam team : doctorTeam) {
                    if (team.getDoctors() == null)
                        continue;
                    doctorTeamViews.add(new DoctorTeamView(team.getTeam_name()));
                    for (Doctor doctor : team.getDoctors()) {
                        doctorTeamViews.add(new DoctorTeamView(doctor));
                        doctors.add(doctor);
                    }
                }
            }
            teamAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }
    };

    private void initBannerViewPager() {
        bbsBannerVp = findView(getView(), R.id.vp_bbs_banner);
        BbsBannerAdapter bbsBannerAdapter = new BbsBannerAdapter(getActivity(), bannerList);
        bbsBannerVp.setAdapter(bbsBannerAdapter);
        bbsBannerVp.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int selectedPosition) {
                updateIndex = selectedPosition;
                selectedPosition %= bannerList.size();
                changeDotState(currentPagerPosition, selectedPosition);

                currentPagerPosition = selectedPosition;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void initBannerList() {
        bannerList = new ArrayList<BbsBannerPager>();

        BannerEntity be1 = new BannerEntity();
        BannerEntity be2 = new BannerEntity();
        BannerEntity be3 = new BannerEntity();
        BannerEntity be4 = new BannerEntity();

        be1.setBannerBgUrl("http://h.hiphotos.baidu.com/image/w%3D400/sign=4d234f34daf9d72a1764111de42b282a/4a36acaf2edda3cc2bb6932b02e93901213f926e.jpg");
        be2.setBannerBgUrl("http://h.hiphotos.baidu.com/image/w%3D400/sign=e19c7908b4003af34dbadd60052ac619/2e2eb9389b504fc2022d2904e7dde71190ef6d45.jpg");
        be3.setBannerBgUrl("http://d.hiphotos.baidu.com/image/w%3D400/sign=85c1c8fcaa773912c4268461c8188675/908fa0ec08fa513d5555e29f3e6d55fbb3fbd9f0.jpg");
        be4.setBannerBgUrl("http://c.hiphotos.baidu.com/image/w%3D400/sign=3d704a35af345982c58ae4923cf5310b/95eef01f3a292df54a537254be315c6035a873db.jpg");

        BbsBannerPager bbp1 = new BbsBannerPager(getActivity(), be1);
        BbsBannerPager bbp2 = new BbsBannerPager(getActivity(), be2);
        BbsBannerPager bbp3 = new BbsBannerPager(getActivity(), be3);
        BbsBannerPager bbp4 = new BbsBannerPager(getActivity(), be4);

        bannerList.add(bbp1);
        bannerList.add(bbp2);
        bannerList.add(bbp3);
        bannerList.add(bbp4);

    }

    private void changeDotState(int currentPagerPosition, int selectedPosition) {
        dotArray[currentPagerPosition].setEnabled(false);
        dotArray[selectedPosition].setEnabled(true);
    }

    @SuppressLint("NewApi")
    private void initDotView() {
        LinearLayout dot_ll = findView(getView(), R.id.ll_banner_dot);

        dotArray = new ImageView[bannerList.size()];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(DOT_MARGIN, DOT_MARGIN, DOT_MARGIN, DOT_MARGIN);

        for (int i = 0; i < bannerList.size(); i++) {
            ImageView dotView = new ImageView(getActivity());
            dotView.setBackground(getResources().getDrawable(R.drawable.selector_dot));
            dotView.setLayoutParams(params);
            dotArray[i] = dotView;
            dotView.setEnabled(false);

            dot_ll.addView(dotView, i);
        }
        bbsBannerVp = findView(getView(), R.id.vp_bbs_banner);
        if (bbsBannerVp != null) {
            if (!isFinishInit) {
                dotArray[0].setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnAsk:
            //快速提问
            if (doctors.size() > 0) {
                int size = doctors.size();
                int i = 0;
                if (size > 0) {
                    Random random = new Random();
                    i = random.nextInt(size);
                }
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //Log.d(TAG, "onClick------>" + i + "   " + doctors.get(i).toString());
                intent.putExtra("userId", doctors.get(i).getId());
                if (!TextUtils.isEmpty(doctors.get(i).getNickname())) {
                    intent.putExtra("userName", doctors.get(i).getNickname());
                }
                startActivity(intent);
            } else {
                ((HomeActivity) getActivity()).showToast("正在获取医生信息，请稍后再试");
            }
            break;
        default:
            break;
        }

    }

}
