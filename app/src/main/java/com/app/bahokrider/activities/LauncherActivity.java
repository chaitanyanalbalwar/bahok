package com.app.bahokrider.activities;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.app.bahokrider.R;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.utils.ActivityNavigator;
import com.app.bahokrider.utils.AppConstants;

public class LauncherActivity extends AppCompatActivity {

    ImageView iv_logo;
    String userId ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        initView();

        userId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(userId)) {
                    ActivityNavigator.launchWelcomeActivity(LauncherActivity.this);
                } else {
                    ActivityNavigator.launchMainScreenActivity(LauncherActivity.this);
                }
            }

        }, 3 * 1000);
    }

    //Initialize controls
    private void initView() {
        iv_logo = findViewById(R.id.iv_logo);
    }


}

