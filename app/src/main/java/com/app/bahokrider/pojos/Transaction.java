package com.app.bahokrider.pojos;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable {

    @SerializedName("transaction_id")
    @Expose
    private String transId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("delivery_boy_transaction_status")
    @Expose
    private String tranStatus;
    @SerializedName("delivery_boy_transaction_created_at")
    @Expose
    private String dateTime;

    public String getTransId() {
        return transId;
    }

    public String getAmount() {
        return amount;
    }

    public String getTranStatus() {
        if (TextUtils.isEmpty(tranStatus))
            tranStatus = "";
        return tranStatus;
    }

    public String getDateTime() {
        return dateTime;
    }
}
