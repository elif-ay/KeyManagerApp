package com.gtu.keymanager;

import android.graphics.Bitmap;

import java.util.Arrays;

public class KeyModel {
    private int id;
    private String keyName;
    private String image;

    // constructors

    public KeyModel(int id, String keyName, String image) {
        this.id = id;
        this.keyName = keyName;
        this.image = image;
    }

    public KeyModel() {
    }

    // toString
    @Override
    public String  toString() {
        return "KeyModel{" +
                "id=" + id +
                ", keyName='" + keyName + '\'' +
                ", image=" + image +
                '}';
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
