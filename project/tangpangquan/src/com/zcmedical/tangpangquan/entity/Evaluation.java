package com.zcmedical.tangpangquan.entity;

import com.google.gson.annotations.SerializedName;
import com.zcmedical.tangpangquan.R;

/**
 * Created by issaclam on 15/6/14.
 */
public class Evaluation {

    public static final String[] COMMENTSTATUS = { "还不清楚", "满意", "很满意" };
    public static final int[] COMMENTCOLORS = { R.color.say_bad, R.color.say_middle, R.color.say_good };

    /**
     * content : 神医啊 id : 2041215368 comment_status : 1 doctor_id : 1316275482 created_at :
     * 20150623141730 user_id : 1619462933 comment_type : 1 user : {"nickname":"用户1619462933","status":1,"fans_count":0,"forums_count":0,"password":"123456","id":1619462933,"follows_count":0,"threads_count":0,"level":1,"collections_count":0,"age":27,"created_at":"20150606123203","marital_status":0,"mobile":"18675686167"}
     * doctor : {"id":1316275482,"comments_count":0,"nickname":"刘德华","fans_count":0,"head_pic":"http://121.40.148.142/tpq/data/images/51d24bf98c2f54443edbda730cb06284.jpg","created_at":"20150616201523","identifier":"79876","password":"123456","hospital":"军区总医院","mobile":"18675686166"}
     */
    @SerializedName("content")
    private String content = "";
    @SerializedName("id")
    private String id = "";
    @SerializedName("comment_status")
    private String comment_status = "";
    @SerializedName("doctor_id")
    private String doctor_id = "";
    @SerializedName("created_at")
    private String created_at = "";
    @SerializedName("user_id")
    private String user_id = "";
    @SerializedName("comment_type")
    private String comment_type = "";
    @SerializedName("user")
    private UserInfo user;
    @SerializedName("doctor")
    private Doctor doctor;

    public Evaluation(String id, String content, String comment_type) {
        this.id = id;
        this.content = content;
        this.comment_type = comment_type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setComment_status(String comment_status) {
        this.comment_status = comment_status;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getComment_status() {
        return comment_status;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getComment_type() {
        return comment_type;
    }

    public UserInfo getUser() {
        return user;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    @Override
    public String toString() {
        return "Evaluation{" + "content='" + content + '\'' + ", id=" + id + ", comment_status=" + comment_status + ", doctor_id='" + doctor_id + '\'' + ", created_at='" + created_at + '\''
                + ", user_id='" + user_id + '\'' + ", comment_type=" + comment_type + ", user=" + (user == null ? "" : user.toString()) + ", doctor=" + (doctor == null ? "" : doctor.toString()) + '}';
    }
}
