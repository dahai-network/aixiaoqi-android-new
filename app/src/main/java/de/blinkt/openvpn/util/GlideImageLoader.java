package de.blinkt.openvpn.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

import cn.com.aixiaoqi.R;

/**
 * Created by quantan.liu on 2017/3/24.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object url, ImageView imageView) {
        Glide.with(context).load(url)
                .crossFade(1000)
                .into(imageView);
    }
}
