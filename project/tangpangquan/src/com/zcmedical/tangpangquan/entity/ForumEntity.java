package com.zcmedical.tangpangquan.entity;

public class ForumEntity {
    /**
     *  ID id【必填】   
                    标题 title【必填】    
                    描述 description
                    圈子粉丝数量 users_count
                    圈子帖子数量 threads_count
                    创建时间 created_at
     */
    
    private String id;
    private String title;
    private String description;
    private String createdSAt;
    private int usersCount;
    private int threadCount;

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

    public String getCreatedSAt() {
        return createdSAt;
    }

    public void setCreatedSAt(String createdSAt) {
        this.createdSAt = createdSAt;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
    
}
