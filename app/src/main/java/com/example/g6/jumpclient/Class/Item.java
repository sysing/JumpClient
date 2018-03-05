package com.example.g6.jumpclient.Class;

/**
 * Created by g6 on 27-Feb-18.
 */

public class Item {
    private String image, name, desc, price;
    private Integer delete;
    private Long created, updated;

    public Item(String image, String name, String desc, String price, Integer delete, Long created, Long updated) {

        this.image = image;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.delete = delete;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
