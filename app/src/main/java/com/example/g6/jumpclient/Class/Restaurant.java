package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 05-Mar-18.
 */

public class Restaurant {
    public static final Integer DELETED = 0, VALID = 1;
    private String image,name,desc,locationKey,vendorKey;
    private Integer status;
    private Long created,updated;

    public Restaurant(){

    }

    public Restaurant(String image, String name, String desc, String locationKey, String vendorKey, Integer status, Long created, Long updated) {
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.locationKey = locationKey;
        this.vendorKey = vendorKey;
        this.status = status;
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

    public String getLocationKey() {
        return locationKey;
    }

    public void setLocationKey(String locationKey) {
        this.locationKey = locationKey;
    }

    public String getVendorKey() {
        return vendorKey;
    }

    public void setVendorKey(String vendorKey) {
        this.vendorKey = vendorKey;
    }
}

