package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddEventsResponseDto {


    @SerializedName("availableSeats")
    @Expose
    private Integer availableSeats;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("discountAlgorithmType")
    @Expose
    private String discountAlgorithmType;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("estimatedDiscountInCents")
    @Expose
    private Integer estimatedDiscountInCents;
    @SerializedName("gateKeepers")
    @Expose
    private GateKeepers gateKeepers;
    @SerializedName("googleMapLocation")
    @Expose
    private String googleMapLocation;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("owner")
    @Expose
    private Integer owner;
    @SerializedName("priceInCents")
    @Expose
    private Integer priceInCents;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("title")
    @Expose
    private String title;

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

    public String getDiscountAlgorithmType() {
        return discountAlgorithmType;
    }

    public void setDiscountAlgorithmType(String discountAlgorithmType) {
        this.discountAlgorithmType = discountAlgorithmType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getEstimatedDiscountInCents() {
        return estimatedDiscountInCents;
    }

    public void setEstimatedDiscountInCents(Integer estimatedDiscountInCents) {
        this.estimatedDiscountInCents = estimatedDiscountInCents;
    }

    public GateKeepers getGateKeepers() {
        return gateKeepers;
    }

    public void setGateKeepers(GateKeepers gateKeepers) {
        this.gateKeepers = gateKeepers;
    }

    public String getGoogleMapLocation() {
        return googleMapLocation;
    }

    public void setGoogleMapLocation(String googleMapLocation) {
        this.googleMapLocation = googleMapLocation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public Integer getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(Integer priceInCents) {
        this.priceInCents = priceInCents;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}