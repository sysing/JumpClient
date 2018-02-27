package com.example.g6.jumpclient;

/**
 * Created by g6 on 27-Feb-18.
 */

public class Item {
    private String image,name,desc,price;

    public Item(){

    }

    public Item(String image,String name, String desc, String price){
        this.image = image;
        this.name = name;
        this.desc = desc;
        this.price = price;
    }

    public String getImage(){
        return this.image;
    }
    public String getName(){
        return this.name;
    }
    public String getDesc(){
        return this.desc;
    }
    public String getPrice(){
        return this.price;
    }
    public void setImage(String image){
         this.image = image;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDsec(String desc){
         this.desc = desc;
    }
    public void setPrice(String price){
         this.price = price;
    }

}
