package com.wudaokou.easylearn.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PropertyDAO {
    @Insert
    void insertProperty(Property property);

    @Query("DELETE FROM property")
    public void deleteAllProperty();

    @Delete
    public void deleteProperty(Property property);

    @Update
    public void updateProperty(Property property);

    @Query("SELECT * FROM property")
    public List<Property> loadAllProperty();

    @Query("SELECT * FROM property WHERE course = :course AND label = :label")
    public List<Property> loadPropertyByCourseAndLabel(String course, String label);

    @Query("SELECT * FROM property WHERE parentUri = :uri")
    public List<Property> loadPropertyByParentUri(String uri);
}
