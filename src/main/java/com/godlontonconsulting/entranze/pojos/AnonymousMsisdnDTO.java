package com.godlontonconsulting.entranze.pojos;

/**
 * Created by Energy on 2017/06/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnonymousMsisdnDTO {

    @SerializedName("data")
    @Expose
    private List<AssignGateKeepers> data = null;

    public List<AssignGateKeepers> getData() {
        return data;
    }

    public void setData(List<AssignGateKeepers> data) {
        this.data = data;
    }

}
