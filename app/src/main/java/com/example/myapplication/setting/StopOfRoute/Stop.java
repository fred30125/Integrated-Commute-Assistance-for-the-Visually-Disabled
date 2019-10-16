
package com.example.myapplication.setting.StopOfRoute;

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
    @SerializedName("StopBoarding")
    @Expose
    private Integer stopBoarding;
    @SerializedName("StopSequence")
    @Expose
    private Integer stopSequence;
    @SerializedName("StopPosition")
    @Expose
    private StopPosition stopPosition;
    @SerializedName("StationID")
    @Expose
    private String stationID;
    @SerializedName("LocationCityCode")
    @Expose
    private String locationCityCode;

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

    public Integer getStopBoarding() {
        return stopBoarding;
    }

    public void setStopBoarding(Integer stopBoarding) {
        this.stopBoarding = stopBoarding;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public StopPosition getStopPosition() {
        return stopPosition;
    }

    public void setStopPosition(StopPosition stopPosition) {
        this.stopPosition = stopPosition;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getLocationCityCode() {
        return locationCityCode;
    }

    public void setLocationCityCode(String locationCityCode) {
        this.locationCityCode = locationCityCode;
    }

}
