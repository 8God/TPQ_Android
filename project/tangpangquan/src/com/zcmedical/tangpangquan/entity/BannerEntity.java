package com.zcmedical.tangpangquan.entity;

public class BannerEntity {

    private String id;
    private String bannerBgUrl;
    private String contentUrl;
    private String bannerTitle;
    private String createdAt;
    private int status;

    private String description;

    public String getBannerBgUrl() {
        return bannerBgUrl;
    }

    public void setBannerBgUrl(String bannerBgUrl) {
        this.bannerBgUrl = bannerBgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
