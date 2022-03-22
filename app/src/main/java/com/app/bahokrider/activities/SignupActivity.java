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
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bahokrider.CircularImageView;
import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BaseResponse;
import com.app.bahokrider.retrofit.APIs;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.DialogUtil;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class SignupActivity extends AppCompatActivity
        implements View.OnClickListener, IProgressBar, IServiceCallbacks {

    private static final String TAG = SignupActivity.class.getSimpleName();

    EditText emailEditText, etFname, etLname, mobileEditText, etAdhar, etLicense;
    Button signupButton;
    ImageView imgLicenseFront, imgLicenseBack, imgAdharFront, imgAdharBack;
    CircularImageView imgProfile;
    TextView tvBack;
    RelativeLayout rlImage;

    private int selectImage = -1;
    private String mobile = "";
    private Bitmap userBitmap;
    private Bitmap adharBitmapFront, adharBitmapBack;
    private Bitmap licenseBitmapFront, licenseBitmapBack;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initview();
        addListener();

        initProgressBar();

        mobile = getIntent().getStringExtra("mobile");

        mobileEditText.setText(mobile);
        if (!TextUtils.isEmpty(mobile))
            mobileEditText.setEnabled(false);

    }

    // click listener for the buttons
    private void addListener() {
        signupButton.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        rlImage.setOnClickListener(this);
        imgLicenseFront.setOnClickListener(this);
        imgLicenseBack.setOnClickListener(this);
        imgAdharFront.setOnClickListener(this);
        imgAdharBack.setOnClickListener(this);
    }

    //Initialize controls
    private void initview() {
        emailEditText = findViewById(R.id.etEmail);
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etAdhar = findViewById(R.id.etAdhar);
        etLicense = findViewById(R.id.etLicense);
        mobileEditText = findViewById(R.id.etMobile);
        signupButton = findViewById(R.id.btnSubmit);
        tvBack = findViewById(R.id.tvBack);
        imgLicenseFront = findViewById(R.id.imgLicenseFront);
        imgLicenseBack = findViewById(R.id.imgLicenseBack);
        imgAdharFront = findViewById(R.id.imgAdharFront);
        imgAdharBack = findViewById(R.id.imgAdharBack);
        rlImage = findViewById(R.id.rlImage);
        imgProfile = findViewById(R.id.imgProfile);
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

        if (requestTag.contains(APIs.REGISTER)) {
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
            case R.id.btnSubmit:
                submitButtonClick();
                break;
            case R.id.tvBack:
                onBackPressed();
                break;
            case R.id.rlImage:
                selectImage = 0;
                getImage();
                break;
            case R.id.imgLicenseFront:
                selectImage = 1;
                getImage();
                break;
            case R.id.imgLicenseBack:
                selectImage = 2;
                getImage();
                break;
            case R.id.imgAdharFront:
                selectImage = 3;
                getImage();
                break;
            case R.id.imgAdharBack:
                selectImage = 4;
                getImage();
                break;
        }
    }

    private void getImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
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

        if (selectImage == 0) {

            userBitmap = (Bitmap) data.getExtras().get("data");

            Glide.with(this)
                    .asBitmap()
                    .load(userBitmap)
                    .into(imgProfile); //>>not tested
        }

        else if (selectImage == 1) {

            licenseBitmapFront = (Bitmap) data.getExtras().get("data");

            Glide.with(this)
                    .asBitmap()
                    .load(licenseBitmapFront)
                    .into(imgLicenseFront); //>>not tested
        }

        else if (selectImage == 2) {

            licenseBitmapBack = (Bitmap) data.getExtras().get("data");

            Glide.with(this)
                    .asBitmap()
                    .load(licenseBitmapBack)
                    .into(imgLicenseBack); //>>not tested
        }

        else if (selectImage == 3) {

            adharBitmapFront = (Bitmap) data.getExtras().get("data");

            Glide.with(this)
                    .asBitmap()
                    .load(adharBitmapFront)
                    .into(imgAdharFront); //>>not tested
        }

        else if (selectImage == 4) {

            adharBitmapBack = (Bitmap) data.getExtras().get("data");

            Glide.with(this)
                    .asBitmap()
                    .load(adharBitmapBack)
                    .into(imgAdharBack); //>>not tested
        }
    }

    // select image from gallery
    private void onSelectFromGalleryResult(Intent data) {


        if (selectImage == 0) {

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

        } else if (selectImage == 1) {

            Uri imageUri = data.getData();

            try {
                licenseBitmapFront = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Glide.with(this)
                        .asBitmap()
                        .load(licenseBitmapFront)
                        .into(imgLicenseFront);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectImage == 2) {

            Uri imageUri = data.getData();

            try {
                licenseBitmapBack = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Glide.with(this)
                        .asBitmap()
                        .load(licenseBitmapBack)
                        .into(imgLicenseBack);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectImage == 3) {

            Uri imageUri = data.getData();

            try {
                adharBitmapFront = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Glide.with(this)
                        .asBitmap()
                        .load(adharBitmapFront)
                        .into(imgAdharFront);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectImage == 4) {

            Uri imageUri = data.getData();

            try {
                adharBitmapBack = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Glide.with(this)
                        .asBitmap()
                        .load(adharBitmapBack)
                        .into(imgAdharBack);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void submitButtonClick() {

        String fName = etFname.getText().toString().trim();
        String lName = etLname.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();
        String adhar = etAdhar.getText().toString().trim();
        String license = etLicense.getText().toString().trim();

        if (TextUtils.isEmpty(fName)) {
            etFname.setError(getString(R.string.required));
            etFname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lName)) {
            etLname.setError(getString(R.string.required));
            etLname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(adhar)) {
            etAdhar.setError(getString(R.string.required));
            etAdhar.requestFocus();
            return;
        }

        if (adhar.length() != 10) {
            etAdhar.setError(getString(R.string.error_adhar));
            etAdhar.requestFocus();
            return;
        }

        /*if (TextUtils.isEmpty(license)) {
            etLicense.setError(getString(R.string.required));
            etLicense.requestFocus();
            return;
        }*/

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.error_email));
            emailEditText.requestFocus();
            return;
        }

        if (mobile.length()!=11) {
            mobileEditText.setError(getString(R.string.error_mobile));
            mobileEditText.requestFocus();
            return;
        }

        /*if (licenseBitmapFront == null || licenseBitmapBack == null) {
            onError(getString(R.string.error_license_image));
            return;
        }*/

        if (adharBitmapFront == null || adharBitmapBack == null) {
            onError(getString(R.string.error_adhar_image));
            return;
        }

        MultipartBody.Part profile_pic = null;
        MultipartBody.Part license_pic_front = null;
        MultipartBody.Part license_pic_back = null;
        MultipartBody.Part adhar_pic_front = null;
        MultipartBody.Part adhar_pic_back = null;

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

        if (adharBitmapFront != null) {

            File file = new File(getCacheDir(), "adhar_image_front.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            adharBitmapFront.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            adhar_pic_front = MultipartBody.Part.createFormData("aadharImage", file.getName(), reqFile1);

        }

        if (adharBitmapBack != null) {

            File file = new File(getCacheDir(), "adhar_image_back.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            adharBitmapBack.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            adhar_pic_back = MultipartBody.Part.createFormData("aadharImageBack", file.getName(), reqFile1);

        }

        if (licenseBitmapFront != null) {

            File file = new File(getCacheDir(), "license_image_front.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            licenseBitmapFront.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            license_pic_front = MultipartBody.Part.createFormData("licenseImage", file.getName(), reqFile1);

        }

        if (licenseBitmapBack != null) {

            File file = new File(getCacheDir(), "license_image_back.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            licenseBitmapBack.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            license_pic_back = MultipartBody.Part.createFormData("licenseImageBack", file.getName(), reqFile1);

        }

        RequestBody rbFirstName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fName);
        RequestBody rbLastName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), lName);
        RequestBody rbEmail =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), email);
        RequestBody rbMobile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), mobile);
        RequestBody rbAdhar =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), adhar);
        RequestBody rbLicense =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), license);

        showProgressBar();

        ServiceProvider.getInstance(this).register(profile_pic, adhar_pic_front, adhar_pic_back, license_pic_front, license_pic_back,
                rbFirstName, rbLastName, rbEmail, rbMobile, rbAdhar, rbLicense);

    }

}
