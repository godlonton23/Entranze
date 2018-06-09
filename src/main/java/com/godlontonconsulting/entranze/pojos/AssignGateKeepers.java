package com.godlontonconsulting.entranze.pojos;

/**
 * Created by Energy on 2017/06/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssignGateKeepers {

    public AssignGateKeepers(long mobile){

        this.msisdn=mobile;
    }

    @SerializedName("msisdn")
    @Expose
    private long msisdn;

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

}
