package com.app.bahokrider.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.activities.OrderDetailsActivity;
import com.app.bahokrider.adapters.OrdersAdapter;
import com.app.bahokrider.R;
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


public class OrderHistoryFragment extends Fragment implements IProgressBar, IServiceCallbacks, IRecyclerViewListener {

    RecyclerView topJobsRecyclerView;
    OrdersAdapter mOrdersAdapter;
    private CustomProgressDialog progressDialog;
    private Context mContext;
    private String userId;
    private List<Order> orderList;


    public static OrderHistoryFragment newInstance(Bundle argument) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orderhistory, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity();
        initView(view);
        setLayoutManager();
        initProgressBar();

        setAdapter();

        userId = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.USER_ID);

//        getTempOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        getTempOrders();
        System.out.println("orderhistory");
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

        if (requestTag.contains(APIs.GET_ORDER_HISTORY)) {
            BaseResponse result = (BaseResponse) response;
            List<Order> list = result.getOrderList();
            orderList.clear();
            orderList.addAll(list);
            mOrdersAdapter.setOrderList(list);
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

    private void getTempOrders() {

        int status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(getActivity(), getString(R.string.dialog_no_internet));
            return;
        }

        showProgressBar();

        ServiceProvider.getInstance(this).getOrderHistory(userId);

    }

    private void setLayoutManager() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        topJobsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setAdapter() {
        if (orderList == null)
            orderList = new ArrayList<>();
        mOrdersAdapter = new OrdersAdapter(this, orderList);
        topJobsRecyclerView.setAdapter(mOrdersAdapter);
    }

    private void initView(View view) {
        topJobsRecyclerView = view.findViewById(R.id.rv_matchingjobs);
    }

}
