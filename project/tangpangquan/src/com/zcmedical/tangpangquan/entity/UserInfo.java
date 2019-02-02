package com.zcmedical.tangpangquan.entity;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    /**
     * nickname : 用户161863928
     * status : 1
     * fans_count : 0
     * forums_count : 0
     * password : 123456
     * id : 161863928
     * follows_count : 0
     * threads_count : 0
     * level : 1
     * collections_count : 0
     * weixin :
     * created_at : 20150606123042
     * marital_status : 0
     * mobile : 18675686168
     *
     * username
     * head_pic
     * sex
     * birthday
     * city
     * qq
     * weibo
     * age
     * 
     *  private int height;
    private int target_weight;
     */
    @SerializedName("height")
    private int height;
    @SerializedName("target_weight")
    private int target_weight;
    @SerializedName("age")
    private String age;
    @SerializedName("username")
    private String username;
    @SerializedName("head_pic")
    private String head_pic;
    @SerializedName("sex")
    private String sex;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("city")
    private String city;
    @SerializedName("qq")
    private String qq;
    @SerializedName("weibo")
    private String weibo;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTarget_weight() {
        return target_weight;
    }

    public void setTarget_weight(int target_weight) {
        this.target_weight = target_weight;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    @SerializedName("nickname")
    private String nickname;
    @SerializedName("status")
    private int status;
    @SerializedName("fans_count")
    private int fans_count;
    @SerializedName("forums_count")
    private int forums_count;
    @SerializedName("password")
    private String password;
    @SerializedName("id")
    private int id;
    @SerializedName("follows_count")
    private int follows_count;
    @SerializedName("threads_count")
    private int threads_count;
    @SerializedName("level")
    private int level;
    @SerializedName("collections_count")
    private int collections_count;
    @SerializedName("weixin")
    private String weixin;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("marital_status")
    private int marital_status;
    @SerializedName("mobile")
    private String mobile;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public void setForums_count(int forums_count) {
        this.forums_count = forums_count;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFollows_count(int follows_count) {
        this.follows_count = follows_count;
    }

    public void setThreads_count(int threads_count) {
        this.threads_count = threads_count;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCollections_count(int collections_count) {
        this.collections_count = collections_count;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setMarital_status(int marital_status) {
        this.marital_status = marital_status;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public int getStatus() {
        return status;
    }

    public int getFans_count() {
        return fans_count;
    }

    public int getForums_count() {
        return forums_count;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public int getFollows_count() {
        return follows_count;
    }

    public int getThreads_count() {
        return threads_count;
    }

    public int getLevel() {
        return level;
    }

    public int getCollections_count() {
        return collections_count;
    }

    public String getWeixin() {
        return weixin;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getMarital_status() {
        return marital_status;
    }

    public String getMobile() {
        return mobile;
    }

    @Override
    public String toString() {
        return "UserInfo [height=" + height + ", target_weight=" + target_weight + ", age=" + age + ", username=" + username + ", head_pic=" + head_pic + ", sex=" + sex + ", birthday=" + birthday
                + ", city=" + city + ", qq=" + qq + ", weibo=" + weibo + ", nickname=" + nickname + ", status=" + status + ", fans_count=" + fans_count + ", forums_count=" + forums_count
                + ", password=" + password + ", id=" + id + ", follows_count=" + follows_count + ", threads_count=" + threads_count + ", level=" + level + ", collections_count=" + collections_count
                + ", weixin=" + weixin + ", created_at=" + created_at + ", marital_status=" + marital_status + ", mobile=" + mobile + "]";
    }

}
