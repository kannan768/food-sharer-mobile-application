package com.appsomniac.refood.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.appsomniac.refood.R;

public class FullImageActivity extends AppCompatActivity {
    ImageView imgFullImage;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_full_image);
//
//        ///findViewBYID
//        imgFullImage = (ImageView) findViewById(R.id.full_imageview);
//
//        // Bundle bundle = getIntent().getExtras();
//        //String image = bundle.getString("image");
//        //String image = ModelBase64.base64Image;
//        //Bitmap bitmap = decodeImage(image);
//        imgFullImage.setImageBitmap(bitmap);
//    }
//
//    private Bitmap decodeImage(String data) {
//        byte[] b = Base64.decode(data, Base64.DEFAULT);
//        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
//        return bmp;
//    }
}