
package com.example.myapplication.setting.Station;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stop {

    @SerializedName("StopUID")
    @Expose
    private String stopUID;
    @SerializedName("StopID")
    @Expose
    private String stopID;
    @SerializedName("StopName")
    @Expose
    private StopName stopName;
    @SerializedName("RouteUID")
    @Expose
    private String routeUID;
    @SerializedName("RouteID")
    @Expose
    private String routeID;
    @SerializedName("RouteName")
    @Expose
    private RouteName routeName;

    public String getStopUID() {
        return stopUID;
    }

    public void setStopUID(String stopUID) {
        this.stopUID = stopUID;
    }

    public String getStopID() {
        return stopID;
    }

    public void setStopID(String stopID) {
        this.stopID = stopID;
    }

    public StopName getStopName() {
        return stopName;
    }

    public void setStopName(StopName stopName) {
        this.stopName = stopName;
    }

    public String getRouteUID() {
        return routeUID;
    }

    public void setRouteUID(String routeUID) {
        this.routeUID = routeUID;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public RouteName getRouteName() {
        return routeName;
    }

    public void setRouteName(RouteName routeName) {
        this.routeName = routeName;
    }

}
