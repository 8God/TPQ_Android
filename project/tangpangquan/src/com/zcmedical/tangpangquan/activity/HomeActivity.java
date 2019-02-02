package com.zcmedical.tangpangquan.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMNotifier;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.BaseFragment;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.BasicDialog;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.update.UpdateUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.huanxin.DemoHXSDKHelper;
import com.zcmedical.huanxin.HXSDKHelper;
import com.zcmedical.huanxin.InviteMessage;
import com.zcmedical.huanxin.InviteMessage.InviteMesageStatus;
import com.zcmedical.huanxin.InviteMessgeDao;
import com.zcmedical.huanxin.User;
import com.zcmedical.huanxin.UserDao;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;
import com.zcmedical.tangpangquan.fragment.BbsFragment;
import com.zcmedical.tangpangquan.fragment.ContactlistFragment;
import com.zcmedical.tangpangquan.fragment.HomeFragment;
import com.zcmedical.tangpangquan.fragment.PersonFragment;
import com.zcmedical.tangpangquan.fragment.QuesAnswerFragment;

public class HomeActivity extends BaseActivity implements OnClickListener, EMEventListener {

    private static final int CHECK_VERSION_DELAY = 1000 * 3600;
    private static final String TAG = "HomeActivity";
    private static final int INIT_CURRENT_ITEM = 0;
    private int currentItem = INIT_CURRENT_ITEM;

    private int[] titleArray = new int[] { R.string.title_tab_home, R.string.title_tab_bbs, R.string.title_tab_qanda, R.string.title_tab_person };
    private List<BaseFragment> fragmentList;
    private List<ImageView> tabIconList;
    private List<TextView> tabTitleList;
    private ContactlistFragment mCantactlistFragment;
    private Toolbar toolbar;

    //private ChatAllHistoryFragment mChatAllHistoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autoUpdate();
        updateUserInfo();
        init();
        initHuanXin();
    }

    private void init() {
        initToolbar();
        setActionBarTitle(INIT_CURRENT_ITEM);
        initFragmentList();
        initUI();

        changeShowingFragment(INIT_CURRENT_ITEM);
    }

    private void initToolbar() {
        toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }
    }

    private void updateUserInfo() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchUserInfo = new CommonRequest();
        fetchUserInfo.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        fetchUserInfo.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        fetchUserInfo.addRequestParam(APIKey.COMMON_ID, userId);

        addRequestAsyncTask(fetchUserInfo);
    }

    public void changeToolbarBg(float progress) {
        int color = Color.parseColor("#29B6F6");
        if (progress <= 18.5f) {
            color = Color.parseColor("#29B6F6");
        } else if (progress <= 25.0f && progress > 18.5) {
            color = Color.parseColor("#37DCA2");
        } else if (progress <= 30.0f && progress > 25.0f) {
            color = Color.parseColor("#FFD600");
        } else if (progress <= 35.0f && progress > 30.0f) {
            color = Color.parseColor("#FFA000");
        } else if (progress <= 40.0f && progress > 35.0f) {
            color = Color.parseColor("#FF6181");
        } else if (progress > 40.0f) {
            color = Color.parseColor("#C2185B");
        }
        toolbar.setBackgroundColor(color);
    }

    private void initTabbar() {
        tabIconList = new ArrayList<ImageView>();
        tabTitleList = new ArrayList<TextView>();

        ImageView tabHomeImv = findView(R.id.imv_tab_home);
        ImageView tabBbsImv = findView(R.id.imv_tab_bbs);
        ImageView tabQandAImv = findView(R.id.imv_tab_qanda);
        ImageView tabPersionImv = findView(R.id.imv_tab_person);
        tabIconList.add(tabHomeImv);
        tabIconList.add(tabBbsImv);
        tabIconList.add(tabQandAImv);
        tabIconList.add(tabPersionImv);

        tabBbsImv.setEnabled(false);
        tabQandAImv.setEnabled(false);
        tabPersionImv.setEnabled(false);

        TextView tabHomeTv = findView(R.id.tv_tab_home);
        TextView tabBbsTv = findView(R.id.tv_tab_bbs);
        TextView tabQandATv = findView(R.id.tv_tab_qanda);
        TextView tabPersionTv = findView(R.id.tv_tab_person);
        tabTitleList.add(tabHomeTv);
        tabTitleList.add(tabBbsTv);
        tabTitleList.add(tabQandATv);
        tabTitleList.add(tabPersionTv);
    }

    private void initFragmentList() {
        fragmentList = new ArrayList<BaseFragment>();

        HomeFragment homeFragment = new HomeFragment();
        BbsFragment bbsFragment = new BbsFragment();
        QuesAnswerFragment quesAnswerFragment = new QuesAnswerFragment();
        PersonFragment personFragment = new PersonFragment();

        //环信测试，待屏蔽
        mCantactlistFragment = new ContactlistFragment();

        fragmentList.add(homeFragment);
        fragmentList.add(bbsFragment);
        fragmentList.add(mCantactlistFragment);
        //fragmentList.add(quesAnswerFragment);
        fragmentList.add(personFragment);
    }

    private void initUI() {
        initTabbar();

        //init tab button
        RelativeLayout tabHomeRl = findView(R.id.rl_home_tab_home);
        RelativeLayout tabBbsRl = findView(R.id.rl_home_tab_bbs);
        RelativeLayout tabQandARl = findView(R.id.rl_home_tab_qanda);
        RelativeLayout tabPersonRl = findView(R.id.rl_home_tab_person);

        tabHomeRl.setOnClickListener(this);
        tabBbsRl.setOnClickListener(this);
        tabQandARl.setOnClickListener(this);
        tabPersonRl.setOnClickListener(this);
    }

    @SuppressLint("Recycle")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.rl_home_tab_home:
            changeShowingFragment(0);
            break;
        case R.id.rl_home_tab_bbs:
            changeShowingFragment(1);
            break;
        case R.id.rl_home_tab_qanda:
            changeShowingFragment(2);
            break;
        case R.id.rl_home_tab_person:
            changeShowingFragment(3);
            break;
        default:
            break;
        }

    }

    private void setActionBarTitle(int position) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            String title = getString(titleArray[position]);
            actionBar.setTitle(title);
        }
    }

    public void changeShowingFragment(int clickedItem) {
        if (null != fragmentList) {
            BaseFragment showingFragment = fragmentList.get(clickedItem);
            if (null != showingFragment) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, showingFragment).commit();

                setActionBarTitle(clickedItem);
                changeTabSelectedStatus(clickedItem);
                currentItem = clickedItem;
                if (currentItem != 0) {
                    changeToolbarBg(0);
                }
            }
        }
    }

    private void changeTabSelectedStatus(int position) {
        tabIconList.get(currentItem).setEnabled(false);
        tabIconList.get(position).setEnabled(true);

        tabTitleList.get(currentItem).setTextColor(getResources().getColor(R.color.micro_gray));
        tabTitleList.get(position).setTextColor(getResources().getColor(R.color.tab_text_pre));

        currentItem = position;
    }

    //huanxin

    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;
    private InviteMessgeDao inviteMessgeDao;
    private UserDao userDao;

    private void initHuanXin() {
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(CommonConstant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        }

        inviteMessgeDao = new InviteMessgeDao(this);
        userDao = new UserDao(this);

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());
        // 注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
    }

    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        TpqApplication.getInstance().logout(null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!HomeActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
                        TpqApplication.getInstance(getContext()).setLogon(false);
                        startActivity(new Intent(HomeActivity.this, LogonActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                //EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        TpqApplication.getInstance().logout(null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!HomeActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
                        startActivity(new Intent(HomeActivity.this, LogonActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    /***
     * 好友变化listener
     *
     */
    private class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Map<String, User> localUsers = TpqApplication.getInstance().getContactList();
            Map<String, User> toAddUsers = new HashMap<String, User>();
            for (String username : usernameList) {
                User user = setUserHead(username);
                // 添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
            // 刷新ui
            if (currentItem == 2) {
                //contactListFragment.refresh();
                mCantactlistFragment.refresh();
            }

        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, User> localUsers = TpqApplication.getInstance().getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    String st10 = getResources().getString(R.string.have_you_removed);
                    if (ChatActivity.activityInstance != null && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
                        Toast.makeText(HomeActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG).show();
                        ChatActivity.activityInstance.finish();
                    }
                    updateUnreadLabel();
                    // 刷新ui
                    mCantactlistFragment.refresh();
                }
            });

        }

        @Override
        public void onContactInvited(String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);

        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

    /**
     * 连接监听listener
     *
     */
    private class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ChatHistoryActivity.isError = false;
                }

            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.Less_than_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {
                        ChatHistoryActivity.isError = true;
                        if (NetUtils.hasNetwork(HomeActivity.this)) {
                            ChatHistoryActivity.errorTx = st1;
                        } else {
                            ChatHistoryActivity.errorTx = st2;
                        }

                    }
                }

            });
        }
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

        // 刷新bottom bar消息未读数
        //updateUnreadAddressLable();
        // 刷新好友页面ui
        if (currentItem == 2) {
            mCantactlistFragment.refresh();
        }
    }

    /**
     * 保存邀请等msg
     *
     * @param msg
     */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        User user = TpqApplication.getInstance().getContactList().get(CommonConstant.NEW_FRIENDS_USERNAME);
        if (user.getUnreadMsgCount() == 0)
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }

    /**
     * set head
     *
     * @param username
     * @return
     */
    User setUserHead(String username) {
        User user = new User();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(CommonConstant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (count > 0) {
            ContactlistFragment.menuTitle = "消息(" + count + "条未读)";
        } else {
            ContactlistFragment.menuTitle = "提问历史";
        }
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        return unreadMsgCountTotal;
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        {
            switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                // 提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUI();
                break;
            }

            case EventOfflineMessage: {
                refreshUI();
                break;
            }

            default:
                refreshUI();
                break;
            }
        }
    }

    private void refreshUI() {
        Log.d(TAG, "refreshUI : ");
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                updateUnreadLabel();
                if (currentItem == 2) {
                    if (mCantactlistFragment != null) {
                        mCantactlistFragment.refresh();
                    }
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (conflictBuilder != null) {
            conflictBuilder.create().dismiss();
            conflictBuilder = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            //updateUnreadAddressLable();
            EMChatManager.getInstance().activityResumed();
        }

        // unregister this event listener when this activity enters the
        // background
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage });
        super.onResume();
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(CommonConstant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (getIntent().getBooleanExtra(CommonConstant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        } else if (intent.getIntExtra("Tab", -1) != -1) {
            changeShowingFragment(intent.getIntExtra("Tab", -1));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //moveTaskToBack(false);
            showTips();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showTips() {
        BasicDialog alertDialog = new BasicDialog.Builder(this).setTitle("提醒").setMessage("是否退出程序").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Stack<Activity> activityStack = new Stack<Activity>();
                while (!activityStack.isEmpty()) {
                    Activity act = activityStack.pop();
                    if (null != act) {
                        act.finish();
                    }
                }
                finish();
            }

        }).setNegativeButton("取消",

        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }

    private void autoUpdate() {
        Date lastTriggerTime = TpqApplication.getInstance().getUpdateTriggerTime();
        boolean isUpdateTrigger = false;
        Date now = new Date();
        if (lastTriggerTime == null) {
            isUpdateTrigger = true;
        } else if (now.after(lastTriggerTime)) {
            isUpdateTrigger = true;
        }
        if (isUpdateTrigger) {
            TpqApplication.getInstance().setUpdateTriggerTime(new Date(now.getTime() + CHECK_VERSION_DELAY)); // 延迟1分钟，下次提醒为1分钟后

            UpdateUtils.checkVersion(getContext());
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        List<UserEntity> userEntityList = EntityUtils.getUserEntityList(userMapList);
                        if (null != userEntityList && userEntityList.size() > 0) {
                            UserEntity user = userEntityList.get(0);
                            if (null != user) {
                                TpqApplication.getInstance().setUser(user);
                            }
                        }
                    }
                }
            }
        }
    }

}
