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

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.interfaces.IRecyclerViewListener;
import com.app.bahokrider.managers.GlideManager;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.pojos.BankObj;
import com.app.bahokrider.pojos.BaseResponse;
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
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

//import io.flutter.plugin.common.MethodChannel;

public class BankDetailsActivity extends AppCompatActivity implements IProgressBar, IRecyclerViewListener, IServiceCallbacks {

    TextView toolbar_title;
    ImageView iv_back;

    Button btnSubmit;
    EditText etAccount;
    EditText etAccName;
    EditText etBankName;
    EditText etBranchName;
    EditText etIFSC;

    ImageView imgCheckbook;

    Bitmap checkImage;

    private CustomProgressDialog progressDialog;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        initViews();
        addListener();
        initProgressBar();

        toolbar_title.setText(getString(R.string.my_bank_details));
        userId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        getBankDetails();

    }

    //Initialize controls
    private void initViews() {
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);

        btnSubmit = findViewById(R.id.btnSubmit);
        etAccount = findViewById(R.id.etAccount);
        etAccName = findViewById(R.id.etAccName);
        etBankName = findViewById(R.id.etBankName);
        etBranchName = findViewById(R.id.etBranchName);
        etIFSC = findViewById(R.id.etIFSC);
        imgCheckbook = findViewById(R.id.imgCheckbook);

    }

    //Initialize controls
    private void addListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitPress();
            }
        });
        imgCheckbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
    }

    @Override
    public void onItemClickListener(View view, int position) {

    }

    @Override
    public void onResponse(Object response, String requestTag) {

        hideProgressBar();

        if (requestTag.contains(APIs.GET_BANK_DETAILS)) {
            BaseResponse result = (BaseResponse) response;

            bindData(result.getBankList());

        } else if (requestTag.contains(APIs.POST_BANK_DETAILS)) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.post_bank_success));

        }

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

    private void getBankDetails() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        showProgressBar();

        ServiceProvider.getInstance(this).getBankDetails(userId);

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

    private void onSubmitPress() {

        String accHolder = etAccName.getText().toString().trim();
        String accNumber = etAccount.getText().toString().trim();
        String bankName = etBankName.getText().toString().trim();
        String branchName = etBranchName.getText().toString().trim();
        String ifsc = etIFSC.getText().toString().trim();

        if (TextUtils.isEmpty(accHolder)) {
            etAccName.setError(getString(R.string.required));
            etAccName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(bankName)) {
            etBankName.setError(getString(R.string.required));
            etBankName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(branchName)) {
            etBranchName.setError(getString(R.string.required));
            etBranchName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(accNumber)) {
            etAccount.setError(getString(R.string.required));
            etAccount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ifsc)) {
            etIFSC.setError(getString(R.string.required));
            etIFSC.requestFocus();
            return;
        }

        showProgressBar();

        RequestBody rbRiderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), userId);

        RequestBody rbAName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), accHolder);

        RequestBody rbBankName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), bankName);

        RequestBody rbBName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), branchName);

        RequestBody rbANumber =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), accNumber);

        RequestBody rbIFSC =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), ifsc);

        MultipartBody.Part cheque_pic = null;

        if (checkImage != null) {

            File file = new File(getCacheDir(), "cheque_image.jpeg");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            checkImage.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
            cheque_pic = MultipartBody.Part.createFormData("cheque_image", file.getName(), reqFile1);

        }


        ServiceProvider.getInstance(this).postBankDetails(rbRiderId, rbAName, rbBankName, rbBName, rbANumber, rbIFSC, cheque_pic);
    }

    private void bindData(List<BankObj> bankList) {

        if (bankList.size() <= 0)
            return;

        BankObj bObj = bankList.get(0);

        etAccName.setText(bObj.getAccName());
        etBankName.setText(bObj.getBankName());
        etBranchName.setText(bObj.getBranchName());
        etAccount.setText(bObj.getAccNumber());
        etIFSC.setText(bObj.getIfsc());

        if (!TextUtils.isEmpty(bObj.getChequeImage()))
            GlideManager.setGlideImage(this, bObj.getChequeImage(), imgCheckbook);

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

        checkImage = (Bitmap) data.getExtras().get("data");

        Glide.with(this)
                .asBitmap()
                .load(checkImage)
                .into(imgCheckbook);

    }

    // select image from gallery
    private void onSelectFromGalleryResult(Intent data) {

        Uri imageUri = data.getData();

        try {
            checkImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Glide.with(this)
                    .asBitmap()
                    .load(checkImage)
                    .into(imgCheckbook);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}








