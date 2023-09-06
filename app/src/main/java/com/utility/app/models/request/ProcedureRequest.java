
package com.utility.app.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProcedureRequest {

    @SerializedName("menuId")
    @Expose
    private String menuId;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

}
