package com.alha_app.toolbox.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CurrencyEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CurrencyDao currencyDao();
}
