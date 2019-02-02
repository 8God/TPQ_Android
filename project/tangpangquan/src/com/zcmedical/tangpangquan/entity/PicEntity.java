package com.zcmedical.tangpangquan.entity;

public class PicEntity {
    /*
     *  ID id   
            帖子评论ID thread_comment_id
            帖子评论图片 thread_comment_pic
            创建时间 created_at 
     * */
    
    private String id;
    private String postsId;
    private String commentId;
    private String picUrl;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostsId() {
        return postsId;
    }

    public void setPostsId(String postsId) {
        this.postsId = postsId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
