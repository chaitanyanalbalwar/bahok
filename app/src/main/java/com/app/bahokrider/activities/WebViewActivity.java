package com.app.bahokrider.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.bahokrider.CustomProgressDialog;
import com.app.bahokrider.R;
import com.app.bahokrider.interfaces.IProgressBar;
import com.app.bahokrider.managers.ToastManager;
import com.app.bahokrider.utils.AppWebViewClients;
import com.app.bahokrider.utils.NetworkUtil;

public class WebViewActivity extends AppCompatActivity implements IProgressBar {

    TextView toolbar_title;
    ImageView iv_back;

    private WebView webView;
    private ProgressBar progressBar;

    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        initViews();
        addListener();

        if (getIntent().getExtras() == null)
            onBackPressed();

        String type = getIntent().getStringExtra("type");
        String link = getIntent().getStringExtra("link");

        if (type.equals("aboutus"))
            toolbar_title.setText(getString(R.string.about_us));
        else if (type.equals("privacy"))
            toolbar_title.setText(getString(R.string.privacy_policy));
        else if (type.equals("contact"))
            toolbar_title.setText(getString(R.string.contact_us));
        else
            toolbar_title.setText(getString(R.string.terms_conditions));

        int status = NetworkUtil.getConnectivityStatusString(this);
        if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            ToastManager.getInstance().showLongToast(this, getString(R.string.dialog_no_internet));
            return;
        }

        setWebView(link);

    }

    //Initialize controls
    private void initViews() {
        toolbar_title = findViewById(R.id.toolbar_title);
        iv_back = findViewById(R.id.iv_back);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
    }

    //Initialize controls
    private void addListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(String URL) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new AppWebViewClients(progressBar));
        webView.loadUrl(URL);
    }


}

