package com.example.g6.jumpclient.Class;

import java.util.ArrayList;

/**
 * Created by g6 on 08-Mar-18.
 */

public class Order {
    public static final Integer DELETED = 0 , DRAFT = 1,  PENDING = 2, REJECTED = 3, ACCEPTED = 4 , READY = 5, COMPLETED = 6 , RATED = 7;
    private ArrayList<OrderItem> iList;
    private String userKey,restaurantKey;
    private Long created,updated,submitted,readied;
    private Integer status,rating;

    public Order() {
    }

    public Long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Long submitted) {
        this.submitted = submitted;
    }

    public Long getReadied() {
        return readied;
    }

    public void setReadied(Long readied) {
        this.readied = readied;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
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
        if (status.equals(DELETED))
            return "DELETED";
        if (status.equals(DRAFT))
            return "DRAFT";
        if (status.equals(PENDING))
            return "PENDING";
        if (status.equals(ACCEPTED))
            return "ACCEPTED";
        if (status.equals(READY))
            return "READY";
        if (status.equals(COMPLETED))
            return "COMPLETED";
        if (status.equals(REJECTED))
            return "REJECTED";
        if (status.equals(RATED))
            return "RATED";
        return "";
    }
}
