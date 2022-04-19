package com.wudaokou.easylearn.retrofit;

public class LoginReturn {
    String token;

    public LoginReturn(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
