package com.zcmedical.tangpangquan.entity;

import java.util.List;

public class PlanEntity {
    //字段
    //    id
    //    title 标题
    //    day_count 学习天数
    //    follow_count 学习用户数
    //    pros 利
    //    cons 弊
    //    description  方法描述
    //    preview  预览
    //    created_at 创建时间

    private String id;
    private String title;
    private int dayCount;
    private int followCount;
    private String pros;
    private String cons;
    private String description;
    private String preview;
    private String createdAt;
    private List<PlanDetailEntity> planDetailList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public String getPros() {
        return pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getCons() {
        return cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String created_at) {
        this.createdAt = created_at;
    }

    public List<PlanDetailEntity> getPlanDetailList() {
        return planDetailList;
    }

    public void setPlanDetailList(List<PlanDetailEntity> planDetailList) {
        this.planDetailList = planDetailList;
    }

}
