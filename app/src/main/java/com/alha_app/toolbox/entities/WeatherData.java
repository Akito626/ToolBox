package com.alha_app.toolbox.entities;

public class WeatherData {
    String weather;
    double temp_min;
    double temp_max;
    int humidity;

    // Getter
    public String getWeather() {
        return weather;
    }
    public double getTemp_min() {
        return temp_min;
    }
    public double getTemp_max() {
        return temp_max;
    }
    public int getHumidity() {
        return humidity;
    }

    // Setter
    public void setWeather(String weather) {
        this.weather = weather;
    }
    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }
    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString(){
        return weather + ", " + temp_min + ", " + temp_max + ", " + humidity;
    }
}
