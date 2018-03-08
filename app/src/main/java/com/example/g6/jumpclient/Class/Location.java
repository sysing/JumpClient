package com.example.g6.jumpclient.Class;

import java.util.List;

/**
 * Created by g6 on 05-Mar-18.
 */

public class Location {
    public static final Integer DELETED = 0, VALID = 1;
    private String image,name,desc;
    private Integer status;
    private Long created,updated;

    public Location(){

    }

    public Location(String image, String name, String desc, Integer delete, Long created, Long updated, List<String> restaurantKeys) {
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.status = delete;
        this.created = created;
        this.updated = updated;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

}

