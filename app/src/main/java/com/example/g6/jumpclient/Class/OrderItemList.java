package com.example.g6.jumpclient.Class;

import java.util.ArrayList;

/**
 * Created by g6 on 09-Mar-18.
 */
public class OrderItemList{
    private ArrayList<OrderItem> iList;

    public OrderItemList() {
    }

    public OrderItemList(ArrayList<OrderItem> iList) {
        this.iList = iList;
    }

    public ArrayList<OrderItem> getiList() {
        return iList;
    }

    public void setiList(ArrayList<OrderItem> manyItems) {
        this.iList = manyItems;
    }
}
