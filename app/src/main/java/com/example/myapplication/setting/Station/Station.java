
package com.example.myapplication.setting.Station;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Station {

    @SerializedName("Stops")
    @Expose
    private List<Stop> stops = null;
    @SerializedName("UpdateTime")
    @Expose
    private String updateTime;
    @SerializedName("VersionID")
    @Expose
    private Integer versionID;

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
