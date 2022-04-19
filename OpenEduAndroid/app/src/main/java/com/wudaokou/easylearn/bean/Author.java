package com.wudaokou.easylearn.bean;

import com.stfalcon.chatkit.commons.models.IUser;

import retrofit2.http.PUT;

public class Author implements IUser {
    String id;
    String name;
    String avatar;

    public Author (final String id, final String name, final String avatar) {
        this.avatar = avatar;
        this.name = name;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}
