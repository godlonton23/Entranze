package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Energy on 2017/05/31.
 */



public class GateKeepers {

    @SerializedName("data")
    @Expose
    private List<ViewGateKeepers> data = null;

    public List<ViewGateKeepers> getData() {
        return data;
    }

    public void setData(List<ViewGateKeepers> data) {
        this.data = data;
    }

}