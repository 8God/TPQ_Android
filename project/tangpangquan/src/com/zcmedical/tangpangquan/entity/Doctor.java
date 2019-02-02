package com.zcmedical.tangpangquan.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by issaclam on 15/6/16.
 */
public class Doctor {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * comments_count : a 医生评价数量
     * nickname : 郭富城  昵称
     * fans_count : a  医生粉丝数量
     * head_pic : a  头像
     * department : a 所在科室
     * recommended : a 推荐指数
     * education : a 教育背景
     * pass_attitude : a 高于服务
     * password : 123456 密码
     * attitude : a 服务态度
     * pass_plane : a 高于水平
     * plane : a 医疗水平
     * skill : a 擅长领域
     * job_title : a 医生职位
     * prize : a 获奖介绍
     * pass_recommended : a  高于推荐
     * created_at : a 创建时间
     * identifier : 79877 医生编号
     * hospital : 军区总医院 所在医院
     * mobile : 18675686166 电话
     * hospital_tel : a 医院电话
     * "id":1316275482,"
     * 
     * 新增的：
     * "doctor_team_id": "1214097028",
                "doctor_team": {
                    "id": 1214097028,
                    "team_name": "你好医疗团队",
                    "created_at": "20150627194925"
                }
     */

    @SerializedName("doctor_team")
    private DoctorTeam doctor_team;
    public DoctorTeam getDoctor_team() {
        return doctor_team;
    }

    public void setDoctor_team(DoctorTeam doctor_team) {
        this.doctor_team = doctor_team;
    }

    @SerializedName("id")
    private String id;
    @SerializedName("comments_count")
    private String comments_count;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("fans_count")
    private String fans_count;
    @SerializedName("head_pic")
    private String head_pic;
    @SerializedName("department")
    private String department;
    @SerializedName("recommended")
    private String recommended;
    @SerializedName("education")
    private String education;
    @SerializedName("pass_attitude")
    private String pass_attitude;
    @SerializedName("password")
    private String password;
    @SerializedName("attitude")
    private String attitude;
    @SerializedName("pass_plane")
    private String pass_plane;
    @SerializedName("plane")
    private String plane;
    @SerializedName("skill")
    private String skill;
    @SerializedName("job_title")
    private String job_title;
    @SerializedName("prize")
    private String prize;
    @SerializedName("pass_recommended")
    private String pass_recommended;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("hospital")
    private String hospital;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("hospital_tel")
    private String hospital_tel;

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFans_count(String fans_count) {
        this.fans_count = fans_count;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setRecommended(String recommended) {
        this.recommended = recommended;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setPass_attitude(String pass_attitude) {
        this.pass_attitude = pass_attitude;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAttitude(String attitude) {
        this.attitude = attitude;
    }

    public void setPass_plane(String pass_plane) {
        this.pass_plane = pass_plane;
    }

    public void setPlane(String plane) {
        this.plane = plane;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public void setPass_recommended(String pass_recommended) {
        this.pass_recommended = pass_recommended;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setHospital_tel(String hospital_tel) {
        this.hospital_tel = hospital_tel;
    }

    public String getComments_count() {
        return comments_count;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFans_count() {
        return fans_count;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public String getDepartment() {
        return department;
    }

    public String getRecommended() {
        return recommended;
    }

    public String getEducation() {
        return education;
    }

    public String getPass_attitude() {
        return pass_attitude;
    }

    public String getPassword() {
        return password;
    }

    public String getAttitude() {
        return attitude;
    }

    public String getPass_plane() {
        return pass_plane;
    }

    public String getPlane() {
        return plane;
    }

    public String getSkill() {
        return skill;
    }

    public String getJob_title() {
        return job_title;
    }

    public String getPrize() {
        return prize;
    }

    public String getPass_recommended() {
        return pass_recommended;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getHospital() {
        return hospital;
    }

    public String getMobile() {
        return mobile;
    }

    public String getHospital_tel() {
        return hospital_tel;
    }

    @Override
    public String toString() {
        return "Doctor{" + "id='" + id + '\'' + ", comments_count='" + comments_count + '\'' + ", nickname='" + nickname + '\'' + ", fans_count='" + fans_count + '\'' + ", head_pic='" + head_pic
                + '\'' + ", department='" + department + '\'' + ", recommended='" + recommended + '\'' + ", education='" + education + '\'' + ", pass_attitude='" + pass_attitude + '\''
                + ", password='" + password + '\'' + ", attitude='" + attitude + '\'' + ", pass_plane='" + pass_plane + '\'' + ", plane='" + plane + '\'' + ", skill='" + skill + '\''
                + ", job_title='" + job_title + '\'' + ", prize='" + prize + '\'' + ", pass_recommended='" + pass_recommended + '\'' + ", created_at='" + created_at + '\'' + ", identifier="
                + identifier + ", hospital='" + hospital + '\'' + ", mobile=" + mobile + ", hospital_tel='" + hospital_tel + '\'' + '}';
    }
}
