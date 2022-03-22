package com.app.bahokrider.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.bahokrider.fragments.OrderHistoryFragment;
import com.app.bahokrider.fragments.HomeFragment;
import com.app.bahokrider.fragments.MoreFragment;
import com.app.bahokrider.R;
import com.app.bahokrider.fragments.StartDaySelfiDialogFragment;
import com.app.bahokrider.managers.SharedPreferencesManager;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.retrofit.IServiceCallbacks;
import com.app.bahokrider.retrofit.ServiceProvider;
import com.app.bahokrider.utils.AppConstants;
import com.app.bahokrider.utils.NetworkUtil;
import com.app.bahokrider.utils.ValidationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainScreenActivity extends AppCompatActivity implements IServiceCallbacks {

    int mSelectedFragmentPosition = 0;
    Fragment mFragment;

    Handler handler = new Handler();

    String deviceToken;

    Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            SmartLocation.with(MainScreenActivity.this).location().start(updatedListener);
        }
    };

    OnLocationUpdatedListener updatedListener = new OnLocationUpdatedListener() {
        @Override
        public void onLocationUpdated(Location location) {

            if (location != null) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();
                postUserLocation(lat, lng);
            }

            handler.postDelayed(locationRunnable, 15 * 1000);
        }
    };

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_orders:
                    if (mFragment instanceof OrderHistoryFragment)
                        return true;
                    mFragment = OrderHistoryFragment.newInstance(null);
                    loadFragment(mFragment);
                    mSelectedFragmentPosition = 0;
                    return true;

                /*case R.id.nav_new_orders:
                    if (mFragment instanceof AllNewOrderFragment)
                        return true;
                    mFragment = AllNewOrderFragment.newInstance(null);
                    loadFragment(mFragment);
                    mSelectedFragmentPosition = 1;
                    return true;*/

                case R.id.nav_home:
                    if (mFragment instanceof HomeFragment)
                        return true;
                    mFragment = HomeFragment.newInstance(null);
                    loadFragment(mFragment);
                    mSelectedFragmentPosition = 2;
                    return true;
                case R.id.nav_wallet:
                    startActivity(new Intent(MainScreenActivity.this, WalletActivity.class));
                    return false;
                case R.id.nav_more:
                    if (mFragment instanceof MoreFragment)
                        return true;
                    mFragment = MoreFragment.newInstance(null);
                    loadFragment(mFragment);
                    mSelectedFragmentPosition = 3;
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        navigation.getMenu().getItem(1).getActionView().performClick();
        navigation.setSelectedItemId(R.id.nav_home);
        loadFragment(new HomeFragment());

    //    String deviceToken = FirebaseInstanceId.getInstance().getToken();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isComplete()){
                    String token = task.getResult();
                    deviceToken = task.getResult();

                }
            }
        });

        System.out.println("deviceToken:" + deviceToken);

        if (getIntent().getExtras() != null) {
            String orderId = getIntent().getStringExtra("orderId");
            String type = getIntent().getStringExtra("type");

            Intent intent = new Intent(getApplicationContext(), NewOrderActivity.class);
            intent.putExtra("orderId", orderId);
            intent.putExtra("type", type);
            startActivity(intent);
        }

        checkSelfiUploaded();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment != null)
            fragment.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionAndTracking();
            }
        }
    }


    @Override
    public void onResponse(Object response, String requestTag) {
        System.out.println("location updated");
    }

    @Override
    public void onError(String errorMessage) {
        System.out.println("failed to location updated");
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.navigation);

        switch (mBottomNavigationView.getSelectedItemId()) {
            // case R.id.login:
            case R.id.nav_home:
                super.onBackPressed();
                finish();
                break;
            default:
                mBottomNavigationView.setSelectedItemId(R.id.nav_home);
                break;
        }

    }

    public void updateWalletAmount(String amount) {

        try {
            BottomNavigationView navigation = findViewById(R.id.navigation);
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2); //set this to 0, 1, 2, or 3.. accordingly which menu item of the bottom bar you want to show badge
            View notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge_view, menuView, false);
            TextView tv = notificationBadge.findViewById(R.id.tvBadge);
            tv.setText(amount);
            itemView.addView(notificationBadge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void checkSelfiUploaded() {

        String lastUploadDateString = SharedPreferencesManager.getInstance()
                .getSomeStringValue(this, AppConstants.LAST_UPLOAD);

        String currentDateString = ValidationUtils.dateToString(new Date());

        if (TextUtils.isEmpty(lastUploadDateString) || TextUtils.isEmpty(currentDateString)) {
            openSelfiDialogFragment();
            return;
        }

        if (!currentDateString.equalsIgnoreCase(lastUploadDateString)) {
            openSelfiDialogFragment();
        }

    }

    private void openSelfiDialogFragment() {

        DialogFragment fragment = new StartDaySelfiDialogFragment();
        fragment.setCancelable(false);
        fragment.show(getSupportFragmentManager(), "");

    }

    public void permissionAndTracking() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }

    }

    public void startTracking() {

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        SmartLocation.with(MainScreenActivity.this).location().start(updatedListener);
    }

    public void stopTracking() {
        try {

            SmartLocation.with(MainScreenActivity.this).location().stop();
            handler.removeCallbacks(locationRunnable);

        } catch (Exception e) {

        }
    }

    private void postUserLocation(double lat, double lng) {
        System.out.println("lat:" + lat + " long:" + lng);

        Intent intent = new Intent("location");    //action: "msg"
        intent.setPackage(getPackageName());
        intent.putExtra("lat", String.valueOf(lat));
        intent.putExtra("lng", String.valueOf(lng));
        getApplicationContext().sendBroadcast(intent);

        String orderId = SharedPreferencesManager.getInstance().getSomeStringValue(this, AppConstants.USER_ID);

        RequestBody rbOrderId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), orderId);

        RequestBody rbLat =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), String.valueOf(lat));

        RequestBody rbLong =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), String.valueOf(lng));

        ServiceProvider.getInstance(this).postCurrentLocation(rbOrderId, rbLat, rbLong);

    }


}
