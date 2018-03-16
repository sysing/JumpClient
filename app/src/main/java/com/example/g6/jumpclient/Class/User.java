package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 08-Mar-18.
 */

public class User {
    public final static Integer DELETED = 0, USER = 1, VENDOR = 2 , ADMIN = 3;
    private Long created,updated,viewOrdersTime;
    private Integer status;
    private String name,image;
    private Double bmr,age,height,weight,targetWeekWeight,mealIntake;
    private Boolean gender;

    public User() {
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Double getBmr() {
        return bmr;
    }

    public void setBmr(Double bmr) {
        this.bmr = bmr;
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getTargetWeekWeight() {
        return targetWeekWeight;
    }

    public void setTargetWeekWeight(Double targetWeekWeight) {
        this.targetWeekWeight = targetWeekWeight;
    }

    public Double getMealIntake() {
        return mealIntake;
    }

    public void setMealIntake(Double mealIntake) {
        this.mealIntake = mealIntake;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getViewOrdersTime() {
        return viewOrdersTime;
    }

    public void setViewOrdersTime(Long viewOrderTime) {
        this.viewOrdersTime = viewOrderTime;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
