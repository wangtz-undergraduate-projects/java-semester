package com.wudaokou.easylearn.data.model;

public class logException extends Exception{
    private String code;

    public logException(String s) {
        this.code = s;
    }

    public  String getcode(){
        return code;
    }

    public String toString(){
        return code;
    }
}
