package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class User implements Serializable {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("createdTime")
    @Expose
    private String createdTime;
    @SerializedName("modifiedTime")
    @Expose
    private String modifiedTime;
    @SerializedName("msisdn")
    @Expose
    private long msisdn;
    @SerializedName("firstName")
    @Expose
    private Object firstName;
    @SerializedName("lastName")
    @Expose
    private Object lastName;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("loyaltyBalanceCents")
    @Expose
    private Integer loyaltyBalanceCents;
    @SerializedName("currentLocation")
    @Expose
    private Object currentLocation;

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    @SerializedName("followerCount")
    @Expose
    private Integer followerCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public Object getFirstName() {
        return firstName;
    }

    public void setFirstName(Object firstName) {
        this.firstName = firstName;
    }

    public Object getLastName() {
        return lastName;
    }

    public void setLastName(Object lastName) {
        this.lastName = lastName;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public Integer getLoyaltyBalanceCents() {
        return loyaltyBalanceCents;
    }

    public void setLoyaltyBalanceCents(Integer loyaltyBalanceCents) {
        this.loyaltyBalanceCents = loyaltyBalanceCents;
    }

    public Object getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Object currentLocation) {
        this.currentLocation = currentLocation;
    }

}
