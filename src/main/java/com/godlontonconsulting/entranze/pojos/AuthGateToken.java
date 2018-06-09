package com.godlontonconsulting.entranze.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Energy on 2017/06/08.
 */

public class AuthGateToken {

    @SerializedName("token")
    @Expose
    public String token;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

}
