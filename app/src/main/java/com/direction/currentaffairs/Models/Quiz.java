package com.direction.currentaffairs.Models;

import java.util.ArrayList;

public class Quiz {
    private String name;
    private String date;
    private ArrayList<Question> questions;

    public Quiz(){

    }

    public Quiz(String name, String date, ArrayList<Question> questions) {
        this.name = name;
        this.date = date;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }
}
