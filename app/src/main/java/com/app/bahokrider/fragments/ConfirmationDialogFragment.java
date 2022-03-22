package com.app.bahokrider.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.bahokrider.R;
import com.app.bahokrider.activities.OrderDetailsActivity;


public class ConfirmationDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private Context mContext;

    private Button btnCancel, btnReject;

    private String orderId;

    public ConfirmationDialogFragment() {
    }

    public static ConfirmationDialogFragment newInstance(String orderId) {
        ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog.getWindow() == null)
            return;

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            orderId = bundle.getString("orderId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initViews(view);
        initListeners();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnReject:
                rejectOrder();
                break;

            case R.id.btnCancel:
                dismiss();
                break;

        }

    }

    private void rejectOrder() {

        OrderListFragment fragment = (OrderListFragment) getParentFragment();
        if (fragment != null) {
            fragment.acceptRejectOrder("0", orderId);
            dismiss();
        } else {
            OrderDetailsActivity activity = (OrderDetailsActivity) getActivity();
            activity.onRejectPressed();
            dismiss();
        }

    }

    private void initViews(View view) {
        btnReject = view.findViewById(R.id.btnReject);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void initListeners() {
        btnReject.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

}
