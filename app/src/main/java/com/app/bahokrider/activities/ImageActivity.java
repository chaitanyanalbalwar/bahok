package com.app.bahokrider.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.app.bahokrider.R;
import com.app.bahokrider.managers.GlideManager;
import com.app.bahokrider.retrofit.APIs;

public class ImageActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imgClose, imgDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        initView();
        initListener();

        String imageUrl = getIntent().getStringExtra("imageUrl");

        GlideManager.setGlideImage(this, APIs.BASE_URL + APIs.PATH + imageUrl, imgDoc);

    }

    //Initialize controls
    private void initView() {
        imgClose = findViewById(R.id.imgClose);
        imgDoc = findViewById(R.id.imgDoc);
    }

    private void initListener() {
        imgClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}

