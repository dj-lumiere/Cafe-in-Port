package com.ssafy.cafe.model.dto;

public class Level {
    String title;
    int unit;
    int max;
    String img;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Level(String title, int unit, int max, String img) {
        this.title = title;
        this.unit = unit;
        this.max = max;
        this.img = img;
    }
}