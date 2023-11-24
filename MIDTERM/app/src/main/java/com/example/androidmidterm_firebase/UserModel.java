package com.example.androidmidterm_firebase;

public class UserModel {
    Long age;
    String imgSrc;
    String name;
    String phoneNum;
    Long status;
    String userName;
    String password;

    public UserModel(){

    }
    public UserModel(String name, String phoneNum, String imgSrc, Long status, Long age, String userName, String password) {
        this.age = age;
        this.imgSrc = imgSrc;
        this.name = name;
        this.phoneNum = phoneNum;
        this.status = status;
        this.userName = userName;
        this.password = password;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
