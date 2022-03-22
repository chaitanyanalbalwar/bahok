package com.app.bahokrider.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notification implements Serializable {

    @SerializedName("rider_notifications_id")
    @Expose
    private String notiId;
    @SerializedName("notification")
    @Expose
    private String message;
    @SerializedName("ordertype")
    @Expose
    private String type;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("rider_notifications_created_at")
    @Expose
    private String dateTime;

    public String getNotiId() {
        return notiId;
    }

    public String getMessage() {
        return message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getType() {
        return type;
    }

    public String getOrderId() {
        return orderId;
    }
}
