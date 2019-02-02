package com.zcmedical.tangpangquan.fragment;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.zcmedical.common.base.BaseFragment;
import com.zcmedical.common.base.TpqApplication;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.constant.InterfaceConstant;
import com.zcmedical.common.network.CommonRequest;
import com.zcmedical.common.update.UpdateUtils;
import com.zcmedical.common.utils.CounterUtils;
import com.zcmedical.common.utils.EntityUtils;
import com.zcmedical.common.utils.TypeUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.common.utils.VersionUtils;
import com.zcmedical.tangpangquan.R;
import com.zcmedical.tangpangquan.activity.AboutUsActivity;
import com.zcmedical.tangpangquan.activity.BloodSugarActivity;
import com.zcmedical.tangpangquan.activity.ChatHistoryActivity;
import com.zcmedical.tangpangquan.activity.FeedbackActivity;
import com.zcmedical.tangpangquan.activity.MyCircleListActivity;
import com.zcmedical.tangpangquan.activity.MyFansActivity;
import com.zcmedical.tangpangquan.activity.MyFollowActivity;
import com.zcmedical.tangpangquan.activity.MyPostsCollectionActivity;
import com.zcmedical.tangpangquan.activity.RemindMainActivity;
import com.zcmedical.tangpangquan.activity.UserInfoDetailActivity;
import com.zcmedical.tangpangquan.activity.WeightMainActivity;
import com.zcmedical.tangpangquan.entity.UserEntity;

public class PersonFragment extends BaseFragment implements OnClickListener {

    private static final String SHARE_PAGE_URL = CommonConstant.HTTP_HOST + "/tpq/h5/about";

    public static final int REQUEST_CODE_UPDATE_USER_INFO = 10;

    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_person, null);

        init();

        return contentView;
    }

    private void init() {
        fetchNewestUserInfo();
        setMenuVisibility(true);
        setHasOptionsMenu(true);
        initUserInfo();
        initItemClickListener();
        initVersionName();
    }

    private void initUserInfo() {
        UserEntity user = TpqApplication.getInstance().getUser();

        if (null != user) {
            String nickName = user.getNickname();
            if (TextUtils.isEmpty(nickName)) {
                nickName = getActivity().getString(R.string.text_no_nickname);
            }
            setTextView(contentView, R.id.tv_user_name, nickName);
            setTextView(contentView, R.id.tv_user_level, "Lv." + user.getLevel());
            int sex = user.getSex();
            switch (sex) {
                case 0:
                    setTextView(contentView, R.id.tv_user_sex, "男");
                    break;
                case 1:
                    setTextView(contentView, R.id.tv_user_sex, "女");
                    break;
                default:
                    setTextView(contentView, R.id.tv_user_sex, "男");
                    break;
            }
            Log.i("cth", "user.getFollowsCount = " + user.getFollowsCount());
            Log.i("cth", "user.getFansCount = " + user.getFansCount());
            Log.i("cth", "user.getCircleCount = " + user.getCircleCount());
            setTextView(contentView, R.id.tv_user_follow_count, CounterUtils.format(user.getFollowsCount() < 0 ? 0 : user.getFollowsCount()));
            setTextView(contentView, R.id.tv_user_fans_count, CounterUtils.format(user.getFansCount() < 0 ? 0 : user.getFansCount()));
            setTextView(contentView, R.id.tv_user_follow_circle_count, CounterUtils.format(user.getCircleCount() < 0 ? 0 : user.getCircleCount()));

            setImageView(contentView, R.id.cimv_user_head, user.getHeadPic(), R.drawable.common_icon_default_user_head);
        }
    }

    private void initItemClickListener() {
        setViewClickListener(contentView, R.id.ll_user_follow_count, this);
        setViewClickListener(contentView, R.id.ll_user_fans_count, this);
        setViewClickListener(contentView, R.id.ll_user_follow_circle_count, this);

        setViewClickListener(contentView, R.id.rl_my_weight, this);
        setViewClickListener(contentView, R.id.rl_my_blood_glucose, this);
        setViewClickListener(contentView, R.id.rl_my_question, this);
        setViewClickListener(contentView, R.id.rl_my_collection, this);
        setViewClickListener(contentView, R.id.rl_my_remind, this);
        setViewClickListener(contentView, R.id.rl_share, this);
        setViewClickListener(contentView, R.id.rl_feedback, this);
        setViewClickListener(contentView, R.id.rl_about_us, this);
        setViewClickListener(contentView, R.id.rl_check_version, this);

    }

    private void initVersionName() {
        String versionName = VersionUtils.getVersionName(getActivity());

        setTextView(contentView, R.id.tv_current_version, "（当前 v" + versionName + "）");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_modify_user_info, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_modify_user_info:
                Intent openUseInfo = new Intent(getActivity(), UserInfoDetailActivity.class);
                startActivityForResult(openUseInfo, REQUEST_CODE_UPDATE_USER_INFO);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_follow_count:
                Intent openMyFollow = new Intent(getActivity(), MyFollowActivity.class);
                startActivity(openMyFollow);
                break;
            case R.id.ll_user_fans_count:
                Intent openMyFans = new Intent(getActivity(), MyFansActivity.class);
                startActivity(openMyFans);
                break;
            case R.id.ll_user_follow_circle_count:
                Intent openMyCircleList = new Intent(getActivity(), MyCircleListActivity.class);
                startActivity(openMyCircleList);
                break;
            case R.id.rl_my_weight:
                Intent openMyWeight = new Intent(getActivity(), WeightMainActivity.class);
                startActivity(openMyWeight);
                break;
            case R.id.rl_my_blood_glucose:
                startActivity(new Intent(getActivity(), BloodSugarActivity.class));
                break;
            case R.id.rl_my_question:
                Intent openMyQuestion = new Intent(getActivity(), ChatHistoryActivity.class);
                startActivity(openMyQuestion);
                break;
            case R.id.rl_my_collection:
                startActivity(new Intent(getActivity(), MyPostsCollectionActivity.class));
                break;
            case R.id.rl_my_remind:
                startActivity(new Intent(getActivity(), RemindMainActivity.class));
                break;
            case R.id.rl_share:
                showShare();
                break;
            case R.id.rl_feedback:
                Intent openFeedback = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(openFeedback);
                break;
            case R.id.rl_about_us:
                Intent openAboutUs = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(openAboutUs);
                break;
            case R.id.rl_check_version:
                UpdateUtils.checkVersion(getActivity(), true);
                break;
            default:
                break;
        }

    }

    private void fetchNewestUserInfo() {
        String userId = TpqApplication.getInstance().getUserId();
        CommonRequest fetchUserInfo = new CommonRequest();
        fetchUserInfo.setRequestApiName(InterfaceConstant.API_USER_FETCH);
        fetchUserInfo.setRequestID(InterfaceConstant.REQUEST_ID_USER_FETCH);
        fetchUserInfo.addRequestParam(APIKey.COMMON_ID, userId);

        addRequestAsyncTask(contentView, fetchUserInfo);
    }

    @Override
    protected void onResponseAsyncTaskRender(int status, String message, int toBeContinued, Map<String, Object> resultMap, String requestID, Map<String, Object> additionalArgsMap) {
        super.onResponseAsyncTaskRender(status, message, toBeContinued, resultMap, requestID, additionalArgsMap);
        if (InterfaceConstant.REQUEST_ID_USER_FETCH.equals(requestID)) {
            if (status == APIKey.STATUS_SUCCESSFUL) {
                if (null != resultMap) {
                    List<Map<String, Object>> userMapList = TypeUtil.getList(resultMap.get(APIKey.USERS));
                    if (null != userMapList && userMapList.size() > 0) {
                        List<UserEntity> userList = EntityUtils.getUserEntityList(userMapList);
                        if (null != userList && userList.size() > 0) {
                            UserEntity user = userList.get(0);
                            if (null != user) {
                                String myUserId = TpqApplication.getInstance().getUserId();
                                String userId = user.getId();
                                if (Validator.isIdValid(myUserId) && Validator.isIdValid(myUserId) && myUserId.equals(userId)) {
                                    TpqApplication.getInstance().setUser(user);
                                    initUserInfo();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.app_name) + "是什么？");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(SHARE_PAGE_URL);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getString(R.string.app_name) + "是什么？" + "下载地址：" + SHARE_PAGE_URL);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl("https://mmbiz.qlogo.cn/mmbiz/ciaMk0usedgpCibZMaGYcJbYkkGl9YqSgAEXMXgeYY6ppddg3RV6D91lg0tjl6VGOxe7vbc2s2IUHCUuwu0mulYQ/0?wx_fmt=png");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(SHARE_PAGE_URL);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(getString(R.string.app_name) + "是什么？" + "下载地址：" + SHARE_PAGE_URL);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(SHARE_PAGE_URL);

        // 启动分享GUI
        oks.show(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_USER_INFO) {
            initUserInfo();
        }
    }
}
