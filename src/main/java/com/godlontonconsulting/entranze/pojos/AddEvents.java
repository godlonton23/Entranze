package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddEvents {

    @SerializedName("aPrivate")
    @Expose
    private Boolean aPrivate;
    @SerializedName("amGateKeeper")
    @Expose
    private Boolean amGateKeeper;
    @SerializedName("amOwner")
    @Expose
    private Boolean amOwner;
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
    @SerializedName("followersCount")
    @Expose
    private Integer followersCount;
    @SerializedName("gateKeepers")
    @Expose
    private GateKeepers gateKeepers;
    @SerializedName("gpsCordinates")
    @Expose
    private GPS gpsCordinates;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("owner")
    @Expose
    private long owner;
    @SerializedName("priceInCents")
    @Expose
    private Integer priceInCents;
    @SerializedName("registeredSeats")
    @Expose
    private Integer registeredSeats;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("venueName")
    @Expose
    private String venueName;

    public Boolean getAPrivate() {
        return aPrivate;
    }

    public void setAPrivate(Boolean aPrivate) {
        this.aPrivate = aPrivate;
    }

    public Boolean getAmGateKeeper() {
        return amGateKeeper;
    }

    public void setAmGateKeeper(Boolean amGateKeeper) {
        this.amGateKeeper = amGateKeeper;
    }

    public Boolean getAmOwner() {
        return amOwner;
    }

    public void setAmOwner(Boolean amOwner) {
        this.amOwner = amOwner;
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

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public GateKeepers getGateKeepers() {
        return gateKeepers;
    }

    public void setGateKeepers(GateKeepers gateKeepers) {
        this.gateKeepers = gateKeepers;
    }

    public GPS getGpsCordinates() {
        return gpsCordinates;
    }

    public void setGpsCordinates(GPS gpsCordinates) {
        this.gpsCordinates = gpsCordinates;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public Integer getPriceInCents() {
        return priceInCents;
    }

    public void setPriceInCents(Integer priceInCents) {
        this.priceInCents = priceInCents;
    }

    public Integer getRegisteredSeats() {
        return registeredSeats;
    }

    public void setRegisteredSeats(Integer registeredSeats) {
        this.registeredSeats = registeredSeats;
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

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

}