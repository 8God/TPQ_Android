package com.zcmedical.tangpangquan.entity;

import com.zcmedical.tangpangquan.adapter.DocterTeamAdapter;

public class DoctorTeamView {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItem_type() {
        return item_type;
    }

    public void setItem_type(int item_type) {
        this.item_type = item_type;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    private String title = "";

    private int item_type;
    private Doctor doctor;

    public DoctorTeamView(String title) {
        this.item_type = DocterTeamAdapter.ITEM_TITLE;
        this.title = title;
    }
    
    public DoctorTeamView(Doctor doctor) {
        this.item_type = DocterTeamAdapter.ITEM_CONTENT;
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "DoctorTeamView [title=" + title + ", item_type=" + item_type + ", doctor=" + doctor + "]";
    }
}
