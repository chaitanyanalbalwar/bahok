package com.app.bahokrider.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.bahokrider.CircularImageView;
import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.adapters.OrderMenuAdapter;
import com.app.bahokrider.fragments.ConfirmationDialogFragment;
import com.app.bahokrider.fragments.ConfirmationPaymentDialogFragment;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.GlideManager;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.Order;
import com.app.bahokrider.pojos.OrderMenu;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.DialogUtil;
import com.app.bahokrider.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    boolean isTimerStarted = false;
    boolean isStart = false;

    Handler handler = new Handler();

    public Runnable refresher = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("refresher");
                if (!isStart)
                    return;
                getOrderDetails();
                handler.postDelayed(refresher, 10 * 1000); // 5 seconds
            } catch (Exception e) {
                System.out.println("e");
            }
        }
    };


    private TextView toolbar_title;
    private ImageView iv_back;
    private NestedScrollView nstScroll;

    private CustomProgressDialog progressDialog;

    private CircularImageView imgHotel, imgProfile;
    private TextView tvOrderId, tvHotel, tvHotelAddress, tvHotelContact, tvMode, tvDatetime, tvAmount;

    private TextView tvUserName, tvUserLoc, tvUserContact, tvSuccess;
    private TextView tvSubtotal, tvTax, tvDeliveryCharge, tvDiscountCharge, tvFinalAmount;

    private LinearLayout llTrack, llCallCustomer, llCallRestro;

    private Button btnAccept, btnPick, btnReached, btnDelivered;
    RecyclerView rvItems;
    TextView tvFoodReady;
    ProgressBar progressBar;

    String userId;
    String orderId;
    float walletBalance;

    OrderMenuAdapter menuAdapter;
    List<OrderMenu> menuList;

    private int onContact = -1;
    private Order orderObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        initViews();
        addListener();
        initProgressBar();

        toolbar_title.setText(getString(R.string.order_details));

        userId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        orderId = getIntent().getStringExtra("orderId");
        walletBalance = 0;

        tvOrderId.setText(getString(R.string.order_tag) + orderId);

        setAdapter();

//        showProgressBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        isStart = true;
        getOrderDetails();
    }

    @Override
    protected void onStop() {
        System.out.println("onStop");
        hideProgressBar();
        isStart = false;
        progressBar.setVisibility(View.GONE);
        stopInterval();
        super.onStop();
    }

    private void startInterval() {

        try {
            stopInterval();
            isTimerStarted = true;
            refresher.run();
        } catch (Exception e) {

        }
    }

    private void stopInterval() {
        try {
            handler.removeCallbacks(refresher);
            handler.removeCallbacksAndMessages(null);
            isTimerStarted = false;
        } catch (Exception e) {

        }

    }

    private void setAdapter() {

        if (menuList == null)
            menuList = new ArrayList<>();
        menuAdapter = new OrderMenuAdapter(menuList);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(menuAdapter);
    }

    private void initViews() {
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);
        imgHotel = findViewById(R.id.imgHotel);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvHotelAddress = findViewById(R.id.tvHotelAddress);
        tvHotelContact = findViewById(R.id.tvHotelContact);
        tvHotel = findViewById(R.id.tvHotel);
        tvMode = findViewById(R.id.tvMode);
        tvDatetime = findViewById(R.id.tvDatetime);
        tvAmount = findViewById(R.id.tvAmount);
        imgProfile = findViewById(R.id.imgProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserLoc = findViewById(R.id.tvUserLoc);
        tvUserContact = findViewById(R.id.tvUserContact);
        rvItems = findViewById(R.id.rvItems);
        btnAccept = findViewById(R.id.btnAccept);
        btnPick = findViewById(R.id.btnPick);
        btnDelivered = findViewById(R.id.btnDelivered);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        tvFinalAmount = findViewById(R.id.tvFinalAmount);
        tvTax = findViewById(R.id.tvTax);
        tvDiscountCharge = findViewById(R.id.tvDiscountCharge);

        tvFoodReady = findViewById(R.id.tvFoodReady);
        btnReached = findViewById(R.id.btnReached);

        llTrack = findViewById(R.id.llTrack);
        llCallCustomer = findViewById(R.id.llCallCustomer);
        llCallRestro = findViewById(R.id.llCallRestro);

        nstScroll = findViewById(R.id.nstScroll);
        tvSuccess = findViewById(R.id.tvSuccess);
        progressBar = findViewById(R.id.progressBar);

    }

    // click listener for the buttons
    private void addListener() {

        iv_back.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnPick.setOnClickListener(this);
        btnReached.setOnClickListener(this);
        btnDelivered.setOnClickListener(this);
        llTrack.setOnClickListener(this);
        llCallCustomer.setOnClickListener(this);
        llCallRestro.setOnClickListener(this);

        nstScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (!nestedScrollView.canScrollVertically(1)) {
                    llTrack.setVisibility(View.GONE);
                    llCallCustomer.setVisibility(View.GONE);
                    llCallRestro.setVisibility(View.GONE);
                } else {
                    if (orderObj.getOrderStatus() != null && !orderObj.getOrderStatus().equalsIgnoreCase("delivered")) {
                        llTrack.setVisibility(View.VISIBLE);
                        llCallCustomer.setVisibility(View.VISIBLE);
                        llCallRestro.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btnReject:
                openDialog(orderId);
                break;
            case R.id.btnAccept:
                onAcceptPressed();
                break;
            case R.id.btnPick:
                onPickupPressed();
                break;
            case R.id.btnReached:
                onReachedPressed();
                break;
            case R.id.btnDelivered:
                if (orderObj.getPaymentMode().equalsIgnoreCase("cash on delivery"))
                    openPaymentConfirmationDialog();
                else
                    onDeliveryPressed();
                break;
            case R.id.llTrack:
                onTrackingPressed();
                break;
            case R.id.llCallRestro:
                onCallRestroPressed();
                break;
            case R.id.llCallCustomer:
                onCallCustomerPressed();
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
        System.out.println("onshow");
        try {
            if (progressDialog != null && isStart)
                progressDialog.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void hideProgressBar() {
        System.out.println("onhide");
        if (progressDialog != null)
            progressDialog.hide();
    }

    @Override
    public void onResponse(Object response, String requestTag) {
        hideProgressBar();
        progressBar.setVisibility(View.GONE);
        if (requestTag.contains(APIs.GET_ORDER_DETAILS)) {
            BaseResponse result = (BaseResponse) response;
            Order uObj = result.getOrderData();
            walletBalance = result.getWalletAmount();

            if (uObj == null)
                return;

            bindData(uObj);
        } else if (requestTag.contains(APIs.ACCEPT_ORDER)) {
            BaseResponse result = (BaseResponse) response;
            onError(getString(R.string.accept_success));
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
            tvFoodReady.setVisibility(View.VISIBLE);
        } else if (requestTag.contains(APIs.REJECT_ORDER)) {
            BaseResponse result = (BaseResponse) response;
            onError(getString(R.string.reject_success));
            onBackPressed();
        } else if (requestTag.contains(APIs.DELIVER_ORDER)) {
            BaseResponse result = (BaseResponse) response;
            onError(result.getMessage());
            showProgressBar();
            getOrderDetails();
        }

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        progressBar.setVisibility(View.GONE);
        ToastManager.getInstance().showLongToast(this, errorMessage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPhonePermissionResult();
            } else {
                DialogUtil.openPermissionDialog(this, getString(R.string.phone_permission_message));
            }
        }
    }

    private void onPhonePermissionResult() {

        if (onContact == 2)
            onCallRestroPressed();
        else
            onCallCustomerPressed();

    }

    private void onCallRestroPressed() {

        onContact = 1;
        String contact = tvHotelContact.getText().toString().trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                return;
            }
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contact));//change the number
        startActivity(callIntent);

    }

    private void onCallCustomerPressed() {

        onContact = 2;
        String contact = tvUserContact.getText().toString().trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 100);
                return;
            }
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contact));//change the number
        startActivity(callIntent);

    }

    private void openDialog(String orderId) {
        DialogFragment dialog = ConfirmationDialogFragment.newInstance(orderId);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), ConfirmationDialogFragment.class.getName());
    }

    private void openPaymentConfirmationDialog() {
        DialogFragment dialog = ConfirmationPaymentDialogFragment.newInstance();
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), ConfirmationPaymentDialogFragment.class.getName());
    }

    private void onAcceptPressed() {

        float orderAmount = Float.parseFloat(orderObj.getCost());

        if (walletBalance < orderAmount && orderObj.getPaymentMode().equalsIgnoreCase("cash on delivery")) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.wallet_error));
            return;
        }

        if (walletBalance < 1000 && orderObj.getPaymentMode().equalsIgnoreCase("cash on delivery")) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.wallet_error_suffi));
            return;
        }

        showProgressBar();

        RequestBody rbOrderId = RequestBody.create(MediaType.parse("multipart/form-data"), orderId);

        RequestBody rbRiderId = RequestBody.create(MediaType.parse("multipart/form-data"), userId);

        RequestBody rbStatus = RequestBody.create(MediaType.parse("multipart/form-data"), "1");

        ServiceProvider.getInstance(this).acceptOrder(rbOrderId, rbRiderId, rbStatus);

    }

    public void onRejectPressed() {

        showProgressBar();

        RequestBody rbOrderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), orderId);

        RequestBody rbRiderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        RequestBody rbStatus =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "0");

        ServiceProvider.getInstance(this).rejectOrder(rbOrderId, rbRiderId, rbStatus);
    }

    private void onPickupPressed() {

        showProgressBar();


        RequestBody rbOrderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), orderId);

        RequestBody rbStatus =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "5");

        ServiceProvider.getInstance(this).deliverOrder(rbOrderId, rbStatus);

    }

    private void onReachedPressed() {

        showProgressBar();

        RequestBody rbOrderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), orderId);

        RequestBody rbStatus =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "8");

        ServiceProvider.getInstance(this).deliverOrder(rbOrderId, rbStatus);

    }

    public void onDeliveryPressed() {

        showProgressBar();

        RequestBody rbOrderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), orderId);

        RequestBody rbStatus =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "3");

        ServiceProvider.getInstance(this).deliverOrder(rbOrderId, rbStatus);

    }

    private void onTrackingPressed() {

        Intent intent = new Intent(this, TrackingActivity.class);

        if (orderObj.getOrderStatus().equalsIgnoreCase("confirmed")) {

            if (TextUtils.isEmpty(orderObj.getHotelLat()) || TextUtils.isEmpty(orderObj.getHotelLong())) {
                ToastManager.getInstance().showLongToast(this, getString(R.string.error_hotel_lat_long));
                return;
            }

            intent.putExtra("lant", orderObj.getHotelLat());
            intent.putExtra("long", orderObj.getHotelLong());
            intent.putExtra("name", orderObj.getHotelName());
            intent.putExtra("track", "hotel");

        } else {

            if (TextUtils.isEmpty(orderObj.getUserLat()) || TextUtils.isEmpty(orderObj.getUserLong())) {
                ToastManager.getInstance().showLongToast(this, getString(R.string.error_user_lat_long));
                return;
            }

            intent.putExtra("lant", orderObj.getUserLat());
            intent.putExtra("long", orderObj.getUserLong());
            intent.putExtra("name", orderObj.getUserName());
            intent.putExtra("track", "user");
        }

        startActivity(intent);
    }

    private void bindData(Order uObj) {

        if (!isStart)
            return;

        orderObj = uObj;

        tvHotel.setText(uObj.getHotelName());
        tvHotelAddress.setText(uObj.getHotelAddress());
        tvHotelContact.setText(uObj.getHotelContact());
        tvMode.setText(uObj.getPaymentMode());
        tvDatetime.setText(uObj.getDateTime());
        tvAmount.setText(uObj.getCurrency() + " " + uObj.getCost());

        tvUserName.setText(uObj.getUserName());
        tvUserLoc.setText(uObj.getUserAddress());
        tvUserContact.setText(uObj.getUserContact());

        tvSubtotal.setText(uObj.getCurrency() + " " + uObj.getSubTotal());
        tvDeliveryCharge.setText(uObj.getCurrency() + " " + uObj.getDeliveryCharges());
        tvFinalAmount.setText(uObj.getCurrency() + " " + uObj.getCost());


        tvTax.setText(uObj.getCurrency() + " " + uObj.getTotalTax());
        tvDiscountCharge.setText(uObj.getCurrency() + " " + uObj.getDiscountAmount());

        menuList.clear();
        menuList.addAll(uObj.getMenuList());

        menuAdapter.notifyDataSetChanged();

        GlideManager.setUserImage(this, uObj.getHotelImage(), imgHotel);

        GlideManager.setUserImage(this, uObj.getUserImage(), imgProfile);

       /* if (uObj.getType().toLowerCase().equals("timeout")) {

            tvFoodReady.setVisibility(View.VISIBLE);
            tvFoodReady.setText(getString(R.string.time_out));
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            llTrack.setVisibility(View.GONE);
            llCallCustomer.setVisibility(View.GONE);
            llCallRestro.setVisibility(View.GONE);
            return;
        }

        if (uObj.getType().toLowerCase().equals("notimeout") && !uObj.getShowTo().equalsIgnoreCase(userId)) {
            tvFoodReady.setVisibility(View.VISIBLE);
            tvFoodReady.setText(getString(R.string.time_out));
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            llTrack.setVisibility(View.GONE);
            llCallCustomer.setVisibility(View.GONE);
            llCallRestro.setVisibility(View.GONE);
            return;
        }
*/

        llTrack.setVisibility(View.VISIBLE);
        llCallCustomer.setVisibility(View.VISIBLE);
        llCallRestro.setVisibility(View.VISIBLE);

        if (uObj.getOrderStatus().equalsIgnoreCase("confirmed") && uObj.getDeliveryboyordrstatus().equals("0")) {
            btnAccept.setVisibility(View.VISIBLE);
            btnPick.setVisibility(View.GONE);
            tvFoodReady.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
        } else if (uObj.getOrderStatus().equalsIgnoreCase("accepted")) {
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            tvFoodReady.setVisibility(View.VISIBLE);
            btnDelivered.setVisibility(View.GONE);
        } else if (uObj.getOrderStatus().equalsIgnoreCase("food is ready")) {
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.VISIBLE);
            tvFoodReady.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
        } else if (uObj.getOrderStatus().equalsIgnoreCase("dispatched")) {
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            tvFoodReady.setVisibility(View.VISIBLE);
            tvFoodReady.setText(getString(R.string.food_to_be_deliver));
            btnReached.setVisibility(View.VISIBLE);
            btnDelivered.setVisibility(View.GONE);
        } else if (uObj.getOrderStatus().equalsIgnoreCase("reached")) {
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            tvFoodReady.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.VISIBLE);
        } else if (uObj.getOrderStatus().equalsIgnoreCase("delivered")) {
            btnAccept.setVisibility(View.GONE);
            btnPick.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
            btnReached.setVisibility(View.GONE);
            tvSuccess.setVisibility(View.VISIBLE);

            llTrack.setVisibility(View.GONE);
            llCallCustomer.setVisibility(View.GONE);
            llCallRestro.setVisibility(View.GONE);

            if (isTimerStarted) {
                ToastManager.getInstance().showLongToast(this, getString(R.string.delivered_success));
                stopInterval();
                finishAffinity();
                startActivity(new Intent(this, MainScreenActivity.class));
            }
        }

        if (isTimerStarted)
            return;

        if (!TextUtils.isEmpty(uObj.getOrderStatus()) && !uObj.getOrderStatus().equalsIgnoreCase("delivered"))
            startInterval();

    }

    private void getOrderDetails() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        try {
            ServiceProvider.getInstance(this).getOrderDetails(orderId, userId);
        } catch (Exception e) {

        }

    }

}
