package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 08-Mar-18.
 */

public class User {
    public final static Integer DELETED = 0, USER = 1, VENDOR = 2 , ADMIN = 3;
    private Long created,updated,viewOrdersTime;
    private Integer status;
    private String name;

    public User() {
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
