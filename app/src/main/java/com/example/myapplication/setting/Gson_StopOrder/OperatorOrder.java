
package com.example.myapplication.setting.Gson_StopOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OperatorOrder {

    @SerializedName("OperatorID")
    @Expose
    private String operatorID;
    @SerializedName("OperatorName")
    @Expose
    private OperatorName operatorName;
    @SerializedName("OperatorCode")
    @Expose
    private String operatorCode;
    @SerializedName("OperatorNo")
    @Expose
    private String operatorNo;

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public OperatorName getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(OperatorName operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

}
