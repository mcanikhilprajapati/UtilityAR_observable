
package com.utility.app.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Useractivity {

    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("value")
    @Expose
    private String value = "";

    public Useractivity(String page, String action, String value) {
        this.page = page;
        this.action = action;
        this.value = value;
    }

    public Useractivity(String page, String action) {
        this.page = page;
        this.action = action;
    }


    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
