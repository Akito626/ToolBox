package com.alha_app.toolbox.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TOOL_DATA")
public class ToolEntity {
    @PrimaryKey
    private int id;
    private int count;
    private boolean isFavorite;

    public ToolEntity(int id, int count, boolean isFavorite){
        this.id = id;
        this.count = count;
        this.isFavorite = isFavorite;
    }

    // Getter
    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
