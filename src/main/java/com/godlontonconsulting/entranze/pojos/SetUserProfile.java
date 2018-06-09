package com.godlontonconsulting.entranze.pojos;

/**
 * Created by Energy on 2017/04/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetUserProfile {

    @SerializedName("createdTime")
    @Expose
    private String createdTime;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("loyaltyBalanceCents")
    @Expose
    private Integer loyaltyBalanceCents;
    @SerializedName("msisdn")
    @Expose
    private Integer msisdn;

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getLoyaltyBalanceCents() {
        return loyaltyBalanceCents;
    }

    public void setLoyaltyBalanceCents(Integer loyaltyBalanceCents) {
        this.loyaltyBalanceCents = loyaltyBalanceCents;
    }

    public Integer getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(Integer msisdn) {
        this.msisdn = msisdn;
    }

}
