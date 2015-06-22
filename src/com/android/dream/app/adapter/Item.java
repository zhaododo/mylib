package com.android.dream.app.adapter;

public class Item {
    
    private int resId;
    private String name;
    private String detail;
    
    public Item(int resId, String name, String detail) {
        this.resId  = resId;
        this.name   = name;
        this.detail = detail;
    }
    
    public void setImageId(int resId) {
        this.resId  = resId;
    }
    
    public int getImageId() {
        return resId;
    }
    
    public void setName(String name) {
        this.name   = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    public String getDetail() {
        return detail;
    }
    
    public String toString() {
        return "Item[" + resId + ", " + name + ", " + detail + "]";
    }

}
