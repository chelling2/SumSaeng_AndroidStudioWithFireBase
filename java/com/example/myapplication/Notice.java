package com.example.myapplication;

public class Notice {
    private String title;
    private String content;

    public Notice() {
       // 기본 생성자
    }

    public Notice(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
