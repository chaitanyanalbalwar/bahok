package com.app.bahokrider.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.bahokrider.R;
import com.app.bahokrider.activities.WalletActivity;


public class UpdateWalletDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private Context mContext;

    private Button btnProceed;
    private EditText etAmount;

    public UpdateWalletDialogFragment() {
    }

    public static UpdateWalletDialogFragment newInstance() {
        return new UpdateWalletDialogFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_update_wallet, container, false);
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

        if (v.getId() == R.id.btnProceed) {
            DeliverOrder();
        }

    }

    private void DeliverOrder() {

        String amount = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            etAmount.setError(getString(R.string.required));
            return;
        }

        float amountFloat = Float.parseFloat(amount);

        if (amountFloat < 0) {
            etAmount.setError(getString(R.string.amount_greater_than_zero));
            return;
        }


        WalletActivity activity = (WalletActivity) getActivity();
        if (activity != null)
            activity.callToPaymentGateway(amountFloat);

        dismiss();

    }

    private void initViews(View view) {
        btnProceed = view.findViewById(R.id.btnProceed);
        etAmount = view.findViewById(R.id.etAmount);
    }

    private void initListeners() {
        btnProceed.setOnClickListener(this);
    }

}
