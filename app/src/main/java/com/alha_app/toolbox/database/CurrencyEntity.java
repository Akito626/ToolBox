package com.alha_app.toolbox.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CURRENCY_DATA")
public class CurrencyEntity {
    @NonNull
    @PrimaryKey
    private String currencyCode;
    private String currencyName;
    private double currencyRate;

    public CurrencyEntity(String currencyCode, String currencyName,double currencyRate){
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.currencyRate = currencyRate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public double getCurrencyRate() {
        return currencyRate;
    }
}
