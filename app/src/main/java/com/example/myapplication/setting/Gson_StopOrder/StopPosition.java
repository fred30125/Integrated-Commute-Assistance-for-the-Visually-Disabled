
package com.example.myapplication.setting.Gson_StopOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopPosition {

    @SerializedName("PositionLat")
    @Expose
    private Double positionLat;
    @SerializedName("PositionLon")
    @Expose
    private Double positionLon;

    public Double getPositionLat() {
        return positionLat;
    }

    public void setPositionLat(Double positionLat) {
        this.positionLat = positionLat;
    }

    public Double getPositionLon() {
        return positionLon;
    }

    public void setPositionLon(Double positionLon) {
        this.positionLon = positionLon;
    }

}
