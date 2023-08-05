package com.alha_app.toolbox.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CurrencyDao {
    @Query("SELECT * FROM CURRENCY_DATA")
    List<CurrencyEntity> getAll();

    @Query("DELETE FROM CURRENCY_DATA")
    void deleteAll();

    @Insert
    void insert(CurrencyEntity currencyEntity);
}
