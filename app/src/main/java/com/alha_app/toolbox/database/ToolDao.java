package com.alha_app.toolbox.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ToolDao {
    @Query("SELECT * FROM TOOL_DATA")
    List<ToolEntity> getAll();

    @Query("DELETE FROM TOOL_DATA")
    void deleteAll();

    @Insert
    void insert(ToolEntity toolEntity);
}
