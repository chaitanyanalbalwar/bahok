package com.app.bahokrider.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.activities.MainScreenActivity;
import com.app.bahokrider.activities.NotificationsActivity;
import com.app.bahokrider.activities.OrderDetailsActivity;
import com.app.bahokrider.activities.WalletActivity;
import com.app.bahokrider.adapters.ActiveOrdersAdapter;
import com.app.bahokrider.adapters.TargetPayAdapter;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.Order;
import com.app.bahokrider.pojos.TargetObj;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class HomeFragment extends Fragment implements IProgressBar, IServiceCallbacks, IRecyclerViewListener {

    private RecyclerView rvOrders;
    private RecyclerView rvTarPays;
    private TextView tvName, tvEmail;
    private LinearLayout llWallet, llBottomOrderView;
    private TextView tvWallet;
    private TextView tvPoints;
    private TextView tvEarnings;
    private RelativeLayout tvNoti;
    private CustomProgressDialog progressDialog;
    private Context mContext;

    private TextView tvStatus;
    private TextView tvTouchPoints, tvTargetPay, tvLoginHours, tvDailyTarget;
    private ProgressBar pbTarget;
    private Switch switchOnOff;

    private String userId;
    private ActiveOrdersAdapter mOrdersAdapter;
    private TargetPayAdapter mPayAdapter;
    private List<Order> orderList;
    private List<TargetObj> targetList;

    public static HomeFragment newInstance(Bundle argument) {
        HomeFragment fragment = new HomeFragment();
        if (null != argument) {
            fragment.setArguments(argument);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initView(view);
        initListeners();

        initProgressBar();

        setAdapters();

        userId = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.USER_ID);
        String fName = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.FNAME);
        String lName = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.LNAME);
        String email = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.EMAIL);

        tvName.setText(fName + " " + lName);
        tvEmail.setText(email);

    }

    @Override
    public void onResume() {
        super.onResume();
        getHomeData();
    }

    private void initListeners() {

        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked && orderList.size() > 0) {
                    showValidationToast();
                    switchOnOff.setChecked(true);
                    return;
                }


                MainScreenActivity activity = (MainScreenActivity) getActivity();

                if (isChecked) {
                    if (activity != null)
                        activity.permissionAndTracking();
                } else {
                    if (activity != null)
                        activity.stopTracking();
                }

                activeInactiveRider(isChecked);
                tvStatus.setText(isChecked ? "Online" : "Offline");
            }
        });

        llWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WalletActivity.class));
            }
        });


        tvNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });


    }

    private void showValidationToast() {
        ToastManager.getInstance().showLongToast(mContext, getString(R.string.invalid_action));
    }

    private void initView(View view) {
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        llWallet = view.findViewById(R.id.llWallet);
        tvWallet = view.findViewById(R.id.tvWallet);
        tvEarnings = view.findViewById(R.id.tvEarnings);
        tvPoints = view.findViewById(R.id.tvPoints);
        rvOrders = view.findViewById(R.id.rvOrders);
        llBottomOrderView = view.findViewById(R.id.llBottomOrderView);
        switchOnOff = view.findViewById(R.id.switchOnOff);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvNoti = view.findViewById(R.id.tvNoti);
        pbTarget = view.findViewById(R.id.pbTarget);
        tvTouchPoints = view.findViewById(R.id.tvTouchPoints);
        tvTargetPay = view.findViewById(R.id.tvTargetPay);
        rvTarPays = view.findViewById(R.id.rvTarPays);
        tvLoginHours = view.findViewById(R.id.tvLoginHours);
        tvDailyTarget = view.findViewById(R.id.tvDailyTarget);
    }

    @Override
    public void initProgressBar() {
        progressDialog = new CustomProgressDialog(mContext);
        progressDialog.setCancelable(false);
        if (progressDialog.getWindow() != null)
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void showProgressBar() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    @Override
    public void hideProgressBar() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onResponse(Object response, String requestTag) {

        hideProgressBar();

        if (requestTag.contains(APIs.HOME_DATA)) {

            BaseResponse result = (BaseResponse) response;

            switchOnOff.setChecked(result.isRiderStatus());

            MainScreenActivity activity = (MainScreenActivity) getActivity();
            if (activity != null)
                activity.updateWalletAmount("" + result.getWalletAmount());

            if (result.isRiderStatus()) {
                tvStatus.setText("Online");
                if (activity != null)
                    activity.permissionAndTracking();
            } else
                tvStatus.setText("Offline");

            tvWallet.setText("BDT\n" + result.getWalletAmount());

            tvEarnings.setText("BDT " + result.getTodaysEarnings());

            tvPoints.setText(result.getTodaysPoints());
            tvTouchPoints.setText(result.getTodaysPoints());
            tvTargetPay.setText("BDT " + result.getTodaysEarnings());
            tvLoginHours.setText(result.getLoginHours() + " Hrs");
            tvDailyTarget.setText(result.getDailyTarget());

            List<Order> list = result.getOrderList();
            mOrdersAdapter.setOrderList(list);

            List<TargetObj> tList = result.getTargetPays();
            mPayAdapter.setOrderList(tList, Integer.parseInt(result.getTodaysPoints()));

            int maProgress = 100;

            if (tList.size() > 0)
                maProgress = Integer.parseInt(tList.get(tList.size() - 1).getRange());

            pbTarget.setMax(maProgress);

            if (Integer.parseInt(result.getTodaysPoints()) > maProgress)
                pbTarget.setProgress(maProgress);
            else
                pbTarget.setProgress(Integer.parseInt(result.getTodaysPoints()));

            orderList.clear();
            orderList.addAll(list);

            targetList.clear();
            targetList.addAll(tList);

            if (list.size() > 0)
                llBottomOrderView.setVisibility(View.VISIBLE);
            else
                llBottomOrderView.setVisibility(View.GONE);

        } else if (requestTag.contains(APIs.RIDER_ACTIVATION)) {
            BaseResponse result = (BaseResponse) response;
        }

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(mContext, errorMessage);
    }

    @Override
    public void onItemClickListener(View view, int position) {
        nextActivity(orderList.get(position).getOrderId());
    }

    private void nextActivity(String orderId) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

    private void getHomeData() {

        int status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            DialogFragment fragment = new NoInternetDialogFragment();
            fragment.setCancelable(false);
            assert getFragmentManager() != null;
            fragment.show(getFragmentManager(), "");
            return;
        }

        showProgressBar();
        ServiceProvider.getInstance(this).getHomeData(userId);

    }

    private void activeInactiveRider(boolean isChecked) {

        showProgressBar();

        RequestBody rbRiderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        RequestBody rbStatus =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), isChecked ? "1" : "0");

        ServiceProvider.getInstance(this).activeInactiveRider(rbRiderId, rbStatus);
    }

    private void setAdapters() {

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvOrders.setLayoutManager(linearLayoutManager);

        if (orderList == null)
            orderList = new ArrayList<>();
        mOrdersAdapter = new ActiveOrdersAdapter(this, orderList);
        rvOrders.setAdapter(mOrdersAdapter);


        LinearLayoutManager linearLayoutManager1 =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        if (targetList == null)
            targetList = new ArrayList<>();
        mPayAdapter = new TargetPayAdapter(this, targetList, 0);
        rvTarPays.setLayoutManager(linearLayoutManager1);
        rvTarPays.setAdapter(mPayAdapter);

    }

}
