package com.ssafy.cafe.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private Integer userNo;
    private String id;
    private String username;
    private String pass;
    private Integer stamps;
    private String email;
    private Date registerTime;
    private String fcmToken;
    private List<Stamp> stampList = new ArrayList<>();

    public User(String id, String username, String pass, String email, Date registerTime, Integer stamps) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.email = email;
        this.registerTime = registerTime;
        this.stamps = stamps;
    }

    public User(String id, String username, String pass, String email) {
        this.id = id;
        this.username = username;
        this.pass = pass;
        this.email = email;
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getStamps() {
        return stamps;
    }

    public void setStamps(Integer stamps) {
        this.stamps = stamps;
    }

    public List<Stamp> getStampList() {
        return stampList;
    }

    public void setStampList(List<Stamp> stampList) {
        this.stampList = stampList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "userNo=" + userNo +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", pass='" + pass + '\'' +
                ", stamps=" + stamps +
                ", email='" + email + '\'' +
                ", registerTime=" + registerTime +
                ", fcmToken='" + fcmToken + '\'' +
                ", stampList=" + stampList +
                '}';
    }
}