package com.zcmedical.tangpangquan.entity;

public class PostCollectionEntity {

    private String id;
    private String createdAt;
    private UserEntity user;
    private PostsEntity post;

    private boolean isSelectToDelete = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public PostsEntity getPost() {
        return post;
    }

    public void setPost(PostsEntity post) {
        this.post = post;
    }

    public boolean isSelectToDelete() {
        return isSelectToDelete;
    }

    public void setSelectToDelete(boolean isSelectToDelete) {
        this.isSelectToDelete = isSelectToDelete;
    }

}
