package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Energy on 2017/06/08.
 */

public class GateTokenRepsonse {

    @SerializedName("aPrivate")
    @Expose
    private Boolean aPrivate;
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
    @SerializedName("availableSeats")
    @Expose
    private Integer availableSeats;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("owner")
    @Expose
    private long owner;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("gateKeepers")
    @Expose
    private Object gateKeepers;
    @SerializedName("amOwner")
    @Expose
    private Boolean amOwner;
    @SerializedName("amGateKeeper")
    @Expose
    private Boolean amGateKeeper;
    @SerializedName("imageData")
    @Expose
    private Object imageData;

    public Boolean getAPrivate() {
        return aPrivate;
    }

    public void setAPrivate(Boolean aPrivate) {
        this.aPrivate = aPrivate;
    }

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

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
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

    public Boolean getAmOwner() {
        return amOwner;
    }

    public void setAmOwner(Boolean amOwner) {
        this.amOwner = amOwner;
    }

    public Boolean getAmGateKeeper() {
        return amGateKeeper;
    }

    public void setAmGateKeeper(Boolean amGateKeeper) {
        this.amGateKeeper = amGateKeeper;
    }

    public Object getImageData() {
        return imageData;
    }

    public void setImageData(Object imageData) {
        this.imageData = imageData;
    }

}
