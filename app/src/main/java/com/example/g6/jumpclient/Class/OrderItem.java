package com.example.g6.jumpclient.Class;

public class OrderItem {
    private String itemKey;
    private Integer quantity;
    private Float price;


    public OrderItem() {
    }

    public OrderItem(String itemKey, Integer quantity, Float price) {
        this.quantity = quantity;
        this.price = price;
        this.itemKey = itemKey;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
}
