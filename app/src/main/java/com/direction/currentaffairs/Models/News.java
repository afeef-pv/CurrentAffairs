package com.direction.currentaffairs.Models;

import java.util.ArrayList;

public class News {
    private String heading;
    private ArrayList<String> subpoints;
    private String url;
    private String date;

    public News(){}


    public News(String news, String... subs) {
        heading = news;
        subpoints = new ArrayList<>();
        for(String s: subs)
            subpoints.add(s);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getHeading() {
        return this.heading;
    }

    public ArrayList<String> getSubpoints() {
        return this.subpoints;
    }

    public String getUrl() { return this.url; }
}
