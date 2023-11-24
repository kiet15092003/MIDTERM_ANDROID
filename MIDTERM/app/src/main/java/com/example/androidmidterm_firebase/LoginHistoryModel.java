package com.example.androidmidterm_firebase;

public class LoginHistoryModel {
    String userName;
    String loginStart;

    public LoginHistoryModel(){

    }
    public LoginHistoryModel(String userName, String loginStart) {
        this.userName = userName;
        this.loginStart = loginStart;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginStart() {
        return loginStart;
    }

    public void setLoginStart(String loginStart) {
        this.loginStart = loginStart;
    }
}
