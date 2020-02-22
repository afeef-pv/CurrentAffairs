package com.direction.currentaffairs.Widgets;

import com.direction.currentaffairs.Models.Question;

public class QuestionHolder {

    String title;
    Question content;
    public static final int UNSEEN = 0,SKIPPED = 1,CORRECT = 2,INCORRECT = 3;
    public int userOption = -1, answerId = -1, flag = 0;
    int status;

    public int getStatus() {
        return status;
    }

    public void setStatus ( int status){
        this.status = status;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title){
        this.title = title;
    }

    public Question getContent () {
        return content;
    }

    public void setContent (Question content){
        this.content = content;
    }

}
