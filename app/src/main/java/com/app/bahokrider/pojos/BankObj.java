package com.app.bahokrider.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BankObj implements Serializable {

    @SerializedName("bank_details_id")
    @Expose
    private String idId;
    @SerializedName("acc_name")
    @Expose
    private String accName;
    @SerializedName("acc_no")
    @Expose
    private String accNumber;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("ifsc")
    @Expose
    private String ifsc;
    @SerializedName("cheque_image")
    @Expose
    private String chequeImage;

    public String getIdId() {
        return idId;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getAccName() {
        return accName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getChequeImage() {
        return chequeImage;
    }
}
