package com.example.g6.jumpclient.Class;

import java.util.Map;

/**
 * Created by g6 on 08-Mar-18.
 */

public class Order {
    public static final Integer DELETED = 0 , PENDING = 1, ACKNOWLEDGED = 2, READY = 3 , COMPLETED = 4;
    private Float totalPrice;
    private Map<Item, Integer> ItemQuantity;
    private String userKey,restaurantKey;
    private Long created,updated;
    private Integer status;

    public Order() {
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Map<Item, Integer> getItemQuantity() {
        return ItemQuantity;
    }

    public void setItemQuantity(Map<Item, Integer> itemQuantity) {
        ItemQuantity = itemQuantity;
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
}
