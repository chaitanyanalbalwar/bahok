package com.app.bahokrider.pojos;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TargetObj implements Serializable {

    @SerializedName("range")
    @Expose
    private String range;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getRange() {
        if (TextUtils.isEmpty(range))
            range = "0-0";
        return range;
    }

    public String getAmount() {
        if (TextUtils.isEmpty(amount))
            amount = "0";
        return amount;
    }
}
