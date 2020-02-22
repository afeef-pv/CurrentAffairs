package com.direction.currentaffairs.RealmModels;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmNews extends RealmObject {
    private String heading, id;
    private RealmList<String> subpoints;
    private String url;
    private String date;

    public RealmNews(){}


    public RealmNews(String id, String news, String url, String date,ArrayList<String> subs) {
        this.id = id;
        heading = news;
        this.url = url;
        this.date = date;
        subpoints = new RealmList<>();
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

    public RealmList<String> getSubpoints() {
        return this.subpoints;
    }

    public String getUrl() { return this.url; }

    public String getId(){
        return this.id;
    }
}
