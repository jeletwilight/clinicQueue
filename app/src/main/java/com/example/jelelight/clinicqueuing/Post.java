package com.example.jelelight.clinicqueuing;

public class Post {

   // private String key;
    private String author;
    private String text;
    private String time;

    public Post(){

    }

    public Post(String author,String text,String time){
        //this.key = key;
        this.author = author;
        this.text = text;
        this.time = time;
    }

    /*public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }*/

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }






}
