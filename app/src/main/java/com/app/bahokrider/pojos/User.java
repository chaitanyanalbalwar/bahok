package com.app.bahokrider.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("delivery_boy_id")
    @Expose
    private String userId;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobileno")
    @Expose
    private String mobileno;
    @SerializedName("aadharno")
    @Expose
    private String aadharno;
    @SerializedName("licenseno")
    @Expose
    private String licenseno;
    @SerializedName("userImage")
    @Expose
    private String userImage;
    @SerializedName("aadharImage")
    @Expose
    private String aadharImage;
    @SerializedName("licenseImage")
    @Expose
    private String licenseImage;

    public String getUserId() {
        return userId;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileno() {
        return mobileno;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getAadharno() {
        return aadharno;
    }

    public String getLicenseno() {
        return licenseno;
    }

    public String getAadharImage() {
        return aadharImage;
    }

    public String getLicenseImage() {
        return licenseImage;
    }
}
