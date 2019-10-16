
package com.example.myapplication.setting.StopOfRoute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StopOfRoute {

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
