package com.direction.currentaffairs.Models;

import java.util.ArrayList;

public class CurrentAffairs {
    private String id;
    private String date;
    ArrayList<News> newses;

    public CurrentAffairs(){}

    public CurrentAffairs(String id, String date, ArrayList<News> newses) {
        this.id = id;
        this.date = date;
        this.newses = newses;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<News> getNewses() {
        return newses;
    }
}
