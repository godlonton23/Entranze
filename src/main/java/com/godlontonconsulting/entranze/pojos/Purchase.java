package com.godlontonconsulting.entranze.pojos;

/**
 * Created by Energy on 2017/06/07.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Purchase {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("purchaseDate")
    @Expose
    private String purchaseDate;
    @SerializedName("entranzeId")
    @Expose
    private Integer entranzeId;
    @SerializedName("priceCents")
    @Expose
    private Integer priceCents;
    @SerializedName("discountCents")
    @Expose
    private Integer discountCents;
    @SerializedName("msisdn")
    @Expose
    private long msisdn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getEntranzeId() {
        return entranzeId;
    }

    public void setEntranzeId(Integer entranzeId) {
        this.entranzeId = entranzeId;
    }

    public Integer getPriceCents() {
        return priceCents;
    }

    public void setPriceCents(Integer priceCents) {
        this.priceCents = priceCents;
    }

    public Integer getDiscountCents() {
        return discountCents;
    }

    public void setDiscountCents(Integer discountCents) {
        this.discountCents = discountCents;
    }

    public long getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(long msisdn) {
        this.msisdn = msisdn;
    }

}