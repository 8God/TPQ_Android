package com.zcmedical.tangpangquan.activity;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonAsyncConnector;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.network.IConnectorToRenderListener;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.huanxin.DemoHXSDKHelper;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.CircleEntity;

public class WelcomeActivity extends BaseActivity {
    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isFullScreen = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        init();
    }

    private void init() {
        preLoadData();

        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        int lastVersionCode = TpqApplication.getInstance(getContext()).getCurrentVersionCode();
        if (currentVersionCode == 0 || lastVersionCode < currentVersionCode) {
            TpqApplication.getInstance(getContext()).setCurrentVersionCode(currentVersionCode);

            startActivity(new Intent(getContext(), GuidePageActivity.class));
            
            finish();
        } else {
            checkLogon();
        }
    }

    private void startHome() {
        Intent startHome = new Intent(getContext(), HomeActivity.class);
        startActivity(startHome);
        finish();
    }

    private void startLogon() {
        Intent startHome = new Intent(getContext(), LogonActivity.class);
        startActivity(startHome);
        finish();
    }

    private void checkLogon() {
        final boolean isLogon = TpqApplication.getInstance(getContext()).isLogon();
        new Thread(new Runnable() {
            public void run() {
                if (DemoHXSDKHelper.getInstance().isLogined() && isLogon) {
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    startHome();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startLogon();
                }
            }
        }).start();
    }

    private void preLoadData() {
        preLoadMyCircleList();
        preLoadAllCircleList();
    }

    private void preLoadAllCircleList() {
        CommonRequest loadCircle = new CommonRequest();
        loadCircle.setRequestApiName(InterfaceConstant.API_FORUM_FETCH);
        loadCircle.addRequestParam(APIKey.FORUM_STATUS, 1);
        loadCircle.addRequestParam(APIKey.COMMON_OFFSET, 0);
        loadCircle.addRequestParam(APIKey.COMMON_PAGE_SIZE, 100);

        CommonAsyncConnector connector = new CommonAsyncConnector(this, new IConnectorToRenderListener() {

            @Override
            public void toRender(Map<String, Object> result) {
                int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), 1);
                if (APIKey.STATUS_SUCCESSFUL == status) {
                    Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                    if (null != resultMap) {
                        List<Map<String, Object>> forums = TypeUtil.getList(resultMap.get(APIKey.FORUMS));
                        if (null != forums && forums.size() > 0) {
                            List<CircleEntity> allCircleList = EntityUtils.getCircleEntityList(forums);
                            if (null != allCircleList && allCircleList.size() > 0) {
                                TpqApplication.getInstance(getContext()).setAllCircleList(allCircleList);
                            }
                        }
                    }
                }
            }
        });

        connector.execute(loadCircle);
    }

    private void preLoadMyCircleList() {
        if (TpqApplication.getInstance(getContext()).isLogon()) {
            String userId = TpqApplication.getInstance(getContext()).getUserId();
            if (Validator.isIdValid(userId)) {
                CommonRequest loadMyForum = new CommonRequest();
                loadMyForum.setRequestApiName(InterfaceConstant.API_FORUM_USER_FETCH);
                loadMyForum.addRequestParam(APIKey.FORUM_STATUS, 1);
                loadMyForum.addRequestParam(APIKey.USER_ID, userId);
                loadMyForum.addRequestParam(APIKey.COMMON_OFFSET, 0);
                loadMyForum.addRequestParam(APIKey.COMMON_PAGE_SIZE, 100);

                CommonAsyncConnector connector = new CommonAsyncConnector(this, new IConnectorToRenderListener() {

                    @Override
                    public void toRender(Map<String, Object> result) {
                        int status = TypeUtil.getInteger(result.get(APIKey.COMMON_STATUS), 1);
                        if (0 == status) {
                            Map<String, Object> resultMap = TypeUtil.getMap(result.get(APIKey.COMMON_RESULT));
                            if (null != resultMap) {
                                List<Map<String, Object>> forumUsers = TypeUtil.getList(resultMap.get(APIKey.FORUM_USERS));
                                if (null != forumUsers && forumUsers.size() > 0) {
                                    List<CircleEntity> myFollowCircleList = EntityUtils.getMyFollowCircleList(forumUsers);
                                    if (null != myFollowCircleList && myFollowCircleList.size() > 0) {
                                        TpqApplication.getInstance(getContext()).setMyFollowCircle(myFollowCircleList);
                                    }
                                }
                            }
                        }
                    }
                });

                connector.execute(loadMyForum);
            }

        }
    }

}
