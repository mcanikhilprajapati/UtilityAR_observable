
package com.utility.app.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("jwt")
    @Expose
    private String jwt;
    @SerializedName("chatThreadId")
    @Expose
    private String chatThreadId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("communicationUserId")
    @Expose
    private String communicationUserId;
    @SerializedName("groupCallId")
    @Expose
    private String groupCallId;
    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * No args constructor for use in serialization
     */
    public LoginResponse() {
    }

    /**
     * @param communicationUserId
     * @param jwt
     * @param name
     * @param isAdmin
     * @param chatThreadId
     * @param groupCallId
     * @param email
     */
    public LoginResponse(String jwt, String chatThreadId, String name, String communicationUserId, String groupCallId, Boolean isAdmin, String email) {
        super();
        this.jwt = jwt;
        this.chatThreadId = chatThreadId;
        this.name = name;
        this.communicationUserId = communicationUserId;
        this.groupCallId = groupCallId;
        this.isAdmin = isAdmin;
        this.email = email;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getChatThreadId() {
        return chatThreadId;
    }

    public void setChatThreadId(String chatThreadId) {
        this.chatThreadId = chatThreadId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommunicationUserId() {
        return communicationUserId;
    }

    public void setCommunicationUserId(String communicationUserId) {
        this.communicationUserId = communicationUserId;
    }

    public String getGroupCallId() {
        return groupCallId;
    }

    public void setGroupCallId(String groupCallId) {
        this.groupCallId = groupCallId;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LoginResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("jwt");
        sb.append('=');
        sb.append(((this.jwt == null) ? "<null>" : this.jwt));
        sb.append(',');
        sb.append("chatThreadId");
        sb.append('=');
        sb.append(((this.chatThreadId == null) ? "<null>" : this.chatThreadId));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("communicationUserId");
        sb.append('=');
        sb.append(((this.communicationUserId == null) ? "<null>" : this.communicationUserId));
        sb.append(',');
        sb.append("groupCallId");
        sb.append('=');
        sb.append(((this.groupCallId == null) ? "<null>" : this.groupCallId));
        sb.append(',');
        sb.append("isAdmin");
        sb.append('=');
        sb.append(((this.isAdmin == null) ? "<null>" : this.isAdmin));
        sb.append(',');
        sb.append("email");
        sb.append('=');
        sb.append(((this.email == null) ? "<null>" : this.email));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}