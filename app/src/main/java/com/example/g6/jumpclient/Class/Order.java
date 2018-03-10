package com.example.g6.jumpclient.Class;

import java.util.ArrayList;

/**
 * Created by g6 on 08-Mar-18.
 */

public class Order {
    public static final Integer DELETED = 0 , DRAFT = 1,  PENDING = 2, ACCEPTED = 3, READY = 4 , COMPLETED = 5, REJECTED = 6;
    private ArrayList<OrderItem> iList;
    private String userKey,restaurantKey;
    private Long created,updated;
    private Integer status;

    public Order() {
    }

    public  ArrayList<OrderItem> getiList() {
        return iList;
    }

    public void setiList(ArrayList<OrderItem> iList) {
        this.iList = iList;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getRestaurantKey() {
        return restaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        this.restaurantKey = restaurantKey;
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
    public static String getStatusString(Integer status){
        if (status == DELETED)
            return "DELETED";
        if (status == DRAFT)
            return "DRAFT";
        if (status == PENDING)
            return "PENDING";
        if (status == ACCEPTED)
            return "ACCEPTED";
        if (status == READY)
            return "READY";
        if (status == COMPLETED)
            return "COMPLETED";
        if (status == REJECTED)
            return "REJECTED";
        return "";
    }
}
