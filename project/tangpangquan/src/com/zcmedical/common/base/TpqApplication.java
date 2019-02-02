package com.zcmedical.common.base;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.easemob.EMCallBack;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zcmedical.common.constant.APIKey;
import com.zcmedical.common.constant.CommonConstant;
import com.zcmedical.common.utils.CalendarUtil;
import com.zcmedical.common.utils.Validator;
import com.zcmedical.huanxin.DemoHXSDKHelper;
import com.zcmedical.huanxin.User;
import com.zcmedical.tangpangquan.entity.CircleEntity;
import com.zcmedical.tangpangquan.entity.MedicalRecordEntity;
import com.zcmedical.tangpangquan.entity.MedicalRecordPicEntity;
import com.zcmedical.tangpangquan.entity.PlanEntity;
import com.zcmedical.tangpangquan.entity.PostsEntity;
import com.zcmedical.tangpangquan.entity.UserEntity;
import com.zcmedical.tangpangquan.view.BbsBannerPager;

public class TpqApplication extends Application {

    public static SharedPreferences sharedPreferences;
    public static Editor sharedPreferencesEditor;
    public static TpqApplication mApplication;
    private static boolean isNetworkSettingsMessageEnabled = false;
    private boolean isUpdateDownloading = false;
    private boolean isLogon = false;

    private String userId;
    private UserEntity user;

    //缓冲数据
    private Map<String, Object> hotPostsListMap;
    private Map<String, Object> cirleListMap;
    private List<CircleEntity> myFollowCircle;
    private List<CircleEntity> allCircleList;
    private List<BbsBannerPager> bannerList;

    private PostsEntity showingPostsEntity;
    private CircleEntity showingCircleEntity;
    private UserEntity showingBbsUserEntity;
    private PlanEntity showingPlanEntity;
    private MedicalRecordEntity showingMedicalRecordEntity;
    private MedicalRecordPicEntity showingMedicalRecordPicEntity;
    private MedicalRecordEntity addMedicalRecordEntity; //添加了记录后把对象缓存在application，供体检主界面更新UI

    //**********************huanxin**************************//
    /**
     * 当前用户nickname,为了苹果推送不是userid而是昵称
     */
    public static String currentUserNick = "";
    public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

    public final static TpqApplication getInstance() {
        return mApplication;
    }

    public void setCurrentVersionCode(int versionCode) {
        sharedPreferencesEditor.putInt("versionCode", versionCode).commit();
    }

    public int getCurrentVersionCode() {
        return sharedPreferences.getInt("versionCode", 0);
    }

    public List<CircleEntity> getAllCircleList() {
        return allCircleList;
    }

    public void setAllCircleList(List<CircleEntity> allCircleList) {
        this.allCircleList = allCircleList;
    }

    public List<BbsBannerPager> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BbsBannerPager> bannerList) {
        this.bannerList = bannerList;
    }

    public List<CircleEntity> getMyFollowCircle() {
        return myFollowCircle;
    }

    public void setMyFollowCircle(List<CircleEntity> myFollowCircle) {
        this.myFollowCircle = myFollowCircle;
    }

    public PostsEntity getShowingPostsEntity() {
        return showingPostsEntity;
    }

    public void setShowingPostsEntity(PostsEntity showingPostsEntity) {
        this.showingPostsEntity = showingPostsEntity;
    }

    public CircleEntity getShowingCircleEntity() {
        return showingCircleEntity;
    }

    public void setShowingCircleEntity(CircleEntity showingCircleEntity) {
        this.showingCircleEntity = showingCircleEntity;
    }

    public UserEntity getShowingBbsUserEntity() {
        return showingBbsUserEntity;
    }

    public void setShowingBbsUserEntity(UserEntity showingBbsUserEntity) {
        this.showingBbsUserEntity = showingBbsUserEntity;
    }

    public PlanEntity getShowingPlanEntity() {
        return showingPlanEntity;
    }

    public void setShowingPlanEntity(PlanEntity showingPlanEntity) {
        this.showingPlanEntity = showingPlanEntity;
    }

    public MedicalRecordEntity getShowingMedicalRecordEntity() {
        return showingMedicalRecordEntity;
    }

    public void setShowingMedicalRecordEntity(MedicalRecordEntity showingMedicalRecordEntity) {
        this.showingMedicalRecordEntity = showingMedicalRecordEntity;
    }

    public MedicalRecordPicEntity getShowingMedicalRecordPicEntity() {
        return showingMedicalRecordPicEntity;
    }

    public void setShowingMedicalRecordPicEntity(MedicalRecordPicEntity showingMedicalRecordPicEntity) {
        this.showingMedicalRecordPicEntity = showingMedicalRecordPicEntity;
    }

    public MedicalRecordEntity getAddMedicalRecordEntity() {
        return addMedicalRecordEntity;
    }

    public void setAddMedicalRecordEntity(MedicalRecordEntity addMedicalRecordEntity) {
        this.addMedicalRecordEntity = addMedicalRecordEntity;
    }

    public Map<String, Object> getHotPostsListMap() {
        return hotPostsListMap;
    }

    public void setHotPostsList(List<PostsEntity> postsList, int toBeContinued) {
        if (null == hotPostsListMap) {
            hotPostsListMap = new HashMap<String, Object>();
        }
        hotPostsListMap.put(CommonConstant.KEY_DATA_LIST, postsList);
        hotPostsListMap.put(CommonConstant.KEY_TO_BE_CONTINUED, toBeContinued);
    }

    public Map<String, Object> getCircleListMap() {
        return cirleListMap;
    }

    public void setCircleList(List<CircleEntity> circleList, int toBeContinued) {
        if (null == cirleListMap) {
            cirleListMap = new HashMap<String, Object>();
        }
        cirleListMap.put(CommonConstant.KEY_DATA_LIST, circleList);
        cirleListMap.put(CommonConstant.KEY_TO_BE_CONTINUED, toBeContinued);
    }

    public boolean isUpdateDownloading() {
        return isUpdateDownloading;
    }

    public void setUpdateDownloading(boolean isUpdateDownloading) {
        this.isUpdateDownloading = isUpdateDownloading;
    }

    public boolean isLogon() {
        if (!this.isLogon) {
            this.isLogon = sharedPreferences.getBoolean("isLogon", false);
        }
        return this.isLogon;
    }

    public void setLogon(boolean isLogon) {
        this.isLogon = isLogon;
        sharedPreferencesEditor.putBoolean("isLogon", isLogon);
        sharedPreferencesEditor.commit();
    }

    public String getUserId() {
        if (null == userId) {
            userId = sharedPreferences.getString(APIKey.USER_ID, "");
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUser(UserEntity user) {
        if (null != user) {
            this.user = user;

            this.userId = user.getId();
            if (Validator.isIdValid(userId)) {
                setLogon(true);

                sharedPreferencesEditor.putString(APIKey.USER_ID, user.getId());
                sharedPreferencesEditor.putString(APIKey.USER_NICKNAME, user.getNickname());
                sharedPreferencesEditor.putString(APIKey.USER_USERNAME, user.getUsername());
                sharedPreferencesEditor.putString(APIKey.USER_BIRTHDAY, user.getBirthday());
                sharedPreferencesEditor.putString(APIKey.USER_CITY, user.getCity());
                sharedPreferencesEditor.putString(APIKey.USER_CREATED_AT, user.getCreatedAt());
                sharedPreferencesEditor.putString(APIKey.USER_HEAD_PIC, user.getHeadPic());
                sharedPreferencesEditor.putString(APIKey.USER_MOBILE, user.getMobile());
                sharedPreferencesEditor.putString(APIKey.USER_QQ, user.getQq());
                sharedPreferencesEditor.putString(APIKey.USER_WEIBO, user.getWeibo());
                sharedPreferencesEditor.putString(APIKey.USER_WEIXIN, user.getWeixin());
                sharedPreferencesEditor.putInt(APIKey.USER_LEVEL, user.getLevel());
                sharedPreferencesEditor.putInt(APIKey.USER_SEX, user.getSex());
                sharedPreferencesEditor.putInt(APIKey.USER_MARITAL_STATUS, user.getMaritalStatus());
                sharedPreferencesEditor.putInt(APIKey.USER_TARGET_WEIGHT, user.getTargetWeight());
                sharedPreferencesEditor.putInt(APIKey.USER_HEIGHT, user.getHeight());
                sharedPreferencesEditor.putInt(APIKey.USER_INTEGRAL, user.getIntegral());
                sharedPreferencesEditor.putInt(APIKey.USER_COLLECTIONS_COUNT, user.getCollectionsCount());
                sharedPreferencesEditor.putInt(APIKey.USER_FANS_COUNT, user.getFansCount());
                sharedPreferencesEditor.putInt(APIKey.USER_FOLLOWS_COUNT, user.getFollowsCount());
                sharedPreferencesEditor.putInt(APIKey.USER_THREADS_COUNT, user.getPostsCount());
                sharedPreferencesEditor.putInt(APIKey.USER_FORUMS_COUNT, user.getFollowsCount());
                if (!TextUtils.isEmpty(user.getPswMD5())) {
                    sharedPreferencesEditor.putString(APIKey.USER_PASSWORD + user.getId(), user.getPswMD5()); //存储密码MD5值，KEY为“password”+userId
                }
                sharedPreferencesEditor.commit();
            }
        }

    }

    public UserEntity getUser() {
        if (null == this.user) {
            user = new UserEntity();

            user.setId(sharedPreferences.getString(APIKey.USER_ID, ""));
            user.setNickname(sharedPreferences.getString(APIKey.USER_NICKNAME, ""));
            user.setUsername(sharedPreferences.getString(APIKey.USER_USERNAME, ""));
            user.setBirthday(sharedPreferences.getString(APIKey.USER_BIRTHDAY, ""));
            user.setCity(sharedPreferences.getString(APIKey.USER_CITY, ""));
            user.setCreatedAt(sharedPreferences.getString(APIKey.USER_CREATED_AT, ""));
            user.setHeadPic(sharedPreferences.getString(APIKey.USER_HEAD_PIC, ""));
            user.setMobile(sharedPreferences.getString(APIKey.USER_MOBILE, ""));
            user.setQq(sharedPreferences.getString(APIKey.USER_QQ, ""));
            user.setWeibo(sharedPreferences.getString(APIKey.USER_WEIBO, ""));
            user.setWeixin(sharedPreferences.getString(APIKey.USER_WEIXIN, ""));
            user.setLevel(sharedPreferences.getInt(APIKey.USER_LEVEL, 1));
            user.setSex(sharedPreferences.getInt(APIKey.USER_SEX, 0));
            user.setHeight(sharedPreferences.getInt(APIKey.USER_HEIGHT, 0));
            user.setTargetWeight(sharedPreferences.getInt(APIKey.USER_TARGET_WEIGHT, 0));
            user.setMaritalStatus(sharedPreferences.getInt(APIKey.USER_MARITAL_STATUS, 0));
            user.setIntegral(sharedPreferences.getInt(APIKey.USER_INTEGRAL, 0));
            user.setCollectionsCount(sharedPreferences.getInt(APIKey.USER_COLLECTIONS_COUNT, 0));
            user.setFansCount(sharedPreferences.getInt(APIKey.USER_FANS_COUNT, 0));
            user.setFollowsCount(sharedPreferences.getInt(APIKey.USER_FOLLOWS_COUNT, 0));
            user.setPostsCount(sharedPreferences.getInt(APIKey.USER_THREADS_COUNT, 0));
            user.setCircleCount(sharedPreferences.getInt(APIKey.USER_FORUMS_COUNT, 0));

        }
        //读取密码MD5值，KEY为“password”+userId,因为服务器不返回密码MD5值，所以每次从本地读取
        user.setPswMD5(sharedPreferences.getString(APIKey.USER_PASSWORD + user.getId(), ""));

        return this.user;
    }

    public void resetNetworkSettingsMessageEnabled() {
        TpqApplication.isNetworkSettingsMessageEnabled = true;
    }

    public void showNetworkSettingsMessage() {

        if (isNetworkSettingsMessageEnabled) {
            isNetworkSettingsMessageEnabled = false;
            Toast toast = Toast.makeText(this, "网络异常，请检查网络设置。", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public boolean isUserFollowed(String userId) {
        return sharedPreferences.getBoolean(getKey("followUser", userId), false);
    }

    public void setUserFollowed(String userId, boolean isFollowed) {
        sharedPreferencesEditor.putBoolean(getKey("followUser", userId), isFollowed).commit();
    }

    public boolean isCircleFollowed(String circleId) {
        return sharedPreferences.getBoolean(getKey("followCircle", circleId), false);
    }

    public void setCircleFollowed(String circleId, boolean isFollowed) {
        sharedPreferencesEditor.putBoolean(getKey("followCircle", circleId), isFollowed).commit();
    }

    public boolean isPostsLikes(String postsId) {
        return sharedPreferences.getBoolean(getKey("postsLikes", postsId), false);
    }

    public void setPostsLikes(String postsId, boolean isLikes) {
        sharedPreferencesEditor.putBoolean(getKey("postsLikes", postsId), isLikes).commit();
    }

    public boolean isPostsCollected(String postsId) {
        return sharedPreferences.getBoolean(getKey("postsCollected", postsId), false);
    }

    public void setPostsCollected(String postsId, boolean isCollected) {
        sharedPreferencesEditor.putBoolean(getKey("postsCollected", postsId), isCollected).commit();
    }

    public boolean isPostsCommentLike(String postsCommentId) {
        return sharedPreferences.getBoolean(getKey("postsCommentLike", postsCommentId), false);
    }

    public void setPostsCommentLike(String postsCommentId, boolean isCommentLike) {
        sharedPreferencesEditor.putBoolean(getKey("postsCommentLike", postsCommentId), isCommentLike).commit();
    }

    public void setTodayRequestIdentifyingCodeCount(int count) {
        Date today = new Date();
        String date = CommonConstant.serverDateFormat.format(today);
        sharedPreferencesEditor.putInt(date + "RequestIdentifyingCode", count).commit();
    }

    public int getTodayRequestIdentifyingCodeCount() {
        Date today = new Date();
        String date = CommonConstant.serverDateFormat.format(today);
        return sharedPreferences.getInt(date + "RequestIdentifyingCode", 0);
    }

    public String getKey(String modelName, String modelId) {
        return userId + "_" + modelName + "_" + modelId;
    }

    public Date getUpdateTriggerTime() {
        return CalendarUtil.strToDate(sharedPreferences.getString("updateTriggerTime", ""));
    }

    public void setUpdateTriggerTime(Date updateTriggerTime) {
        sharedPreferencesEditor.putString("updateTriggerTime", CommonConstant.serverTimeFormat.format(updateTriggerTime));
        sharedPreferencesEditor.commit();
    }

    public static TpqApplication getInstance(Context context) {
        return (TpqApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initImageLoader(getApplicationContext());

        ShareSDK.initSDK(this);

        sharedPreferences = getSharedPreferences(TpqApplication.class.getName(), 0);
        sharedPreferencesEditor = sharedPreferences.edit();

        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related
         *         APIs after calling onInit, otherwise false.
         * 
         *         环信初始化SDK帮助函数
         *         返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         *         for example: 例子：
         * 
         *         public class DemoHXSDKHelper extends HXSDKHelper
         * 
         *         HXHelper = new DemoHXSDKHelper();
         *         if(HXHelper.onInit(context)){ // do HuanXin related work }
         */
        hxSDKHelper.onInit(mApplication);
        //if(spf.isLogon()){
        //  currentUserNick = spf.getUserNickName();
        //}
    }

    /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, User> getContactList() {
        return hxSDKHelper.getContactList();
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, User> contactList) {
        hxSDKHelper.setContactList(contactList);
    }

    /**
     * 获取当前登录用户名
     *
     * @return
     */
    public String getUserName() {
        return hxSDKHelper.getHXId();
    }

    /**
     * 获取密码
     *
     * @return
     */
    public String getPassword() {
        return hxSDKHelper.getPassword();
    }

    /**
     * 设置用户名
     *
     * @paramuser
     */
    public void setHxUserName(String username) {
        hxSDKHelper.setHXId(username);
    }

    /**
     * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
     * 内部的自动登录需要的密码，已经加密存储了
     *
     * @param pwd
     */
    public void setHxPassword(String pwd) {
        hxSDKHelper.setPassword(pwd);
    }

    /**
     * 退出登录,清空数据
     */
    public void logout(final EMCallBack emCallBack) {
        // 先调用sdk logout，在清理app中自己的数据
        hxSDKHelper.logout(emCallBack);
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
