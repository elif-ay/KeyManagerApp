package com.gtu.keymanager;

public class FirstModel {
    private int Id;
    private String Name;

    // constructors
    public FirstModel() {
    }

    public FirstModel(int id, String name) {
        Id = id;
        Name = name;
    }

    // toString()
    @Override
    public String toString() {
        return "FirstModel{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                '}';
    }

    // getters-setters
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
