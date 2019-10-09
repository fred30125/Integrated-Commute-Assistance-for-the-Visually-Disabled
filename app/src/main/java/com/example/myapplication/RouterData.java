package com.example.myapplication;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

public class RouterData {
    private int id;
    private Point point;
    private LatLng latLng;
    private String description;

    public int getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Point getPoint() {
        return point;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
