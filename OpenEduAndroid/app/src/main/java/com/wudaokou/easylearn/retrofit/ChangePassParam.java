package com.wudaokou.easylearn.retrofit;

public class ChangePassParam {
    String username;
    String oldPassword;
    String newPassword;

    public ChangePassParam(String username, String oldPassword, String newPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
