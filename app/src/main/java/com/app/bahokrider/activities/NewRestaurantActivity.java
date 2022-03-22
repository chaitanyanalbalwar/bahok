package com.app.bahokrider.activities;

import android.Manifest;
import android.annotation.TargetApi;
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
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.DialogUtil;
import com.app.bahokrider.utils.NetworkUtil;
import com.app.bahokrider.utils.ValidationUtils;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class NewRestaurantActivity extends AppCompatActivity
        implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    private static final String TAG = NewRestaurantActivity.class.getSimpleName();

    TextView toolbar_title;
    ImageView iv_back;
    private CustomProgressDialog progressDialog;

    EditText etName, etEmail, etMobile, etPass, etLocation, etCPass, etDescription;
    TextInputLayout tvlLoc;
    Button btnSubmit;
    ImageView imgLogo;

    private Bitmap logoBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restrorant);
        initViews();
        addListener();

        initProgressBar();

        toolbar_title.setText(getString(R.string.add_new_restaurant));

    }

    // click listener for the buttons
    private void addListener() {
        btnSubmit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        imgLogo.setOnClickListener(this);
        tvlLoc.setOnClickListener(this);
    }

    //Initialize controls
    private void initViews() {
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPass = findViewById(R.id.etPass);
        etCPass = findViewById(R.id.etCPass);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        tvlLoc = findViewById(R.id.tvlLoc);
        imgLogo = findViewById(R.id.imgLogo);
        btnSubmit = findViewById(R.id.btnSubmit);
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
    public void onResponse(Object response, String requestTag) {
        hideProgressBar();

        if (requestTag.contains(APIs.REGISTER_RESTAURANT)) {
            BaseResponse result = (BaseResponse) response;
            openDialog();
        }

    }

    private void openDialog() {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(getString(R.string.thank_you));
        alertBuilder.setMessage(getString(R.string.register_success));
        alertBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();

    }

    @Override
    public void onError(String errorMessage) {
        hideProgressBar();
        ToastManager.getInstance().showLongToast(this, errorMessage);
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btnSubmit:
                submitButtonClick();
                break;
            case R.id.imgLogo:
                getImage();
                break;
            case R.id.tvlLoc:
                fetchLocation();
                break;
        }
    }

    private void fetchLocation() {

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


        logoBitmap = (Bitmap) data.getExtras().get("data");

        Glide.with(this)
                .asBitmap()
                .load(logoBitmap)
                .into(imgLogo); //>>not tested

    }

    // select image from gallery
    private void onSelectFromGalleryResult(Intent data) {

        Uri imageUri = data.getData();

        try {
            logoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Glide.with(this)
                    .asBitmap()
                    .load(logoBitmap)
                    .into(imgLogo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void submitButtonClick() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String descr = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String cPass = etCPass.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.required));
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.required));
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError(getString(R.string.required));
            etMobile.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidMobile(mobile)) {
            etMobile.setError(getString(R.string.required));
            etMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            etLocation.setError(getString(R.string.required));
            etLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            etPass.setError(getString(R.string.required));
            etPass.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cPass)) {
            etCPass.setError(getString(R.string.required));
            etCPass.requestFocus();
            return;
        }

        if (!pass.equals(cPass)) {
            etCPass.setError(getString(R.string.pass_not_match));
            etCPass.requestFocus();
            return;
        }

//        if (TextUtils.isEmpty(descr)) {
//            etDescription.setError(getString(R.string.required));
//            etDescription.requestFocus();
//            return;
//        }

        if (logoBitmap == null) {
            onError(getString(R.string.error_restaurant_image));
            return;
        }


        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }


        MultipartBody.Part pic = null;

        if (logoBitmap != null) {

            File file = new File(getCacheDir(), "image.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            logoBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            pic = MultipartBody.Part.createFormData("restaurant_logo", file.getName(), reqFile1);

        }


        RequestBody rbName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), name);
        RequestBody rbEmail =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), email);
        RequestBody rbMobile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), mobile);
        RequestBody rbLoc =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), location);
        RequestBody rbLat =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "");
        RequestBody rbLong =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "");
        RequestBody rbPass =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), pass);
        RequestBody rbDesc =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descr);

        showProgressBar();

        ServiceProvider.getInstance(this).registerRestaurant(pic, rbName, rbEmail, rbMobile, rbLoc, rbLat, rbLong, rbPass, rbDesc);

    }

}
