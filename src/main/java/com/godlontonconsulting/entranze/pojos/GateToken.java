package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Energy on 2017/06/08.
 */

public class GateToken {

    @SerializedName("createdTime")
    @Expose
    private String createdTime;
    @SerializedName("msisdn")
    @Expose
    private long msisdn;
    @SerializedName("token")
    @Expose
    private String token;

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
