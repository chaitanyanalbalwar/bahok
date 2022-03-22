package com.app.bahokrider.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.app.bahokrider.activities.LoginActivity;
import com.app.bahokrider.activities.MainScreenActivity;
import com.app.bahokrider.managers.SharedPreferencesManager;


public class LogoutDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private Context mContext;

    private Button btnLogout;
    private Button btnCancel;


    public LogoutDialogFragment() {
    }

    public static LogoutDialogFragment newInstance() {
        return new LogoutDialogFragment();
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
        return inflater.inflate(R.layout.fragment_logout_confirmation, container, false);
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

            case R.id.btnLogout:
                onLogClicked();
                break;

            case R.id.btnCancel:
                dismiss();
                break;

        }

    }

    private void onLogClicked() {
        MainScreenActivity activity = (MainScreenActivity) getActivity();
        if (activity != null)
            activity.stopTracking();

        SharedPreferencesManager.getInstance().clearAll(getActivity());
        Intent intent1 = new Intent(getActivity(), LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }

    private void initViews(View view) {
        btnLogout = view.findViewById(R.id.btnLogout);
        btnCancel = view.findViewById(R.id.btnCancel);
    }

    private void initListeners() {
        btnLogout.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
}
