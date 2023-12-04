
package com.utility.app.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurveyRequest {

    @SerializedName("text")
    @Expose
    private String text = "";
    @SerializedName("media")
    @Expose
    private String media = "";
    @SerializedName("mediaType")
    @Expose
    private String mediaType = "";
    @SerializedName("inputDataType")
    @Expose
    private String inputDataType = "";

    @SerializedName("stepId")
    @Expose
    private String stepId= "";


    @SerializedName("menuName")
    @Expose
    private String menuName= "";

    @SerializedName("stepName")
    @Expose
    private String stepName= "";

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    @SerializedName("procedureName")
    @Expose
    private String procedureName= "";


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @SerializedName("priority")
    @Expose
    private String priority = "";

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    @SerializedName("menuId")
    @Expose
    private String menuId = "";

    @SerializedName("procedureId")
    @Expose
    private String procedureId = "";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getInputDataType() {
        return inputDataType;
    }

    public void setInputDataType(String inputDataType) {
        this.inputDataType = inputDataType;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

}
