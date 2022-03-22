package com.app.bahokrider.pojos;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderMenu implements Serializable {

    @SerializedName("order_menu_id")
    @Expose
    private String menuId;
    @SerializedName("menu_name")
    @Expose
    private String menuName;
    @SerializedName("menu_variation")
    @Expose
    private String menuVariation;
    @SerializedName("menu_price")
    @Expose
    private String menuPrice = "0";
    @SerializedName("qty")
    @Expose
    private String qty = "0";
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("menu_logo")
    @Expose
    private String menuLogo;

    public String getMenuId() {
        return menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuVariation() {
        return menuVariation;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public String getQty() {
        return qty;
    }

    public String getCurrency() {
        return currency;
    }

    public String getMenuTotalPrice() {
        if (TextUtils.isEmpty(qty))
            qty = "0";
        if (TextUtils.isEmpty(menuPrice))
            menuPrice = "0";
        return (Double.parseDouble(qty) * Double.parseDouble(menuPrice)) + "";
    }

    public String getMenuLogo() {
        return menuLogo;
    }
}
