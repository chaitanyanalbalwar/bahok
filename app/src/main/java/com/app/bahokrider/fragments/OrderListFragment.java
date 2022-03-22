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

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OrderListFragment extends Fragment implements IProgressBar, IServiceCallbacks, IRecyclerViewListener {

    private RecyclerView topJobsRecyclerView;
    private CustomProgressDialog progressDialog;
    private Context mContext;

    private OrdersAdapter mOrdersAdapter;
    private String userId;
    private List<Order> orderList;

    public static OrderListFragment newInstance(Bundle argument) {
        OrderListFragment fragment = new OrderListFragment();
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
        return inflater.inflate(R.layout.fragment_orderlist, container, false);
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
        System.out.println("orderlist");
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

    private void getTempOrders() {

        int status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(getActivity(), getString(R.string.dialog_no_internet));
            return;
        }

        showProgressBar();

        ServiceProvider.getInstance(this).getOrders(userId);

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
        topJobsRecyclerView = view.findViewById(R.id.rv_topjobs);
    }

    @Override
    public void onResponse(Object response, String requestTag) {

        hideProgressBar();

        if (requestTag.contains(APIs.GET_ORDERS)) {
            BaseResponse result = (BaseResponse) response;
            List<Order> list = result.getOrderList();
            orderList.clear();
            orderList.addAll(list);
            mOrdersAdapter.setOrderList(orderList);
        } else if (requestTag.contains(APIs.ACCEPT_ORDER)) {
            BaseResponse result = (BaseResponse) response;
            onError(getString(R.string.accept_success));
            getTempOrders();
            nextActivity(result.getOrderId());
        } else if (requestTag.contains(APIs.REJECT_ORDER)) {
            BaseResponse result = (BaseResponse) response;
            onError(getString(R.string.reject_success));
            getTempOrders();
        }

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(mContext, errorMessage);
    }

    @Override
    public void onItemClickListener(View view, int position) {

        if (view.getId() == R.id.btnAccept) {
            acceptRejectOrder("1", orderList.get(position).getOrderId());
        }
//        else if (view.getId() == R.id.btnReject) {
//            openDialog(orderList.get(position).getOrderId());
//        }
        else {
            nextActivity(orderList.get(position).getOrderId());
        }

    }


    private void nextActivity(String orderId) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

    public void acceptRejectOrder(String value, String id) {

        showProgressBar();

        RequestBody rbOrderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), id);

        RequestBody rbRiderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        RequestBody rbStatus =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), value);

        if (value.equals("1"))
            ServiceProvider.getInstance(this).acceptOrder(rbOrderId, rbRiderId, rbStatus);
        else
            ServiceProvider.getInstance(this).rejectOrder(rbOrderId, rbRiderId, rbStatus);


    }

    private void openDialog(String orderId) {
        DialogFragment dialog = ConfirmationDialogFragment.newInstance(orderId);
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), ConfirmationDialogFragment.class.getName());
    }


}
