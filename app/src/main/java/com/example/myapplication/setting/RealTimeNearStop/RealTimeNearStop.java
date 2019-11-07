
package com.example.myapplication.setting.RealTimeNearStop;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RealTimeNearStop {

    @SerializedName("PlateNumb")
    @Expose
    private String plateNumb;
    @SerializedName("OperatorID")
    @Expose
    private String operatorID;
    @SerializedName("RouteUID")
    @Expose
    private String routeUID;
    @SerializedName("RouteID")
    @Expose
    private String routeID;
    @SerializedName("RouteName")
    @Expose
    private RouteName routeName;
    @SerializedName("SubRouteUID")
    @Expose
    private String subRouteUID;
    @SerializedName("SubRouteID")
    @Expose
    private String subRouteID;
    @SerializedName("SubRouteName")
    @Expose
    private SubRouteName subRouteName;
    @SerializedName("Direction")
    @Expose
    private Integer direction;
    @SerializedName("StopUID")
    @Expose
    private String stopUID;
    @SerializedName("StopID")
    @Expose
    private String stopID;
    @SerializedName("StopName")
    @Expose
    private StopName stopName;
    @SerializedName("StopSequence")
    @Expose
    private Integer stopSequence;
    @SerializedName("MessageType")
    @Expose
    private Integer messageType;
    @SerializedName("DutyStatus")
    @Expose
    private Integer dutyStatus;
    @SerializedName("BusStatus")
    @Expose
    private Integer busStatus;
    @SerializedName("A2EventType")
    @Expose
    private Integer a2EventType;
    @SerializedName("GPSTime")
    @Expose
    private String gPSTime;
    @SerializedName("SrcUpdateTime")
    @Expose
    private String srcUpdateTime;
    @SerializedName("UpdateTime")
    @Expose
    private String updateTime;

    public String getPlateNumb() {
        return plateNumb;
    }

    public void setPlateNumb(String plateNumb) {
        this.plateNumb = plateNumb;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
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

    public String getSubRouteUID() {
        return subRouteUID;
    }

    public void setSubRouteUID(String subRouteUID) {
        this.subRouteUID = subRouteUID;
    }

    public String getSubRouteID() {
        return subRouteID;
    }

    public void setSubRouteID(String subRouteID) {
        this.subRouteID = subRouteID;
    }

    public SubRouteName getSubRouteName() {
        return subRouteName;
    }

    public void setSubRouteName(SubRouteName subRouteName) {
        this.subRouteName = subRouteName;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

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

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getDutyStatus() {
        return dutyStatus;
    }

    public void setDutyStatus(Integer dutyStatus) {
        this.dutyStatus = dutyStatus;
    }

    public Integer getBusStatus() {
        return busStatus;
    }

    public void setBusStatus(Integer busStatus) {
        this.busStatus = busStatus;
    }

    public Integer getA2EventType() {
        return a2EventType;
    }

    public void setA2EventType(Integer a2EventType) {
        this.a2EventType = a2EventType;
    }

    public String getGPSTime() {
        return gPSTime;
    }

    public void setGPSTime(String gPSTime) {
        this.gPSTime = gPSTime;
    }

    public String getSrcUpdateTime() {
        return srcUpdateTime;
    }

    public void setSrcUpdateTime(String srcUpdateTime) {
        this.srcUpdateTime = srcUpdateTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
