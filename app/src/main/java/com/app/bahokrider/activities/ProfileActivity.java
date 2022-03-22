package com.app.bahokrider.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bahokrider.CircularImageView;
import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.GlideManager;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.pojos.User;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.DialogUtil;
import com.app.bahokrider.utils.NetworkUtil;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends AppCompatActivity
        implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    EditText emailEditText, etFname, etLname, mobileEditText, etAdhar, etLicense;
    Button btnSave;
    TextView toolbar_title, tvAdharLink, tvLicenseLink;
    CircularImageView imgProfile;
    ImageView iv_back, iv_toolbar_menu;

    String userId;
    String adharLink;
    String licenseLink;
    private Bitmap userBitmap;

    private CustomProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initview();
        addListener();
        initProgressBar();

        toolbar_title.setText(getString(R.string.my_profile));

        userId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        getProfile();
    }

    // click listner for the buttons
    private void addListener() {
        btnSave.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        tvLicenseLink.setOnClickListener(this);
        tvAdharLink.setOnClickListener(this);
    }

    //Initialize controls
    private void initview() {
        emailEditText = findViewById(R.id.etEmail);
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        mobileEditText = findViewById(R.id.etMobile);
        btnSave = findViewById(R.id.btnSave);
        etAdhar = findViewById(R.id.etAdhar);
        etLicense = findViewById(R.id.etLicense);
        imgProfile = findViewById(R.id.imgProfile);
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);
        iv_toolbar_menu = findViewById(R.id.iv_toolbar_menu);
        tvAdharLink = findViewById(R.id.tvAdharLink);
        tvLicenseLink = findViewById(R.id.tvLicenseLink);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tvAdharLink:
                viewAdharLink();
                break;
            case R.id.tvLicenseLink:
                viewLicenseLink();
                break;
            case R.id.btnSave:
                saveButtonClick();
                break;
            case R.id.imgProfile:
                getImage();
                break;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101)
                onSelectFromGalleryResult(data);
            else if (requestCode == 100)
                onCaptureImageResult(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    DialogUtil.openPermissionDialog(this, getString(R.string.storage_permission_message));
                }
                break;

            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraIntent();
                } else {
                    DialogUtil.openPermissionDialog(this, getString(R.string.camera_permission_message));
                }
                break;
        }
    }

    @Override
    public void onResponse(Object response, String requestTag) {
        hideProgressBar();

        if (requestTag.contains(APIs.GET_PROFILE)) {
            BaseResponse result = (BaseResponse) response;
            User uObj = result.getUserData();

            bindData(uObj);
        } else if (requestTag.contains(APIs.UPDATE_PROFILE)) {
            onError(getString(R.string.my_profile_updated));
        }

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(this, errorMessage);
    }

    private void bindData(User uObj) {

        GlideManager.setUserImage(this, uObj.getUserImage(), imgProfile);

        etFname.setText(uObj.getFname());
        etLname.setText(uObj.getLname());
        emailEditText.setText(uObj.getEmail());
        mobileEditText.setText(uObj.getMobileno());
        etAdhar.setText(uObj.getAadharno());
        etLicense.setText(uObj.getLicenseno());

        adharLink = uObj.getAadharImage();
        licenseLink = uObj.getLicenseImage();

    }

    private void getProfile() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        showProgressBar();

        ServiceProvider.getInstance(this).getProfile(userId);

    }

    private void getImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    // camera intent
    private void cameraIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                return;
            }

        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    // gallery intent
    private void galleryIntent() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
                return;
            }

        }


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), 101);
    }

    // capture image from camra
    private void onCaptureImageResult(Intent data) {


        userBitmap = (Bitmap) data.getExtras().get("data");

        Glide.with(this)
                .asBitmap()
                .load(userBitmap)
                .into(imgProfile);

    }

    // select image from gallery
    private void onSelectFromGalleryResult(Intent data) {

        Uri imageUri = data.getData();

        try {
            userBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Glide.with(this)
                    .asBitmap()
                    .load(userBitmap)
                    .into(imgProfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveButtonClick() {

        String fName = etFname.getText().toString().trim();
        String lName = etLname.getText().toString().trim();

        if (TextUtils.isEmpty(fName)) {
            etFname.setError("Required");
            etFname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lName)) {
            etLname.setError("Required");
            etLname.requestFocus();
            return;
        }

        MultipartBody.Part profile_pic = null;

        if (userBitmap != null) {

            File file = new File(getCacheDir(), "user_image.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            userBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            profile_pic = MultipartBody.Part.createFormData("userImage", file.getName(), reqFile1);

        }


        RequestBody rbUserID =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);
        RequestBody rbFirstName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fName);
        RequestBody rbLastName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), lName);

        showProgressBar();

        ServiceProvider.getInstance(this).updateProfile(profile_pic, rbUserID, rbFirstName, rbLastName);


    }

    private void viewLicenseLink() {

        if (TextUtils.isEmpty(licenseLink)) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.no_license));
            return;
        }

        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("imageUrl", licenseLink);
        startActivity(intent);

    }

    private void viewAdharLink() {

        if (TextUtils.isEmpty(adharLink)) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.no_adhar));
            return;
        }

        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra("imageUrl", adharLink);
        startActivity(intent);

    }


}
