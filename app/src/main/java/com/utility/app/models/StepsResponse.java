
package com.utility.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StepsResponse implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("media")
    @Expose
    private String media;
    @SerializedName("mediaType")
    @Expose
    private String mediaType;
    @SerializedName("inputDataType")
    @Expose
    private String inputDataType;
    @SerializedName("procedureId")
    @Expose
    private String procedureId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    private boolean isInputSubmitted;

    public boolean isInputSubmitted() {
        return isInputSubmitted;
    }

    public void setInputSubmitted(boolean inputSubmitted) {
        isInputSubmitted = inputSubmitted;
    }

    private boolean isTextSubmitted;

    public boolean isTextSubmitted() {
        return isTextSubmitted;
    }

    public void setTextSubmitted(boolean textSubmitted) {
        isTextSubmitted = textSubmitted;
    }

    public boolean isMediaSubmitted() {
        return isMediaSubmitted;
    }

    public void setMediaSubmitted(boolean mediaSubmitted) {
        isMediaSubmitted = mediaSubmitted;
    }

    private boolean isMediaSubmitted;

    public Uri getFileURI() {
        return fileURI;
    }

    public void setFileURI(Uri fileURI) {
        this.fileURI = fileURI;
    }

    private Uri fileURI;
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @SerializedName("priority")
    @Expose
    private String priority;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;


    public StepsResponse() {

    }

    protected StepsResponse(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        media = in.readString();
        mediaType = in.readString();
        inputDataType = in.readString();
        procedureId = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
    }

    public static final Creator<StepsResponse> CREATOR = new Creator<StepsResponse>() {
        @Override
        public StepsResponse createFromParcel(Parcel in) {
            return new StepsResponse(in);
        }

        @Override
        public StepsResponse[] newArray(int size) {
            return new StepsResponse[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(media);
        dest.writeString(mediaType);
        dest.writeString(inputDataType);
        dest.writeString(procedureId);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
    }

    @Override
    public String toString() {
        return name;
    }
}
