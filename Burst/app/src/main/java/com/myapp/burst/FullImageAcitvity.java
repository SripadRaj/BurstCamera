package com.myapp.burst;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * Created by SRamesh on 4/8/2017.
 */

public class FullImageAcitvity extends Activity {

    TouchImageView fullImage;
    Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        bundle = getIntent().getExtras();

        fullImage = (TouchImageView) findViewById(R.id.full_image);

        String uri = bundle.getString("uri");

        fullImage.setImageURI(Uri.parse(uri));

        Uri u = Uri.parse(uri);
        File f = new File(u.getPath());

        try {
            ExifInterface exifInterface = new ExifInterface(f.getAbsolutePath());
            String orientationString  = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
            int rotationAngle = 0;
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(f.getAbsolutePath(), bounds);

            int orientation = Integer.parseInt(orientationString);
            switch (orientation){
                case ExifInterface.ORIENTATION_NORMAL:
                    rotationAngle = 0;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = 270;
                    break;
            }

            Picasso.with(FullImageAcitvity.this).load(u).rotate(rotationAngle).into(fullImage);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
