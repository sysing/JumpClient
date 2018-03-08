package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 27-Feb-18.
 */

public class Item {
    public static final Integer DELETED = 0, VALID = 1;
    private String image, name, desc, restaurantKey;
    private Float price;
    private Integer status;
    private Long created, updated;


    public Item(String image, String name, String desc, Float price, String restaurantKey, Integer status, Long created, Long updated) {

        this.image = image;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.restaurantKey = restaurantKey;

        this.status = status;
        this.created = created;
        this.updated = updated;
    }

    public Item() {
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

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
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

    public String getRestaurantKey() {
        return restaurantKey;
    }

    public void setRestaurantKey(String restaurantKey) {
        this.restaurantKey = restaurantKey;
    }


}
