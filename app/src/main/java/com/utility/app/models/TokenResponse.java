
package com.utility.app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("expiresOn")
    @Expose
    private String expiresOn;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TokenResponse() {
    }

    /**
     * 
     * @param expiresOn
     * @param token
     */
    public TokenResponse(String token, String expiresOn) {
        super();
        this.token = token;
        this.expiresOn = expiresOn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TokenResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("token");
        sb.append('=');
        sb.append(((this.token == null)?"<null>":this.token));
        sb.append(',');
        sb.append("expiresOn");
        sb.append('=');
        sb.append(((this.expiresOn == null)?"<null>":this.expiresOn));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
