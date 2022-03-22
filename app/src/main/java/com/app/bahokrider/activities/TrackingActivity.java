package com.app.bahokrider.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IPathFinderListener;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.utils.PathFinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback, IProgressBar {

    private GoogleMap mMap;
    private ImageView imgBack;

    private Marker userMarker;
    private LatLng oldLocation;

    private CustomProgressDialog progressDialog;
    private boolean onStart = false;


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!onStart)
                return;

            String action = intent.getAction();
            if ("location".equals(action)) {
                String lat = intent.getStringExtra("lat");
                String lng = intent.getStringExtra("lng");

                if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {
                    ToastManager.getInstance().showLongToast(context, "So sorry! Enable to place your route. Please try again later.");
                    return;
                }

                if (mMap == null) {
                    //ToastManager.getInstance().showLongToast(context, "So sorry! Map is not ready yet. Please try again later.");
                    return;
                }

                System.out.println("mBroadcastReceiver:" + lat + " long: " + lng);

                if (userMarker == null) {
                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    MarkerOptions options = new MarkerOptions()
                            .position(latLng);
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.rider_marker);
                    options.icon(icon);
                    options.title("You are here");
                    options.anchor(0.5f, 0.907f);
                    options.flat(true);
                    userMarker = mMap.addMarker(options);
                    oldLocation = latLng;
                    plotTrackPoint();
                    loadRouteRoute(latLng);

                } else {
                    LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    if (oldLocation != null) {
                        float bearing = bearingBetweenLocations(latLng);
                        userMarker.setRotation(bearing);
                    }
                    userMarker.setPosition(latLng);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }

            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        initViews();
        initListeners();

        initProgressBar();

        ToastManager.getInstance().showLongToast(this, "Please wait for while... initialising google map.");

        initGoogleMap();

    }

    private void initListeners() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
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
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMaxZoomPreference(16);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            onStart = true;
            IntentFilter filter = new IntentFilter("location");
            registerReceiver(mBroadcastReceiver, filter);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {

        try {
//            progressDialog = null;
            onStart = false;
            unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        super.onPause();
    }


    private void initGoogleMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

            try {
                if (mapFragment.getView() == null)
                    return;
                View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 30, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void plotTrackPoint() {

        if (getIntent().getExtras() == null)
            return;

        String track = getIntent().getStringExtra("track");
        String lant = getIntent().getStringExtra("lant");
        String longd = getIntent().getStringExtra("long");
        String name = getIntent().getStringExtra("name");

        if (TextUtils.isEmpty(lant) || TextUtils.isEmpty(longd)) {
            ToastManager.getInstance().showLongToast(this, "So sorry! unable to plat your marker.");
            return;
        }

        final LatLng latLng = new LatLng(Double.parseDouble(lant), Double.parseDouble(longd));
        MarkerOptions options = new MarkerOptions()
                .position(latLng);
        options.title(name);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(track.equals("hotel") ? R.drawable.resto_marker : R.drawable.user_marker);
        options.icon(icon);
        options.anchor(0.5f, 0.907f);
        mMap.addMarker(options);

    }

    private void loadRouteRoute(LatLng latLng) {

        if (getIntent().getExtras() == null) {
            ToastManager.getInstance().showLongToast(this, "So sorry! Enable to place your route. Please try again later.");
            return;
        }

        ToastManager.getInstance().showLongToast(this, "Finding best path, wait for a while...");

        String lant = getIntent().getStringExtra("lant");
        String longd = getIntent().getStringExtra("long");

        final LatLng source = new LatLng(Double.parseDouble(lant), Double.parseDouble(longd));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

//        showProgressBar();

        new PathFinder(source, latLng, getString(R.string.map_key), new IPathFinderListener() {
            @Override
            public void onPath(PolylineOptions polyLine) {
                hideProgressBar();
                mMap.addPolyline(polyLine);
            }

            @Override
            public void onError() {
                hideProgressBar();
                ToastManager.getInstance().showLongToast(TrackingActivity.this, "So sorry! Enable to place your route. Please try again later.");
            }
        }).execute();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private float bearingBetweenLocations(LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = oldLocation.latitude * PI / 180;
        double long1 = oldLocation.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return (float) brng;
    }


}
