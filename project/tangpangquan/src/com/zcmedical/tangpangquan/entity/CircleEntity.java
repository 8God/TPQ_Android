package com.zcmedical.tangpangquan.entity;

public class CircleEntity {
    /*
        圈子 forum
      ID id【必填】   
            标题 title【必填】    
            描述 description
            圈子logo forum_pic
            创建时间 created_at
    */

    private String id;
    private String title;
    private String description;
    private String forumLogoUrl;
    private String createdAt;
    private int userCount;
    private int postsCount;
    private UserEntity circleAdmin;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getForumLogoUrl() {
        return forumLogoUrl;
    }

    public void setForumLogoUrl(String forumLogoUrl) {
        this.forumLogoUrl = forumLogoUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public UserEntity getCircleAdmin() {
        return circleAdmin;
    }

    public void setCircleAdmin(UserEntity circleAdmin) {
        this.circleAdmin = circleAdmin;
    }

}
