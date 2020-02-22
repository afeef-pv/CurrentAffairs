package com.direction.currentaffairs.Models;

import java.util.ArrayList;

public class Question {
    private String question;
    private ArrayList<String> options;
    private String answer;
    private String extra;

    public Question(){}

    public Question(String question, ArrayList<String> options, String answer, String extra) {
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.extra = extra;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getOpt_a() {
        try{
            return options.get(0);
        }catch (Exception e){
            return "";
        }
    }

    public String getOpt_b() {
        try{
            return options.get(1);
        }catch (Exception e){
            return "";
        }
    }

    public String getOpt_c() {
        try{
            return options.get(2);
        }catch (Exception e){
            return "";
        }
    }

    public String getOpt_d() {
        try{
            return options.get(3);
        }catch (Exception e){
            return "";
        }
    }

    public String getOpt_e() {
        try{
            return options.get(4);
        }catch (Exception e){
            return "";
        }
    }

    public ArrayList<String> getOptions(){
        return this.options;
    }

    public String getAnswer() {
        try{
            return answer;
        }catch (Exception e){
            return "";
        }
    }

    public String getExtra() {
        return extra;
    }
}
