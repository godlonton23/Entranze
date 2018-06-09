package com.godlontonconsulting.entranze.pojos;

/**
 * Created by Energy on 2017/06/09.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Energy on 2017/04/28.
 */

public class EntranzePurchases implements Serializable {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Purchases> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Purchases> getData() {
        return data;
    }

    public void setData(List<Purchases> data) {
        this.data = data;
    }
}
