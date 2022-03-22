package com.app.bahokrider.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.adapters.WalletAdapter;
import com.app.bahokrider.fragments.UpdateWalletDialogFragment;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.Transaction;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.NetworkUtil;
import com.easebuzz.payment.kit.PWECouponsActivity;

//import io.flutter.plugin.common.MethodChannel;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import datamodels.PWEStaticDataModel;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WalletActivity extends AppCompatActivity implements IProgressBar, IRecyclerViewListener, IServiceCallbacks {

    TextView toolbar_title;
    ImageView iv_back;

    private CustomProgressDialog progressDialog;
    private RecyclerView rvList;
    private TextView tvBalance;
    private LinearLayout llAdd;

    private WalletAdapter walletAdapter;
    private List<Transaction> list;

    private final String KEY = "N7Q2112ME8";
    private final String SALT = "4ACE76HEX3";
    private final String MODE = "test";

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        initViews();
        addListener();
        initProgressBar();

        toolbar_title.setText(getString(R.string.wallet));
        userId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        setAdapter();
        getWallet();

    }

    @Override
    protected void onDestroy() {
        hideProgressBar();
        super.onDestroy();
    }

    //Initialize controls
    private void initViews() {
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);

        rvList = findViewById(R.id.rvList);
        tvBalance = findViewById(R.id.tvBalance);
        llAdd = findViewById(R.id.llAdd);
    }

    private void setAdapter() {

        if (list == null)
            list = new ArrayList<>();
        walletAdapter = new WalletAdapter(this, list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvList.setAdapter(walletAdapter);
    }

    //Initialize controls
    private void addListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        llAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAmountDialog();
            }
        });
    }


    @Override
    public void onItemClickListener(View view, int position) {

    }

    @Override
    public void onResponse(Object response, String requestTag) {

        hideProgressBar();

        if (requestTag.contains(APIs.GET_MY_WALLER)) {
            BaseResponse result = (BaseResponse) response;

            String balance = String.valueOf(result.getWalletAmount());

            bindData(balance, result.getTransList());

        } else if (requestTag.contains(APIs.ADD_BALANCE)) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.money_added_success));
            getWallet();
        }

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(this, errorMessage);
    }

    @Override
    public void initProgressBar() {
        progressDialog = new CustomProgressDialog(this);
        progressDialog.setCancelable(false);
        if (progressDialog.getWindow() != null)
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void showProgressBar() {
        if (progressDialog != null)
            progressDialog.show();
    }

    @Override
    public void hideProgressBar() {
        if (progressDialog != null)
            progressDialog.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;

        if (requestCode == PWEStaticDataModel.PWE_REQUEST_CODE) {
            String result = data.getStringExtra("result");
            String payment_response = data.getStringExtra("payment_response");
            if ("payment_successfull".equalsIgnoreCase(result))
                onApiCall(payment_response);
            else
                ToastManager.getInstance().showLongToast(WalletActivity.this, result);
        }
    }

    private void getWallet() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        showProgressBar();

        ServiceProvider.getInstance(this).getMyWallet(userId);

    }

    private void openAmountDialog() {

        DialogFragment dialog = UpdateWalletDialogFragment.newInstance();
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), UpdateWalletDialogFragment.class.getName());
    }

    public void callToPaymentGateway(float amountFloat) {

        String name = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.FNAME);
        String email = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.EMAIL);
        String mobile = "+91" + SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.MOBILE);

        String txsId = "TSX_" + userId + "U_" + new Date().getTime() + "T";

        String merchant_udf1 = userId + "ut1";
        String merchant_udf2 = userId + "ut2";
        String merchant_udf3 = userId + "ut3";
        String merchant_udf4 = userId + "ut4";
        String merchant_udf5 = userId + "ut5";

        String hsString = KEY + "|" + txsId + "|" + amountFloat + "|wallet|" + name + "|" + email
                + "|" + merchant_udf1 + "|" + merchant_udf2 + "|" + merchant_udf3 + "|" + merchant_udf4 + "|" + merchant_udf5 + "||||||" + SALT + "|" + KEY;

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] data = md.digest(hsString.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte datum : data)
                sb.append(Integer.toString((datum & 0xff) + 0x100, 16).substring(1));

            String hash = sb.toString();

            Intent intentProceed = new Intent(this, PWECouponsActivity.class);
            intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // This is mandatory flag
            intentProceed.putExtra("txnid", txsId);
            intentProceed.putExtra("amount", amountFloat);
            intentProceed.putExtra("productinfo", "wallet");
            intentProceed.putExtra("firstname", name);
            intentProceed.putExtra("email", email);
            intentProceed.putExtra("phone", mobile);
            intentProceed.putExtra("key", KEY);
            intentProceed.putExtra("udf1", merchant_udf1);
            intentProceed.putExtra("udf2", merchant_udf2);
            intentProceed.putExtra("udf3", merchant_udf3);
            intentProceed.putExtra("udf4", merchant_udf4);
            intentProceed.putExtra("udf5", merchant_udf5);
            intentProceed.putExtra("hash", hash);
            intentProceed.putExtra("pay_mode", MODE);
            startActivityForResult(intentProceed, PWEStaticDataModel.PWE_REQUEST_CODE);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void onApiCall(String payment_response) {


        try {
            JSONObject jsonObject = new JSONObject(payment_response);

            String trnsid = jsonObject.getString("txnid");
            String amount = jsonObject.getString("net_amount_debit");

            showProgressBar();

            RequestBody rbRiderId =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), userId);

            RequestBody rbTransId =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), trnsid);

            RequestBody rbAmount =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), amount);

            ServiceProvider.getInstance(this).addBalance(rbRiderId, rbTransId, rbAmount);

        } catch (Exception ex) {
            Log.e("exception ::: ", ex.toString());
        }
    }

    private void bindData(String balance, List<Transaction> transList) {

        tvBalance.setText("BDT\n" + balance);

        list.clear();
        if (transList!=null){
            list.addAll(transList);
            walletAdapter.notifyDataSetChanged();
        }
    }


}








