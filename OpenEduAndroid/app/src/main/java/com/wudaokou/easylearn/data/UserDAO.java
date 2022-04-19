package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface UserDAO {

    // 用户名（主键）不能重复
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertUser(User user);

    @Update()
    public void updateUser(User user);

    @RawQuery()
    public User updateBoolean(SupportSQLiteQuery query);

    @Delete()
    public void deleteUser(User user);

    @Query("SELECT * FROM user WHERE name = :name")
    public User loadUserByName(String name);

    @RawQuery()
    public boolean loadBoolean(SupportSQLiteQuery query);
}
