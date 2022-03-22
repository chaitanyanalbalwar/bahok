package com.app.bahokrider.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bahokrider.CircularImageView;
import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.DialogUtil;
import com.app.bahokrider.utils.ValidationUtils;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class StartDaySelfiDialogFragment extends DialogFragment
        implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    private Context mContext;
    private CircularImageView imgSelfi;
    private RelativeLayout rlCamera;
    private TextView tvName;

    private CustomProgressDialog progressDialog;

    private Button btnUpload;
    private Bitmap bitmap;


    public StartDaySelfiDialogFragment() {
    }

    public static StartDaySelfiDialogFragment newInstance() {
        return new StartDaySelfiDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog.getWindow() == null)
            return;

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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
        return inflater.inflate(R.layout.fragment_selfi_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        initViews(view);
        initListeners();
        initProgressBar();

        String fName = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.FNAME);

        String lName = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.FNAME);

        tvName.setText("Hi " + fName + " " + lName);

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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlCamera:
            case R.id.imgSelfi:
                cameraIntent();
                break;

            case R.id.btnUpload:
                uploadClick();
                break;

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    DialogUtil.openPermissionDialog(getActivity(), getString(R.string.camera_permission_message));
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100)
                onCaptureImageResult(data);
        }
    }

    // camera intent
    private void cameraIntent() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }


    // capture image from camra
    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");

        Glide.with(mContext)
                .asBitmap()
                .load(bitmap)
                .into(imgSelfi); //>>not tested

    }

    private void initViews(View view) {
        rlCamera = view.findViewById(R.id.rlCamera);
        imgSelfi = view.findViewById(R.id.imgSelfi);
        btnUpload = view.findViewById(R.id.btnUpload);
        tvName = view.findViewById(R.id.tvName);
    }

    private void initListeners() {
        rlCamera.setOnClickListener(this);
        imgSelfi.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    private void uploadClick() {

        if (bitmap == null) {
            onError(getString(R.string.error_capture_image));
            return;
        }

        MultipartBody.Part profile_pic = null;

        File file = new File(mContext.getCacheDir(), "user_image.jpeg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();

        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody reqFile1 = RequestBody.create(MediaType.parse("image/*"), file);
        profile_pic = MultipartBody.Part.createFormData("day_selfie", file.getName(), reqFile1);

        String userId = SharedPreferencesManager
                .getInstance()
                .getSomeStringValue(mContext, AppConstants.USER_ID);

        RequestBody rbUserID =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        showProgressBar();

        ServiceProvider.getInstance(this).uploadSelfi(profile_pic, rbUserID);


    }

    @Override
    public void onResponse(Object response, String requestTag) {

        hideProgressBar();

        if (requestTag.contains(APIs.UPDATE_SELFI)) {
            BaseResponse result = (BaseResponse) response;
            onError(getString(R.string.sefi_success));

            SharedPreferencesManager.getInstance()
                    .setSomeStringValue(mContext, AppConstants.LAST_UPLOAD,
                            ValidationUtils.dateToString(new Date()));
            dismiss();
        }
    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(mContext, errorMessage);
    }
}
