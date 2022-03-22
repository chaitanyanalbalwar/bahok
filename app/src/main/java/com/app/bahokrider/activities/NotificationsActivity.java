package com.app.bahokrider.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.adapters.NotificationsAdapter;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.Notification;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;


public class NotificationsActivity extends AppCompatActivity implements IProgressBar, IRecyclerViewListener, IServiceCallbacks {

    TextView toolbar_title;
    ImageView iv_back;

    private CustomProgressDialog progressDialog;
    private RecyclerView rvList;
    private TextView tvNoData;

    private NotificationsAdapter adapter;
    private List<Notification> list;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        addListener();
        initProgressBar();

        toolbar_title.setText(getString(R.string.notifications));
        userId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        setAdapter();
        getNotifications();

    }

    //Initialize controls
    private void initViews() {
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);

        rvList = findViewById(R.id.rvList);
        tvNoData = findViewById(R.id.tvNoData);
    }

    private void setAdapter() {

        if (list == null)
            list = new ArrayList<>();
        adapter = new NotificationsAdapter(this, list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvList.setAdapter(adapter);
    }

    //Initialize controls
    private void addListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onItemClickListener(View view, int position) {

        String riderId = list.get(position).getOrderId();

        if (TextUtils.isEmpty(riderId))
            return;

        int riderIdInt = Integer.parseInt(riderId);

        if (riderIdInt>0) {
            startActivity(new Intent(this, OrderDetailsActivity.class)
                    .putExtra("orderId", list.get(position).getOrderId()));
        }

    }

    @Override
    public void onResponse(Object response, String requestTag) {

        hideProgressBar();

        if (requestTag.contains(APIs.NOTIFICATIONS)) {
            BaseResponse result = (BaseResponse) response;

            bindData(result.getNotiList());

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

    private void getNotifications() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        showProgressBar();

        ServiceProvider.getInstance(this).getNotifications(userId);

    }

    private void bindData(List<Notification> transList) {

        list.clear();
        if (transList!=null){
            list.addAll(transList);
            adapter.notifyDataSetChanged();
        }else {
            tvNoData.setVisibility(View.VISIBLE);
        }

        /*if (transList.size() == 0)
            tvNoData.setVisibility(View.VISIBLE);*/
    }


}








