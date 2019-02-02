package com.zcmedical.tangpangquan.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.utils.DialogUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class AccountBindingActivity extends BaseActivity implements OnClickListener, PlatformActionListener {
    private static final int REQUEST_CODE_BIND_MOBILE = 4;
    private UserEntity logonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_binding);

        init();
    }

    private void init() {
        initToolbar("账号绑定");
        initData();
        if (null != logonUser) {
            initUI();
        }
    }

    private void initData() {
        logonUser = TpqApplication.getInstance().getUser();
    }

    private void initUI() {
        initAccountInfo();

        setViewClickListener(R.id.rl_accountphone, this);
        setViewClickListener(R.id.rl_accountwechat, this);
        setViewClickListener(R.id.rl_accountqq, this);
        setViewClickListener(R.id.rl_accountweibo, this);

    }

    private void initAccountInfo() {
        initAccountPhone();
        initAccountWechat();
        initAccountQQ();
        initAccountWeibo();
    }

    private void initAccountPhone() {
        TextView phoneTv = findView(R.id.tv_accountphone);
        RelativeLayout accountPhoneRl = findView(R.id.rl_accountphone);
        ImageView accountphoneArrowImv = findView(R.id.imv_accountphone_arrow);
        String mobile = logonUser.getMobile();

        if (!TextUtils.isEmpty(mobile)) {
            phoneTv.setText(mobile);
            phoneTv.setEnabled(true);
            accountPhoneRl.setTag(true);
            accountphoneArrowImv.setVisibility(View.GONE);
        } else {
            phoneTv.setText("未绑定");
            phoneTv.setEnabled(false);
            accountPhoneRl.setTag(false);
            accountphoneArrowImv.setVisibility(View.VISIBLE);
        }
    }

    private void initAccountWechat() {
        TextView wechatTv = findView(R.id.tv_accountwechat);
        RelativeLayout accountWechatRl = findView(R.id.rl_accountwechat);
        String wechat = logonUser.getWeixin();

        if (!TextUtils.isEmpty(wechat)) {
            wechatTv.setText("已绑定");
            wechatTv.setEnabled(true);
            accountWechatRl.setEnabled(false);

        } else {
            wechatTv.setText("未绑定");
            wechatTv.setEnabled(false);
            accountWechatRl.setEnabled(true);
        }
    }

    private void initAccountQQ() {
        TextView qqTv = findView(R.id.tv_accountqq);
        RelativeLayout accountQQRl = findView(R.id.rl_accountqq);
        String qq = logonUser.getQq();

        if (!TextUtils.isEmpty(qq)) {
            qqTv.setText("已绑定");
            qqTv.setEnabled(true);
            accountQQRl.setEnabled(false);
        } else {
            qqTv.setText("未绑定");
            qqTv.setEnabled(false);
            accountQQRl.setEnabled(true);
        }
    }

    private void initAccountWeibo() {
        TextView weiboTv = findView(R.id.tv_accountweibo);
        RelativeLayout accountWeiboRl = findView(R.id.rl_accountweibo);
        String weibo = logonUser.getWeibo();

        if (!TextUtils.isEmpty(weibo)) {
            weiboTv.setText("已绑定");
            weiboTv.setEnabled(true);
            accountWeiboRl.setEnabled(false);
        } else {
            weiboTv.setText("未绑定");
            weiboTv.setEnabled(false);
            accountWeiboRl.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_accountphone:
                boolean isBinded = TypeUtil.getBoolean(v.getTag(), false);

                if (isBinded) {
                    DialogUtils.showAlertDialog(getContext(), "该手机账号为您的注册账号,无法进行解绑哦!");
                } else {
                    Intent openMobileBinding = new Intent(getContext(), MobileBindingActivity.class);
                    startActivityForResult(openMobileBinding, REQUEST_CODE_BIND_MOBILE);
                }
                break;
            case R.id.rl_accountwechat:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);
                break;
            case R.id.rl_accountqq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
            case R.id.rl_accountweibo:
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

        plat.setPlatformActionListener(this);
        //关闭SSO授权
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    /********************************* 第三方账号绑定回调 **************************************************/

    @Override
    public void onCancel(Platform platform, int action) {
        showToast("取消第三方账号绑定");
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> acountInfo) {
        String platformName = platform.getName();
        if (!TextUtils.isEmpty(platformName) && action == Platform.ACTION_USER_INFOR) {
            if (null != platform.getDb()) {
                String thirdLogonName = platform.getDb().getUserName();
                String thirdLogonId = platform.getDb().getUserId();
                checkIsBinded(platformName, thirdLogonId, thirdLogonName);
            } else {
                showToast("第三方账号绑定失败");
            }
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        showToast("第三方账号绑定失败");
    }

    /*****************************************************************************************************/

    /**
     * 检查用户是否绑定过
     * 
     * @param platformName
     *            平台名称
     * @param identify
     *            平台标识
     * @param thirdLogonName
     *            平台昵称
     */
    private void checkIsBinded(String platformName, String identify, String thirdLogonName) {
        CommonRequest checkIsBinding = new CommonRequest();
        checkIsBinding.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        checkIsBinding.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        checkIsBinding.addRequestParam(APIKey.COMMON_STATUS, APIKey.COMMON_STATUS_LEGAL);
        checkIsBinding.addAdditionalArg("PlatformName", platformName);
        checkIsBinding.addAdditionalArg("PlatformIdentify", identify);
        checkIsBinding.addAdditionalArg("ThirdLogonName", thirdLogonName);
        if (!TextUtils.isEmpty(platformName)) {
            if (QQ.NAME.equals(platformName)) {
                checkIsBinding.addRequestParam(APIKey.USER_QQ_IDENTIFY, identify);
            } else if (Wechat.NAME.equals(platformName)) {
                checkIsBinding.addRequestParam(APIKey.USER_WEIXIN_IDENTIFY, identify);
            } else if (SinaWeibo.NAME.equals(platformName)) {
                checkIsBinding.addRequestParam(APIKey.USER_WEIBO_IDENTIFY, identify);
            }

            addRequestAsyncTask(checkIsBinding);

            showProgressDialog("绑定中");
        }
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);

        if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            boolean isBinded = false; //是否绑定过
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        isBinded = true;
                    }
                }
            }
            if (isBinded) {
                dimissProgressDialog();
                showToast("该账户已绑定过");
            } else {
                if (null != additionalArgsMap) {
                    String platformName = TypeUtil.getString(additionalArgsMap.get("PlatformName"), "");
                    String identify = TypeUtil.getString(additionalArgsMap.get("PlatformIdentify"), "");
                    String thirdLogonName = TypeUtil.getString(additionalArgsMap.get("ThirdLogonName"), "");

                    bindThirdAccount(platformName, identify, thirdLogonName);
                } else {
                    dimissProgressDialog();
                    showToast("账户绑定失败");
                }
            }
        } else if (InterfaceConstant.REQUEST_ID_USER_THIRD_LOGON.equals(requestID)) {
            boolean isBindSuccessfully = false;
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    Map<String, Object> userMap = TypeUtil.getMap(resultMap.get(APIKey.USER));
                    if (null != userMap) {
                        UserEntity userEntity = EntityUtils.getUserEntity(userMap);
                        if (null != userEntity) {
                            isBindSuccessfully = true;
                            TpqApplication.getInstance().setUser(userEntity);

                            logonUser = userEntity; //绑定成重置UI
                            initAccountInfo();

                            showToast("绑定成功");
                        }
                    }
                }
            }

            if (!isBindSuccessfully) {
                showToast("绑定失败");
            }

            dimissProgressDialog();
        }
    }

    private void bindThirdAccount(String platformName, String platformUserId, String thirdLogonName) {
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
            String userId = TpqApplication.getInstance().getUserId();
            thirdPartyLogonRequest.addRequestParam(APIKey.USER_ID, userId);

            addRequestAsyncTask(thirdPartyLogonRequest);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BIND_MOBILE) {
            if (resultCode == RESULT_OK) {
                logonUser = TpqApplication.getInstance().getUser();

                initAccountPhone();
                
            }
        }
    }
}
