
package com.example.myapplication.setting.EstimateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EsimateTime {

    @SerializedName("Direction")
    @Expose
    private Integer direction;
    @SerializedName("EstimateTime")
    @Expose
    private Integer estimateTime;
    @SerializedName("StopStatus")
    @Expose
    private Integer stopStatus;
    @SerializedName("MessageType")
    @Expose
    private Integer messageType;
    @SerializedName("SrcUpdateTime")
    @Expose
    private String srcUpdateTime;
    @SerializedName("UpdateTime")
    @Expose
    private String updateTime;

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(Integer estimateTime) {
        this.estimateTime = estimateTime;
    }

    public Integer getStopStatus() {
        return stopStatus;
    }

    public void setStopStatus(Integer stopStatus) {
        this.stopStatus = stopStatus;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
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
