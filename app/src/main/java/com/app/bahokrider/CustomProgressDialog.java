package com.app.bahokrider;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import android.widget.ProgressBar;


public class CustomProgressDialog extends ProgressDialog {

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_loading);

        ProgressBar mProgressBar = findViewById(R.id.progressBar);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(mProgressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(), R.color.colorAccent));
            mProgressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(),
                    R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }

    }

}
