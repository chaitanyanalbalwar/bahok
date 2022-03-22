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

import java.util.Objects;


public class NoInternetDialogFragment extends DialogFragment
        implements View.OnClickListener {

    private Context mContext;

    private Button btnExit;


    public NoInternetDialogFragment() {
    }

    public static NoInternetDialogFragment newInstance() {
        return new NoInternetDialogFragment();
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
        return inflater.inflate(R.layout.fragment_no_internet, container, false);
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

        if (v.getId() == R.id.btnExit) {
            Objects.requireNonNull(getActivity()).finish();
            System.exit(0);
        }

    }

    private void initViews(View view) {
        btnExit = view.findViewById(R.id.btnExit);
    }

    private void initListeners() {
        btnExit.setOnClickListener(this);
    }

}
