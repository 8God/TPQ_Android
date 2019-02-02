package com.zcmedical.tangpangquan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class AccountSafetyActivity extends BaseActivity implements OnClickListener {

    private UserEntity logonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_safety);

        init();
    }

    private void init() {
        initToolbar("账号安全");
        initData();
        if (null != logonUser) {
            initUI();
        }
    }

    private void initData() {
        logonUser = TpqApplication.getInstance().getUser();
    }

    private void initUI() {
        initAccountSafetyUI();
        initLogonAccountUI();

        setViewClickListener(R.id.rl_account_safety, this);
        setViewClickListener(R.id.rl_modify_password, this);
    }

    private void initAccountSafetyUI() {
        String mobile = logonUser.getMobile();
        String wechat = logonUser.getWeixin();
        String qq = logonUser.getQq();
        String weibo = logonUser.getWeibo();

        if (!TextUtils.isEmpty(mobile)) {
            findView(R.id.imv_accountphone).setVisibility(View.VISIBLE);
        } else {
            findView(R.id.imv_accountphone).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(wechat)) {
            findView(R.id.imv_accountwechat).setVisibility(View.VISIBLE);
        } else {
            findView(R.id.imv_accountwechat).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(qq)) {
            findView(R.id.imv_accountqq).setVisibility(View.VISIBLE);
        } else {
            findView(R.id.imv_accountqq).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(weibo)) {
            findView(R.id.imv_accountweibo).setVisibility(View.VISIBLE);
        } else {
            findView(R.id.imv_accountweibo).setVisibility(View.GONE);
        }
    }

    private void initLogonAccountUI() {
        setTextView(R.id.tv_logon_account, logonUser.getMobile());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_account_safety:
                startActivity(new Intent(getContext(), AccountBindingActivity.class));
                break;
            case R.id.rl_modify_password:
                String mobile = "";
                if (null != logonUser) {
                    mobile = logonUser.getMobile();
                    if (Validator.isPhoneNumber(mobile)) {
                        startActivity(new Intent(getContext(), ModifyPasswordActivity.class));
                    } else {
                        showToast("您为第三方账户登陆，暂不支持修改密码，请先绑定手机");
                    }
                }
                break;

            default:
                break;
        }
    }
}
