package com.app.bahokrider.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bahokrider.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class NewOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mPlayer;
    private ImageView imgGif;
    private TextView tvTimer;
    private Button btnView;
    private Button btnCancel;

    private int counter = 180;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        initView();
        initListeners();

        mPlayer = MediaPlayer.create(this, R.raw.new_order);
        mPlayer.setLooping(true);
        mPlayer.start();

        tvTimer.setText(String.valueOf(counter));
        imgGif.setImageDrawable(null);

        Glide.with(this)
                .asGif()
                .load(R.drawable.timer) //or url
                .into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(@NonNull GifDrawable resource,
                                                @Nullable Transition<? super GifDrawable> transition) {

                        resource.start();
//                        resource.setLoopCount(1);
                        imgGif.setImageDrawable(resource);

                        startTimer();
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            imgGif.setImageDrawable(null);
            stopPlayer();
        } catch (Exception ignored) {

        }
    }

    private void initListeners() {
        btnView.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void startTimer() {

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(counter));
                counter--;
            }

            @Override
            public void onFinish() {
                stopPlayer();
                finish();
            }
        }.start();
    }

    private void stopPlayer() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.setLooping(true);
            mPlayer.release();
        }
    }

    private void initView() {
        tvTimer = findViewById(R.id.tvTimer);
        imgGif = findViewById(R.id.imgGif);
        btnView = findViewById(R.id.btnView);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void onView() {

        if (countDownTimer != null)
            countDownTimer.cancel();
        stopPlayer();

        String orderId = getIntent().getStringExtra("orderId");

        finish();
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("orderId", orderId);
        startActivity(intent);

    }

    private void onCancel() {
        if (countDownTimer != null)
            countDownTimer.cancel();
        stopPlayer();
        finish();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnView)
            onView();
        else if (v.getId() == R.id.btnCancel)
            onCancel();
    }
}
