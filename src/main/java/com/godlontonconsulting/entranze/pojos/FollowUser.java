package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Energy on 2017/07/19.
 */

public class FollowUser {
    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    @SerializedName("msisdn")
    @Expose
    private long msisdn;
}
