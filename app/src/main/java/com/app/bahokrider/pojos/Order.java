package com.app.bahokrider.pojos;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @SerializedName("ordered_user_name")
    @Expose
    private String userName;

    @SerializedName("ordered_user_image")
    @Expose
    private String userImage;

    @SerializedName("ordered_user_address")
    @Expose
    private String userAddress;

    @SerializedName("orderes_user_latitude")
    @Expose
    private String userLat;
    @SerializedName("orderes_user_longitude")
    @Expose
    private String userLong;

    @SerializedName("ordered_user_contact")
    @Expose
    private String userContact;

    @SerializedName("cost")
    @Expose
    private String cost;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;

    @SerializedName("delivery_time")
    @Expose
    private String deliveryTime;

    @SerializedName("subtotal")
    @Expose
    private final String subTotal = "0";

    @SerializedName("charges")
    @Expose
    private final String deliveryCharges = "0";

    @SerializedName("cgst")
    @Expose
    private final String cgst = "0";

    @SerializedName("sgst")
    @Expose
    private final String sgst = "0";

    @SerializedName("discount_amount")
    @Expose
    private final String discountAmount = "0";

    @SerializedName("order_status")
    @Expose
    private String orderStatus;

    @SerializedName("type")
    @Expose
    private String orderType;

    @SerializedName("show_to")
    @Expose
    private String showTo;

    @SerializedName("date_time")
    @Expose
    private String dateTime;

    @SerializedName("restaurant_name")
    @Expose
    private String hotelName;

    @SerializedName("restaurant_image")
    @Expose
    private String hotelImage;

    @SerializedName("restaurant_contact")
    @Expose
    private String hotelContact;

    @SerializedName("restaurant_address")
    @Expose
    private String hotelAddress;

    @SerializedName("restaurant_latitude")
    @Expose
    private String hotelLat;

    @SerializedName("restaurant_longitude")
    @Expose
    private String hotelLong;

    @SerializedName("menus")
    @Expose
    private final List<OrderMenu> menuList = new ArrayList<>();


    @SerializedName("delivery_boy_order_status")
    @Expose
    private String deliveryboyordrstatus;



    public String getOrderId() {
        return orderId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserLat() {
        return userLat;
    }

    public String getUserLong() {
        return userLong;
    }

    public String getUserContact() {
        return userContact;
    }

    public String getCost() {
        return cost;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getType() {
        if (TextUtils.isEmpty(orderType))
            orderType = "";
        return orderType;
    }

    public String getShowTo() {
        if (TextUtils.isEmpty(showTo))
            showTo = "";
        return showTo;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getHotelImage() {
        return hotelImage;
    }

    public String getHotelContact() {
        return hotelContact;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public String getHotelLat() {
        return hotelLat;
    }

    public String getHotelLong() {
        return hotelLong;
    }

    public String getCgst() {
        return cgst;
    }

    public String getSgst() {
        return sgst;
    }

    public String getDiscountAmount() {
        return discountAmount;
    }

    public String getTotalTax() {
        return (Double.parseDouble(cgst) + Double.parseDouble(sgst)) + "";
    }

    public List<OrderMenu> getMenuList() {
        return menuList;
    }

    public String getDeliveryboyordrstatus() {
        return deliveryboyordrstatus;
    }
}
