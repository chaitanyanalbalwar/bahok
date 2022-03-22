package com.app.bahokrider.managers;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.app.bahokrider.R;
import com.app.bahokrider.retrofit.APIs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class GlideManager {

    public static void setGlideImage(Context context, String imageUrl, ImageView imageView) {

        if (!TextUtils.isEmpty(imageUrl)) {
            if (!imageUrl.startsWith(APIs.BASE_URL)) {
                imageUrl = APIs.BASE_URL + APIs.PATH + imageUrl;
            }
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageView);
    }

    public static void setUserImage(Context context, String imageUrl, ImageView imageView) {

        if (!TextUtils.isEmpty(imageUrl)) {
            if (!imageUrl.startsWith(APIs.BASE_URL)) {
                imageUrl = APIs.BASE_URL + APIs.PATH + imageUrl;
            }
        }

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.msg_default_profile)
                        .error(R.drawable.msg_default_profile)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(imageView);
    }


}
