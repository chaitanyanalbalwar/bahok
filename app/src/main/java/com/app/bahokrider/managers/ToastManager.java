package com.app.bahokrider.managers;

import android.content.Context;
import android.widget.Toast;

public class ToastManager {

    private static ToastManager ourInstance = null;

    public static synchronized ToastManager getInstance() {
        if (ourInstance == null)
            ourInstance = new ToastManager();
        return ourInstance;
    }

    public void showLongToast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
