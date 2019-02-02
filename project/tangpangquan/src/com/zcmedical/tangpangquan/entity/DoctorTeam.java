package com.zcmedical.tangpangquan.entity;

import java.util.List;

public class DoctorTeam {

    //    {
    //        "id": 986704154,
    //        "team_name": "张大牛医疗团队",
    //        "created_at": "20150627194618",
    //        "doctors": [
    //            {
    //                "id": 851335713,
    //                "doctor_team_id": 986704154,
    //                "nickname": "张学友",
    //                "mobile": "18675686167",
    //                "password": "123456",
    //                "head_pic": "http://121.40.148.142/tpq/data/images/fa130b481eaff3a156ba502cb5150194.jpg",
    //                "identifier": "79876",
    //                "comments_count": 0,
    //                "fans_count": 0,
    //                "created_at": "20150616201350"
    //            },
    //            {
    //                "id": 994096470,
    //                "doctor_team_id": 986704154,
    //                "nickname": "医生994096470",
    //                "mobile": "18675686164",
    //                "password": "123456",
    //                "identifier": "77889",
    //                "comments_count": 0,
    //                "fans_count": 0,
    //                "created_at": "20150616211007"
    //            }
    //        ]
    //    }

    private String id = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    private String team_name = "";
    private String created_at = "";
    private List<Doctor> doctors;

    @Override
    public String toString() {
        return "DoctorTeam [id=" + id + ", team_name=" + team_name + ", created_at=" + created_at + ", doctors=" + doctors + "]";
    }
    
    

}
