package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Energy on 2017/07/19.
 */

public class DeviceUser {

    @SerializedName("fcmToken")
    @Expose
    private String fcmToken;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("os")
    @Expose
    private String os;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
