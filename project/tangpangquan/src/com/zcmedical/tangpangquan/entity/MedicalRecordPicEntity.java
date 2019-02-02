package com.zcmedical.tangpangquan.entity;

public class MedicalRecordPicEntity {
    private String id;
    private String userId;
    private UserEntity user;
    private String medicalRecordId;
    private String medicalRecordPic;
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public String getMedicalRecordPic() {
        return medicalRecordPic;
    }

    public void setMedicalRecordPic(String medicalRecordPic) {
        this.medicalRecordPic = medicalRecordPic;
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

}
