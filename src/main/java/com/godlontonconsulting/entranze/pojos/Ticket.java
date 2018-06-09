package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Energy on 2017/04/28.
 */

public class Ticket implements Serializable {

    public Ticket(){
    }

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("googleMapLocation")
    @Expose
    private String googleMapLocation;
    @SerializedName("priceInCents")
    @Expose
    private Integer priceInCents;
    @SerializedName("estimatedDiscountInCents")
    @Expose
    private Integer estimatedDiscountInCents;
    @SerializedName("discountAlgorithmType")
    @Expose
    private Object discountAlgorithmType;
    @SerializedName("owner")
    @Expose
    private float owner;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("gateKeepers")
    @Expose
    private Object gateKeepers;
    @SerializedName("base64image")
    @Expose
    private Object base64image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGoogleMapLocation() {
        return googleMapLocation;
    }

    public void setGoogleMapLocation(String googleMapLocation) {
        this.googleMapLocation = googleMapLocation;
    }

    public Integer getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(Integer priceInCents) {
        this.priceInCents = priceInCents;
    }

    public Integer getEstimatedDiscountInCents() {
        return estimatedDiscountInCents;
    }

    public void setEstimatedDiscountInCents(Integer estimatedDiscountInCents) {
        this.estimatedDiscountInCents = estimatedDiscountInCents;
    }

    public Object getDiscountAlgorithmType() {
        return discountAlgorithmType;
    }

    public void setDiscountAlgorithmType(Object discountAlgorithmType) {
        this.discountAlgorithmType = discountAlgorithmType;
    }

    public float getOwner() {
        return owner;
    }

    public void setOwner(float owner) {
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getGateKeepers() {
        return gateKeepers;
    }

    public void setGateKeepers(Object gateKeepers) {
        this.gateKeepers = gateKeepers;
    }

    public Object getBase64image() {
        return base64image;
    }

    public void setBase64image(Object base64image) {
        this.base64image = base64image;
    }

}
