
package com.example.myapplication.setting.Gson_StopOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusRouteOrder {

    @SerializedName("RouteUID")
    @Expose
    private String routeUID;
    @SerializedName("RouteID")
    @Expose
    private String routeID;
    @SerializedName("RouteName")
    @Expose
    private RouteName routeName;
    @SerializedName("Operators")
    @Expose
    private List<OperatorOrder> operatorOrders = null;
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
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("CityCode")
    @Expose
    private String cityCode;
    @SerializedName("Stops")
    @Expose
    private List<StopOrder> stopOrders = null;
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

    public List<OperatorOrder> getOperatorOrders() {
        return operatorOrders;
    }

    public void setOperatorOrders(List<OperatorOrder> operatorOrders) {
        this.operatorOrders = operatorOrders;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public List<StopOrder> getStopOrders() {
        return stopOrders;
    }

    public void setStopOrders(List<StopOrder> stopOrders) {
        this.stopOrders = stopOrders;
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
