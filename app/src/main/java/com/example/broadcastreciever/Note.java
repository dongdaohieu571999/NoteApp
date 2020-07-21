package com.example.broadcastreciever;

import java.io.Serializable;

public class Note implements Serializable {
    private String id;
    private String title;
    private String date;
    private String content;
    private int color;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Note(String title, String date, String content, int color, boolean checked) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.color = color;
        this.checked = checked;
    }

    public Note(String id,String title, String date, String content, int color, boolean checked) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
        this.color = color;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
