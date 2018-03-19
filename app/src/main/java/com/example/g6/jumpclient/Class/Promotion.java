package com.example.g6.jumpclient.Class;

import java.util.ArrayList;

/**
 * Created by g6 on 19-Mar-18.
 */

public class Promotion {
    private String itemKey;
    private Long created;
    private ArrayList<String > subscribers;

    public Promotion() {
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public ArrayList<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(ArrayList<String> subscribers) {
        this.subscribers = subscribers;
    }
}
