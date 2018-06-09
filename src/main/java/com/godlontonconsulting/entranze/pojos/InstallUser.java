package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Energy on 2017/05/07.
 */

public class InstallUser {


    @SerializedName("countryCodePrefix")
    @Expose
    private String countryCodePrefix;
    @SerializedName("msisdn")
    @Expose
    private long msisdn;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;

    public InstallUser(String countryCodePrefix, long msisdn, String firstName, String lastName ) {
        this.countryCodePrefix = countryCodePrefix;
        this.msisdn = msisdn;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getCountryCodePrefix() {
        return countryCodePrefix;
    }

    public void setCountryCodePrefix(String countryCodePrefix) {
        this.countryCodePrefix = countryCodePrefix;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
