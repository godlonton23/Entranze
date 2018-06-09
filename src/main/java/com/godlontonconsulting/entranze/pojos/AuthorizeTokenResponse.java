package com.godlontonconsulting.entranze.pojos;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Energy on 2017/04/28.
 */

public class AuthorizeTokenResponse implements Serializable {


    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("exception")
    @Expose
    private Object exception;
    @SerializedName("data")
    @Expose
    private GateTokenRepsonse data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

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

    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }

    public GateTokenRepsonse getData() {
        return data;
    }

    public void setData(GateTokenRepsonse data) {
        this.data = data;
    }
}