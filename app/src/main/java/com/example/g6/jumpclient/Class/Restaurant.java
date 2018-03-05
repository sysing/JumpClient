package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 05-Mar-18.
 */

public class Restaurant {
    private String image,name,desc;
    private Integer delete;
    private Long created,updated;

    public Restaurant(){

    }

    public Restaurant(String image, String name, String desc, Integer delete, Long created, Long updated) {
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.delete = delete;
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

    public Integer getDelete() {
        return delete;
    }

    public void setDelete(Integer delete) {
        this.delete = delete;
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

