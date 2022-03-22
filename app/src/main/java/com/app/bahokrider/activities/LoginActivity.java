package com.app.bahokrider.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.ActivityNavigator;
import com.app.bahokrider.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    private static final String TAG = LoginActivity.class.getSimpleName();
    EditText etMobile;
    Button loginButton;
    //    TextView forgotPassBtn;
    TextView tv_new_account;

    private CustomProgressDialog progressDialog;

//    LoginContractor.LoginActionListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MultiDex.install(this);
        initView();
        addListener();
        initProgressBar();
    }

    //Initialize controls
    private void initView() {
        etMobile = findViewById(R.id.etMobile);
        loginButton = findViewById(R.id.btnLogin);
//        forgotPassBtn = findViewById(R.id.btnForgot);

        tv_new_account = findViewById(R.id.tv_new_account);
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

    // click listner for the buttons
    private void addListener() {
        loginButton.setOnClickListener(this);
        tv_new_account.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
//                startActivity(new Intent(this, NewOrderActivity.class));

                loginButtonClick();
                break;
//            case R.id.btnForgot:
//                forgotPassBtnClick();
//                break;
            case R.id.tv_new_account:
                newUserTextviewClick();
                break;
        }
    }

    private void newUserTextviewClick() {
        ActivityNavigator.launchSignupActivity(this);
    }

    private void loginButtonClick() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        String phoneNumber = etMobile.getText().toString().trim();

        if (phoneNumber.length() !=11) {
            etMobile.setError("Invalid mobile number");
            etMobile.requestFocus();
            return;
        }

        showProgressBar();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("mobileno", phoneNumber);

        ServiceProvider.getInstance(this).requestOTP(phoneNumber);

    }

    @Override
    public void onResponse(Object response, String requestTag) {
        hideProgressBar();
        if (requestTag.contains(APIs.REQUEST_OTP)) {
            BaseResponse result = (BaseResponse) response;

//            onError(result.getOTP());

            Intent intent = new Intent(this, OTPActivity.class);
            intent.putExtra("otp", result.getOTP());
            intent.putExtra("mobile", etMobile.getText().toString().trim());
            startActivity(intent);

        }

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(this, errorMessage);
    }


}
