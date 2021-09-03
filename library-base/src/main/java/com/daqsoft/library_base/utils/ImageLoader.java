package com.daqsoft.library_base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

/**
 * @author zp
 * @package name：com.daqsoft.library_base.utils
 * @date 16/7/2021 上午 10:04
 * @describe
 */
public class ImageLoader implements XPopupImageLoader {

    int unit10M = 10 * 1024 * 1024;

    private RequestOptions buildOptions() {
        return new RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    @Override
    public void loadImage(final int position, @NonNull final Object url, @NonNull final ImageView imageView) {
        //如果你确定你的图片没有超级大的，直接这样写就行
//            Glide.with(imageView).load(url).apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(imageView);

        //如果你的图片可能存在超级大图，按下面这样写
        Glide.with(imageView).asBitmap().load(url).apply(buildOptions()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int r = resource.getByteCount() / unit10M;
                if (resource != null && r >= 1) {
//                        BitmapDrawable bd = (BitmapDrawable) resource;
//                        int r = bd.getBitmap().getByteCount() / unit10M;
                    int w = resource.getWidth() / r;
                    int h = resource.getHeight() / r;
                    Glide.with(imageView).load(url).apply(buildOptions().override(w, h)).into(imageView);
                } else {
                    Glide.with(imageView).load(url).apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(imageView);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}