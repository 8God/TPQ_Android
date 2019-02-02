package com.zcmedical.tangpangquan.entity;

public class Meals {

    //    "id": 247134787,
    //    "title": "今天推荐一个",
    //    "meals_pic": "http:\\/\\/121.40.148.142\\/tpq\\/data\\/images\\/f34c9a3de246c5276a600475ae17f25c.jpg",
    //    "meals_type": 2,
    //    "meals_food": "啊沙发沙发斯蒂芬",
    //    "description": "阿凡达",
    //    "efficiency": "阿道夫",
    //    "method": "爱妃",
    //    "created_at": "20150726143543"

    private String id;

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

    public String getMeals_pic() {
        return meals_pic;
    }

    public void setMeals_pic(String meals_pic) {
        this.meals_pic = meals_pic;
    }

    public String getMeals_type() {
        return meals_type;
    }

    public void setMeals_type(String meals_type) {
        this.meals_type = meals_type;
    }

    public String getMeals_food() {
        return meals_food;
    }

    public void setMeals_food(String meals_food) {
        this.meals_food = meals_food;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(String efficiency) {
        this.efficiency = efficiency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    private String title;
    private String meals_pic;
    private String meals_type;
    private String meals_food;
    private String description;
    private String efficiency;
    private String method;
    private String created_at;

    @Override
    public String toString() {
        return "Meals [id=" + id + ", title=" + title + ", meals_pic=" + meals_pic + ", meals_type=" + meals_type + ", meals_food=" + meals_food + ", description=" + description + ", efficiency="
                + efficiency + ", method=" + method + ", created_at=" + created_at + "]";
    }

}
