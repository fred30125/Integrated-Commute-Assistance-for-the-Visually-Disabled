
package com.example.myapplication.setting.Gson_StopOrder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopName {

    @SerializedName("Zh_tw")
    @Expose
    private String zhTw;
    @SerializedName("En")
    @Expose
    private String en;

    public String getZhTw() {
        return zhTw;
    }

    public void setZhTw(String zhTw) {
        this.zhTw = zhTw;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

}
