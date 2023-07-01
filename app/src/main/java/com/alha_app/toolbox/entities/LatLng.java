package com.alha_app.toolbox.entities;

public class LatLng {
    double latitude;
    double longitude;

    public LatLng(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    // Setter
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
