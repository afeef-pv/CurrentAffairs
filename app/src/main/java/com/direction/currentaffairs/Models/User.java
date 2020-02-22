package com.direction.currentaffairs.Models;

public class User {
    private String name;
    private String phone;

    public User(){}

    public User(String phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
