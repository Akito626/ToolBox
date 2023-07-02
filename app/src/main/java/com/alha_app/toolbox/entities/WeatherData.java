package com.alha_app.toolbox.entities;

import java.io.Serializable;

public class WeatherData {
    String month;
    String day;
    String time;
    String weather;
    double temp_min;
    double temp_max;
    double pop;
    int humidity;

    // Getter
    public String getMonth() {
        return month;
    }
    public String getDay() {
        return day;
    }
    public String getTime() {
        return time;
    }
    public String getWeather() {
        return weather;
    }
    public double getTemp_min() {
        return temp_min;
    }
    public double getTemp_max() {
        return temp_max;
    }
    public double getPop() {
        return pop;
    }
    public int getHumidity() {
        return humidity;
    }

    // Setter
    public void setDt_txt(String dt_txt) {
        int hyphen1 = dt_txt.indexOf("-");
        int hyphen2 = dt_txt.lastIndexOf("-");
        int space = dt_txt.indexOf(" ");
        int colon = dt_txt.indexOf(":");
        month = dt_txt.substring(hyphen1 + 1, hyphen2);
        day = dt_txt.substring(hyphen2 + 1, space);
        time = dt_txt.substring(space + 1, colon);
    }
    public void setWeather(String weather) {
        this.weather = weather;
    }
    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }
    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }
    public void setPop(double pop) {
        this.pop = pop;
    }
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString(){
        return month + " " + day + " " + time + " " + weather + " " + temp_min + " " + temp_max + " " + pop + " " + humidity;
    }
}
