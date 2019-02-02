package com.zcmedical.tangpangquan.entity;

import java.util.List;

public class CommentEntity {
    /*
            帖子评论 thread_comment
        ID id【必填】   
                评论内容 content【必填】    
                帖子ID thread_id【必填】  
                用户ID user_id【必填】    
                评论状态 comment_status
                创建时间 created_at
    */

    private String id;
    private String content;
    private String threadId;
    private String userId;
    private String createdAt;
    private int commentStatus;
    private int commentLikeCount;
    private List<PicEntity> commentPics;
    private UserEntity user;
    private CommentEntity parentComment; //父评论thread_comment
    private PostsEntity posts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(int commentStatus) {
        this.commentStatus = commentStatus;
    }

    public int getCommentLikeCount() {
        return commentLikeCount;
    }

    public void setCommentLikeCount(int commentLikeCount) {
        this.commentLikeCount = commentLikeCount;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<PicEntity> getCommentPics() {
        return commentPics;
    }

    public void setCommentPics(List<PicEntity> commentPics) {
        this.commentPics = commentPics;
    }

    public CommentEntity getParentComment() {
        return parentComment;
    }

    public void setParentComment(CommentEntity parentComment) {
        this.parentComment = parentComment;
    }

    public PostsEntity getPosts() {
        return posts;
    }

    public void setPosts(PostsEntity posts) {
        this.posts = posts;
    }

}
