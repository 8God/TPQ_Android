package com.zcmedical.tangpangquan.entity;

public class UserEntity {
    /*
     * 用户表 user ID id 【必填】 昵称 nickname 手机 mobile 【必填】 密码 password 【必填】 头像
     * head_pic 等级 level 【1——9】 真实姓名 username 性别 sex 【0代表男， 1代表女】 婚姻状况
     * marital_status 【0代表未婚， 1代表已婚】 生日 birthday 城市 city 微信 weixin QQ qq 微博
     * weibo 创建时间 created_at 收藏数 collections_count 粉丝数 fans_count 关注数
     * follows_count 帖子数 threads_count 圈子数 forums_count
     */

    private int targetWeight;
    private int height;
    private String id;
    private String nickname;
    private String mobile;
    private String headPic;
    private int level = 1;
    private int integral;
    private int fansCount;
    private int collectionsCount;
    private int followsCount;
    private int postsCount;
    private int circleCount; //forums_count
    private String username;
    private int sex;
    private int maritalStatus;
    private String birthday;
    private String city;
    private String weixin;
    private String qq;
    private String weibo;
    private String createdAt;
    private String pswMD5;

    public int getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(int targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String head_pic) {
        this.headPic = head_pic;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 【0代表男， 1代表女】
     * 
     * @return
     */
    public int getSex() {
        return sex;
    }

    /**
     * 【0代表男， 1代表女】
     * 
     * @return
     */
    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(int marital_status) {
        this.maritalStatus = marital_status;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getCollectionsCount() {
        return collectionsCount;
    }

    public void setCollectionsCount(int collectionsCount) {
        this.collectionsCount = collectionsCount;
    }

    public int getFollowsCount() {
        return followsCount;
    }

    public void setFollowsCount(int followsCount) {
        this.followsCount = followsCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int threadsCount) {
        this.postsCount = threadsCount;
    }

    public int getCircleCount() {
        return circleCount;
    }

    public void setCircleCount(int circleCount) {
        this.circleCount = circleCount;
    }

    public String getPswMD5() {
        return pswMD5;
    }

    public void setPswMD5(String pswMD5) {
        this.pswMD5 = pswMD5;
    }

}
