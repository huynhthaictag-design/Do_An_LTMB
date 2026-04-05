package com.example.doanltmb.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.doanltmb.R;

// Helper dung chung de hien thi anh tu drawable hoac tu Uri da luu trong database.
public final class ImageLoader {

    private ImageLoader() {
    }

    // Nap anh san pham vao ImageView va tu dong fallback neu du lieu anh khong hop le.
    public static void loadProductImage(Context context, ImageView imageView, String imageValue) {
        if (imageView == null) return;

        imageView.setImageDrawable(null);
        imageView.setImageTintList(null);

        if (TextUtils.isEmpty(imageValue)) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        if (imageValue.startsWith("content://") || imageValue.startsWith("file://")) {
            try {
                imageView.setImageURI(Uri.parse(imageValue));
                if (imageView.getDrawable() != null) {
                    return;
                }
            } catch (Exception ignored) {
            }
        } else {
            int resId = context.getResources().getIdentifier(imageValue, "drawable", context.getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
                return;
            }
        }

        imageView.setImageResource(R.drawable.ic_launcher_background);
    }
}
