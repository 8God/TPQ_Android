package com.zcmedical.tangpangquan.entity;

import java.util.List;

public class PostsEntity {
    /*
          帖子 thread
        ID id【必填】       
                标题 title【必填】    
                内容 content
                用户ID user_id【必填】        
                圈子ID forum_id【必填】       
                帖子图片 thread_pic
                帖子状态 thread_status      
                创建时间 created_at
                帖子浏览次数 views_count
                帖子评论数量 comments_count
                帖子收藏次数 collections_count
                帖子点赞次数 likes_count
                帖子是否精华 essence
                帖子是否热门 hot
                帖子是否置顶 top
                
     */

    private String id;
    private String title;
    private String content;
    private String userId;
    private String forumId;
    private List<PicEntity> postsPicUrls;
    private String status;
    private String createdAt;
    private int viewsCount; //views_count 
    private int commentCount; //comments_count 
    private int collecttionCount; //collections_count 
    private int likesCount; //likes_count 
    private boolean isEssence; //thread_type 
    private boolean isHot; //thread_type 
    private boolean isTop; //thread_type 
    private UserEntity user;
    private CircleEntity circle; //forum

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public List<PicEntity> getPostsPicUrls() {
        return postsPicUrls;
    }

    public void setPostsPicUrls(List<PicEntity> postsPicUrls) {
        this.postsPicUrls = postsPicUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CircleEntity getCircle() {
        return circle;
    }

    public void setCircle(CircleEntity circle) {
        this.circle = circle;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCollecttionCount() {
        return collecttionCount;
    }

    public void setCollecttionCount(int collecttionCount) {
        this.collecttionCount = collecttionCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isEssence() {
        return isEssence;
    }

    public void setEssence(boolean isEssence) {
        this.isEssence = isEssence;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean isHot) {
        this.isHot = isHot;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean isTop) {
        this.isTop = isTop;
    }

}
