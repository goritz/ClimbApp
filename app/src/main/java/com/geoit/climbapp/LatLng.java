package com.geoit.climbapp;

import java.util.Objects;

public class LatLng {
    private double latitude;
    private double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public boolean equals(LatLng o) {
        if (this == o)
            return true;
        return (this.latitude == o.latitude && this.longitude == o.longitude);
//        return Double.compare(latLng.latitude, latitude) == 0 && Double.compare(latLng.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
