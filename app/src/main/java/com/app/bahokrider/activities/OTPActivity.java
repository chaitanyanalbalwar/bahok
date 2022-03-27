package com.app.bahokrider.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.User;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.ActivityNavigator;
import com.app.bahokrider.utils.AppConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.HashMap;
import java.util.Map;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    private static final String TAG = OTPActivity.class.getSimpleName();

    EditText edOTP1, edOTP2, edOTP3, edOTP4;
    Button btnSubmit;
    TextView tvBack;
    TextView tvMobile;
    TextView tvResend;
    String mobile = "";
    String OTP = "";

    private CustomProgressDialog progressDialog;

    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initview();
        addListener();
        initProgressBar();

        mobile = getIntent().getStringExtra("mobile");
        OTP = getIntent().getStringExtra("otp");

        tvMobile.setText(mobile);

        edOTP1.setText(OTP.substring(0, 1));
        edOTP2.setText(OTP.substring(1, 2));
        edOTP3.setText(OTP.substring(2, 3));
        edOTP4.setText(OTP.substring(3, 4));
    }

    //Initialize controls
    private void initview() {
        edOTP1 = findViewById(R.id.edOTP1);
        edOTP2 = findViewById(R.id.edOTP2);
        edOTP3 = findViewById(R.id.edOTP3);
        edOTP4 = findViewById(R.id.edOTP4);
        tvResend = findViewById(R.id.tvResend);
        tvBack = findViewById(R.id.tvBack);
        tvMobile = findViewById(R.id.tvMobile);
        btnSubmit = findViewById(R.id.btnSubmit);

        edOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().length() > 0)
                    edOTP2.requestFocus();

            }
        });
        edOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().length() > 0)
                    edOTP3.requestFocus();

            }
        });
        edOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().length() > 0)
                    edOTP4.requestFocus();

            }
        });

    }

    // click listener for the buttons
    private void addListener() {
        btnSubmit.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        tvResend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                submitButtonClick();
                break;
            case R.id.tvBack:
                onBackPressed();
                break;
            case R.id.tvResend:
                onResendClick();
                break;
        }
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
    public void onResponse(Object response, String requestTag) {
        hideProgressBar();

        if (requestTag.contains(APIs.VERIFY_OTP)) {
            BaseResponse result = (BaseResponse) response;

            User uObj = result.getUserData();

            if (uObj == null) {
                onError(result.getMessage());
                return;
            }

            SharedPreferencesManager.getInstance().setSomeStringValue(this, AppConstants.USER_ID, uObj.getUserId());
            SharedPreferencesManager.getInstance().setSomeStringValue(this, AppConstants.EMAIL, uObj.getEmail());
            SharedPreferencesManager.getInstance().setSomeStringValue(this, AppConstants.FNAME, uObj.getFname());
            SharedPreferencesManager.getInstance().setSomeStringValue(this, AppConstants.LNAME, uObj.getLname());
            SharedPreferencesManager.getInstance().setSomeStringValue(this, AppConstants.IMAGE, uObj.getUserImage());
            SharedPreferencesManager.getInstance().setSomeStringValue(this, AppConstants.MOBILE, uObj.getMobileno());

            ActivityNavigator.launchMainScreenActivity(this);

        } else if (requestTag.contains(APIs.REQUEST_OTP)) {

            BaseResponse result = (BaseResponse) response;

            OTP = result.getOTP();
            edOTP1.setText(OTP.charAt(0));
            edOTP2.setText(OTP.charAt(1));
            edOTP3.setText(OTP.charAt(2));
            edOTP4.setText(OTP.charAt(3));

            onError("OTP has been sent on your mobile number.");


        }
    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();

        if (errorMessage.equals("failed")) {
            Intent intent = new Intent(this, SignupActivity.class);
            intent.putExtra("mobile", mobile);
            finish();
            startActivity(intent);
            return;
        }

        ToastManager.getInstance().showLongToast(this, errorMessage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 67) {
            if (edOTP1.hasFocus()) {
                edOTP1.setText("");
            } else if (edOTP2.hasFocus()) {
                edOTP2.setText("");
                edOTP1.performClick();
                edOTP1.requestFocus();
            } else if (edOTP3.hasFocus()) {
                edOTP3.setText("");
                edOTP2.performClick();
                edOTP2.requestFocus();
            } else if (edOTP4.hasFocus()) {
                edOTP4.setText("");
                edOTP3.performClick();
                edOTP3.requestFocus();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void submitButtonClick() {

        String otp1 = edOTP1.getText().toString().trim();
        String otp2 = edOTP2.getText().toString().trim();
        String otp3 = edOTP3.getText().toString().trim();
        String otp4 = edOTP4.getText().toString().trim();

        if (TextUtils.isEmpty(otp1) || TextUtils.isEmpty(otp2)
                || TextUtils.isEmpty(otp3) || TextUtils.isEmpty(otp4)) {
            onError("Please enter OTP. We sent you on your number");
            return;
        }

        String otp = otp1 + otp2 + otp3 + otp4;

        if (!otp.equals(OTP)) {
            onError("Invalid OTP. Please enter valid OTP.");
            return;
        }

       // String deviceToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    String token = task.getResult();
                    deviceToken = task.getResult();

                }
            }
        });

        showProgressBar();

        ServiceProvider.getInstance(this).verifyOTP(mobile, deviceToken);

    }

    private void onResendClick() {

        showProgressBar();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("mobileno", mobile);

        ServiceProvider.getInstance(this).requestOTP(mobile);

    }


}
