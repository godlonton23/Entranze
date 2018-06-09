package com.godlontonconsulting.entranze.pojos;

/**
 * Created by Energy on 2017/06/07.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Fav {

    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

}