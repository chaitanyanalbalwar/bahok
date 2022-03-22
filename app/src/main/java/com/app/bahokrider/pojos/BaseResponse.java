package com.app.bahokrider.pojos;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private final int success = 0;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("userdetails")
    @Expose
    private User userData;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("order_details")
    @Expose
    private Order Order;
    @SerializedName("availability")
    @Expose
    private final boolean riderStatus = false;
    @SerializedName("current_balance")
    @Expose
    private final int walletAmount = 0;
    @SerializedName("today_earnings")
    @Expose
    private String todaysEarnings;
    @SerializedName("today_points")
    @Expose
    private String todaysPoints;
    @SerializedName("daily_target")
    @Expose
    private String dailyTarget;
    @SerializedName("login_hours")
    @Expose
    private String loginHours;
    @SerializedName("order_list")
    @Expose
    private final List<Order> orderList = new ArrayList<>();
    @SerializedName("transactions")
    @Expose
    private final List<Transaction> transList = new ArrayList<>();
    @SerializedName("notifications")
    @Expose
    private final List<Notification> notiList = new ArrayList<>();
    @SerializedName("bank_details")
    @Expose
    private final List<BankObj> bankList = new ArrayList<>();
    @SerializedName("target_pay")
    @Expose
    private final List<TargetObj> targetPays = new ArrayList<>();

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getOTP() {
        return otp;
    }

    public User getUserData() {
        return userData;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public String getOrderId() {
        return orderId;
    }

    public Order getOrderData() {
        return Order;
    }

    public boolean isRiderStatus() {
        return riderStatus;
    }

    public int getWalletAmount() {
        return walletAmount;
    }

    public String getDailyTarget() {
        if (TextUtils.isEmpty(dailyTarget))
            dailyTarget = "00 - 00";
        return dailyTarget;
    }

    public String getTodaysEarnings() {
        if (TextUtils.isEmpty(todaysEarnings))
            todaysEarnings = "0";
        return todaysEarnings;
    }

    public String getTodaysPoints() {
        if (TextUtils.isEmpty(todaysPoints))
            todaysPoints = "0";
        return todaysPoints;
    }

    public List<Transaction> getTransList() {
        return transList;
    }

    public List<Notification> getNotiList() {
        return notiList;
    }

    public List<BankObj> getBankList() {
        return bankList;
    }

    public List<TargetObj> getTargetPays() {
        return targetPays;
    }

    public String getLoginHours() {
        if (TextUtils.isEmpty(loginHours))
            loginHours = "0";
        return loginHours;
    }
}
