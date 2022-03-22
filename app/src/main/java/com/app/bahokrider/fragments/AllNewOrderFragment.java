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
import android.widget.RelativeLayout;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.activities.OrderDetailsActivity;
import com.app.bahokrider.adapters.ActiveOrdersAdapter;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.Order;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class AllNewOrderFragment extends Fragment implements IProgressBar, IServiceCallbacks, IRecyclerViewListener {

    private RecyclerView rvOrders;
    private ActiveOrdersAdapter mOrdersAdapter;
    private List<Order> orderList;
    private RelativeLayout tvNoti;
    private CustomProgressDialog progressDialog;
    private Context mContext;
    private String userId;


    public static AllNewOrderFragment newInstance(Bundle argument) {
        AllNewOrderFragment fragment = new AllNewOrderFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_new_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initView(view);

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


    }

    @Override
    public void onResume() {
        super.onResume();
        getHomeData();
    }


    private void initView(View view) {

        rvOrders = view.findViewById(R.id.rvAllOrders);
        tvNoti = view.findViewById(R.id.tvNoti);

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
    public void onItemClickListener(View view, int position) {
        nextActivity(orderList.get(position).getOrderId());
    }

    @Override
    public void onResponse(Object response, String requestTag) {
        hideProgressBar();

        if (requestTag.contains(APIs.GET_ORDERS)) {
            BaseResponse result = (BaseResponse) response;

            List<Order> list = result.getOrderList();
            mOrdersAdapter.setOrderList(list);

            orderList.clear();
            orderList.addAll(list);
        }
    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(mContext, errorMessage);
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
       // ServiceProvider.getInstance(this).getHomeData(userId);
          ServiceProvider.getInstance(this).getOrders(userId);

    }

    private void setAdapters() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvOrders.setLayoutManager(linearLayoutManager);

        if (orderList == null)
            orderList = new ArrayList<>();
        mOrdersAdapter = new ActiveOrdersAdapter(this, orderList);
        rvOrders.setAdapter(mOrdersAdapter);

    }

    private void nextActivity(String orderId) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }
}