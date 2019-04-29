package com.example.myapplication;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.net.URL;

public class busData {
    private LatLng latLng;
    private String name;
    private URL icon;
    public busData(LatLng latLng,String name,URL icon){
        this.latLng=latLng;
        this.name=name;
        this.icon=icon;
    }
    public LatLng getLatLng() {
        return latLng;
    }
    public String getName() {
        return name;
    }

    public URL getIcon() {
        return icon;
    }

    public void setIcon(URL icon) {
        this.icon = icon;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setName(String name) {
        this.name = name;
    }
}
