package com.zcmedical.tangpangquan.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mob.tools.utils.UIHandler;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.component.BaseProgressDialog;
import com.zcmedical.common.component.XEditText;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.db.BloodSugar;
import com.zcmedical.common.db.DbUtils;
import com.zcmedical.common.db.Weight;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.JsonUtils;
import com.zcmedical.common.utils.MD5UUtil;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.widget.ClearTextEditText;
import com.zcmedical.huanxin.User;
import com.zcmedical.huanxin.UserDao;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class LogonActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
    private static final String TAG = "LogonActivity";
    private boolean progressShow;
    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);
        client = new AsyncHttpClient();
        init();

    }

    private void init() {
        initToolbar(getString(R.string.title_logon_activity), false);
        initUI();
    }

    private void initUI() {
        final ClearTextEditText userCountEdt = findView(R.id.ctedt_user_account);
        final ClearTextEditText pswEdt = findView(R.id.ctedt_user_psw);
        pswEdt.setTag(false); //tag = false 表示密码是隐藏状态

        ImageButton showPswBtn = findView(R.id.imvbtn_show_psw);
        showPswBtn.setOnClickListener(this);

        Button logonBtn = findView(R.id.btn_logon);
        Button registerBtn = findView(R.id.btn_register);
        TextView forgetPswTv = findView(R.id.tv_forget_psw);
        logonBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        forgetPswTv.setOnClickListener(this);

        setViewClickListener(R.id.imvbtn_logon_qq, this);
        setViewClickListener(R.id.imvbtn_logon_wechat, this);
        setViewClickListener(R.id.imvbtn_logon_weibo, this);

        UserEntity user = TpqApplication.getInstance(getContext()).getUser(); //自动填写上次登陆过的账号
        if (null != user) {
            String mobile = user.getMobile();
            if (!TextUtils.isEmpty(mobile)) {
                userCountEdt.setText(mobile);
                userCountEdt.setSelection(mobile.length());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logon:
                XEditText userCountEdt = findView(R.id.ctedt_user_account);
                XEditText usePswEdt = findView(R.id.ctedt_user_psw);
                String userCount = userCountEdt.getText().toString();
                String psw = usePswEdt.getText().toString();
                if (!TextUtils.isEmpty(userCount) && !TextUtils.isEmpty(psw)) {
                    logon(userCount, psw);
                } else {
                    showToast(getString(R.string.no_input_logon_alert));
                }
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.imvbtn_show_psw:
                final XEditText pswEdt = findView(R.id.ctedt_user_psw);
                final ImageButton showPswBtn = findView(R.id.imvbtn_show_psw);
                boolean isShowPsw = TypeUtil.getBoolean(pswEdt.getTag(), false);
                if (isShowPsw) {
                    pswEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPswBtn.setImageDrawable(getResources().getDrawable(R.drawable.log_btn_pswhide_normal));
                } else {
                    pswEdt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPswBtn.setImageDrawable(getResources().getDrawable(R.drawable.log_btn_pswshow_normal));
                }
                pswEdt.setTag(!isShowPsw);
                pswEdt.setSelection(pswEdt.getText().toString().length());
                break;
            case R.id.tv_forget_psw:
                Intent openForgetPsw = new Intent(getContext(), ForgetPasswordActivity.class);
                startActivity(openForgetPsw);
                break;
            case R.id.imvbtn_logon_qq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
            case R.id.imvbtn_logon_wechat:
                Platform weixin = ShareSDK.getPlatform(Wechat.NAME);
                authorize(weixin);
                break;
            case R.id.imvbtn_logon_weibo:
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                authorize(weibo);
                break;

            default:
                break;
        }
    }

    //执行授权,获取用户信息
    //文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
    private void authorize(Platform plat) {

        //判断指定平台是否已经完成授权
        if (plat.isValid() && plat.getDb() != null) {
            String userId = plat.getDb().getUserId();
            String thirdLogonName = plat.getDb().getUserName();
            if (userId != null) {
                showToast("授权成功，正在登陆");
                thirdPartyLogon(plat.getName(), userId, thirdLogonName);
                return;
            }
        }

        plat.setPlatformActionListener(this);
        //关闭SSO授权
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    private void logon(String userCount, String psw) {
        CommonRequest logonRequest = new CommonRequest();
        logonRequest.setRequestApiName(InterfaceConstant.API_USER_LOGON);
        logonRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_LOGON);
        logonRequest.addRequestParam(APIKey.USER_USERNAME, userCount);
        logonRequest.addRequestParam(APIKey.USER_PASSWORD, psw);

        addRequestAsyncTask(logonRequest);

        showProgressDialog(getString(R.string.logoning));
    }

    private void register() {
        Intent openRegisteration = new Intent(getContext(), RegisterationActivity.class);
        startActivity(openRegisteration);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        if (InterfaceConstant.REQUEST_ID_USER_LOGON.equals(requestID) || InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON.equals(requestID)) {
            if (APIKey.STATUS_SUCCESSFUL == status) {
                if (null != resultMap) {
                    showToast(getString(R.string.logon_sucessful));
                    UserEntity user = EntityUtils.getUserEntity(TypeUtil.getMap(resultMap.get(APIKey.USER)));
                    if (null != user) {
                        final XEditText pswEdt = findView(R.id.ctedt_user_psw);
                        String psw = pswEdt.getText() != null ? pswEdt.getText().toString() : "";
                        if (InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON.equals(requestID)) { //如果是第三方登陆，密码为123456
                            psw = "123456";
                        }
                        String pswMD5 = MD5UUtil.generate(psw);
                        user.setPswMD5(pswMD5);
                        TpqApplication.getInstance(getContext()).setUser(user);

                        //登录tpq账号后需继续调用登录环信的函数
                        LoginHx(user.getId(), psw);
                    }

                    //                    startHome();
                    //                    finish();
                }
            } else {
                showToast(message);
            }
        }
        dimissProgressDialog();
    }

    private void startHome() {
        Intent startHome = new Intent(getContext(), HomeActivity.class);
        startActivity(startHome);
    }

    //环信
    BaseProgressDialog pd;

    private void LoginHx(final String currentUsername, final String currentPassword) {
        pd = new BaseProgressDialog(LogonActivity.this);
        progressShow = true;
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {

                if (!progressShow) {
                    return;
                }
                // 登陆成功，保存用户名密码
                TpqApplication.getInstance().setHxUserName(currentUsername);
                TpqApplication.getInstance().setHxPassword(currentPassword);
                TpqApplication.currentUserNick = spf.getString(APIKey.USER_NICKNAME, "");
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.setMessage(getString(R.string.list_is_for));
                    }
                });
                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    // conversations in case we are auto login
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    //处理好友和群组
                    processContactsAndGroups();
                } catch (Exception e) {
                    e.printStackTrace();
                    //取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            TpqApplication.getInstance().logout(null);
                            Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                //更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(TpqApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                if (!spf.getBoolean("isSync", false)) {
                    hanlder.sendEmptyMessage(HANDLER_WIEGHT_SYNC);
                } else {
                    // 进入主页面
                    hanlder.sendEmptyMessage(HANDLER_FINISH);
                }
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //同步数据，for instance ： 聊天记录 用户体重 血糖数据
    private void syncWeightData() {
        mDbUtils = DbUtils.getInstance(this);
        //同步体重数据:recursive=0 user_id 
        RequestParams params = new RequestParams();
        params.put("recursive", 0);
        params.put("user_id", TpqApplication.getInstance().getUserId());
        client.post(InterfaceConstant.WEIGHT_FETCH, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "syncData.weight.failed");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "syncData.weight.onSuccess : " + response.toString());
                Type listType = new TypeToken<LinkedList<Weight>>() {
                }.getType();
                Gson gson = new Gson();
                LinkedList<Weight> weights = gson.fromJson((JsonUtils.getOjectString(response.toString(), "weights")), listType);
                if (weights != null && weights.size() > 0) {
                    for (Weight weight2 : weights) {
                        Log.d(TAG, "syncData.weight.onSuccess. : " + weight2.toString());
                    }
                    mDbUtils.insertSeriesWeight(weights);
                } else {
                    Log.d(TAG, "syncData.weight.onSuccess.无数据 : ");
                }

            }

            @Override
            public void onFinish() {
                super.onFinish();
                //去同步血糖数据
                hanlder.sendEmptyMessage(HANDLER_BS_SYNV);
            }
        });
    }

    private void syncBloodSugarData() {
        //同步血糖数据:recursive=0 user_id 
        RequestParams params = new RequestParams();
        params.put("recursive", 0);
        params.put("user_id", TpqApplication.getInstance().getUserId());
        client.post(InterfaceConstant.BLOOD_SUGAR_FETCH, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "syncData.BloodSugar.failed");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(TAG, "syncData.BloodSugar.onSuccess : " + response.toString());
                Type listType = new TypeToken<LinkedList<BloodSugar>>() {
                }.getType();
                Gson gson = new Gson();
                LinkedList<BloodSugar> bloodSugars = gson.fromJson((JsonUtils.getOjectString(response.toString(), "blood_sugars")), listType);
                if (bloodSugars != null && bloodSugars.size() > 0) {
                    for (BloodSugar bs : bloodSugars) {
                        Log.d(TAG, "syncData.bs.onSuccess. : " + bs.toString());
                    }
                    mDbUtils.insertSeriesBloodSugar(bloodSugars);
                } else {
                    Log.d(TAG, "syncData.BloodSugar.onSuccess.无数据 : ");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                // 进入主页面
                hanlder.sendEmptyMessage(HANDLER_FINISH);
            }
        });

    }

    private static final int HANDLER_FINISH = 10;
    private static final int HANDLER_WIEGHT_SYNC = 20;
    private static final int HANDLER_BS_SYNV = 30;

    Handler hanlder = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_FINISH:
                    if (!LogonActivity.this.isFinishing())
                        pd.dismiss();
                    spf.edit().putBoolean("isSync", true);
                    startActivity(new Intent(LogonActivity.this, HomeActivity.class));
                    finish();
                    break;
                case HANDLER_WIEGHT_SYNC:
                    syncWeightData();
                    break;
                case HANDLER_BS_SYNV:
                    syncBloodSugarData();
                    break;
                default:
                    break;
            }
        }

    };

    private void processContactsAndGroups() throws EaseMobException {
        // demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
        List<String> usernames = EMContactManager.getInstance().getContactUserNames();
        EMLog.d("roster", "contacts size: " + usernames.size());
        Map<String, User> userlist = new HashMap<String, User>();
        for (String username : usernames) {
            User user = new User();
            user.setUsername(username);
            setUserHearder(username, user);
            userlist.put(username, user);
        }
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(CommonConstant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(CommonConstant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(CommonConstant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(CommonConstant.GROUP_USERNAME, groupUser);

        // 存入内存
        TpqApplication.getInstance().setContactList(userlist);
        System.out.println("----------------" + userlist.values().toString());
        // 存入db
        UserDao dao = new UserDao(LogonActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);

        //获取黑名单列表
        List<String> blackList = EMContactManager.getInstance().getBlackListUsernamesFromServer();
        //保存黑名单
        EMContactManager.getInstance().saveBlackList(blackList);

        // 获取群聊列表(群聊里只有groupid和groupname等简单信息，不包含members),sdk会把群组存入到内存和db中
        EMGroupManager.getInstance().getGroupsFromServer();
    }

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    protected void setUserHearder(String username, User user) {
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
    }

    /********************************* 第三方登陆回调 *****************************************************/

    @Override
    public void onCancel(Platform platform, int action) {
        showToast("取消第三方登陆");
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> acountInfo) {
        String platformName = platform.getName();
        if (!TextUtils.isEmpty(platformName) && action == Platform.ACTION_USER_INFOR) {
            Log.i("cth", "platformName = " + platformName + ",acountInfo = " + acountInfo);
            Log.i("cth", "UserId = " + platform.getDb().getUserId());
            if (null != platform.getDb()) {
                String thirdLogonName = platform.getDb().getUserName();
                thirdPartyLogon(platformName, platform.getDb().getUserId(), thirdLogonName);
            } else {
                showToast("第三方登陆失败");
            }
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        showToast("第三方登陆失败");
    }

    /*****************************************************************************************************/

    private void thirdPartyLogon(String platformName, String platformUserId, String thirdLogonName) {
        CommonRequest thirdPartyLogonRequest = new CommonRequest();
        thirdPartyLogonRequest.setRequestApiName(InterfaceConstant.API_USER_THIRD_LOGON);
        thirdPartyLogonRequest.setRequestID(InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON);
        if (!TextUtils.isEmpty(platformName)) {
            if (QQ.NAME.equals(platformName)) {
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_QQ_IDENTIFY, platformUserId);
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_QQ, thirdLogonName);
            } else if (Wechat.NAME.equals(platformName)) {
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIXIN_IDENTIFY, platformUserId);
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIXIN, thirdLogonName);
            } else if (SinaWeibo.NAME.equals(platformName)) {
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIBO_IDENTIFY, platformUserId);
                thirdPartyLogonRequest.addRequestParam(APIKey.USER_WEIBO, thirdLogonName);
            }

            addRequestAsyncTask(thirdPartyLogonRequest);

            showProgressDialog("登录中");
        }
    }
}
