package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Energy on 2017/04/28.
 */

public class Event implements Serializable {

    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("exception")
    @Expose
    private Object exception;
    @SerializedName("data")
    @Expose
    private List<Entranze> data = null;

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

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }


    public List<Entranze> getData() {
        return data;
    }

    public void setData(List<Entranze> data) {
        this.data = data;
    }
}
