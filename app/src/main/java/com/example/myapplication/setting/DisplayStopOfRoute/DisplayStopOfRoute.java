
package com.example.myapplication.setting.DisplayStopOfRoute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DisplayStopOfRoute {

    @SerializedName("RouteUID")
    @Expose
    private String routeUID;
    @SerializedName("RouteID")
    @Expose
    private String routeID;
    @SerializedName("RouteName")
    @Expose
    private RouteName routeName;
    @SerializedName("Direction")
    @Expose
    private Integer direction;
    @SerializedName("Stops")
    @Expose
    private List<Stop> stops = null;
    @SerializedName("UpdateTime")
    @Expose
    private String updateTime;
    @SerializedName("VersionID")
    @Expose
    private Integer versionID;

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

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersionID() {
        return versionID;
    }

    public void setVersionID(Integer versionID) {
        this.versionID = versionID;
    }

}
