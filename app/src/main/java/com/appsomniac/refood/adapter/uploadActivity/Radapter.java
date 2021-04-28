package com.appsomniac.refood.adapter.uploadActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.appsomniac.refood.R;
import com.appsomniac.refood.classFragments.DashboardFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.Dash;

import java.io.IOException;
import java.util.ArrayList;

public class Radapter extends RecyclerView.Adapter<RviewHolder> {

    public static ArrayList<String> image_uris;
    public Context context;
    private String uploadORsingleview;

    public Radapter(Context context, ArrayList<String> image_uris, String uploadORsingleview) {

        this.context = context;
        this.image_uris = image_uris;
        this.uploadORsingleview = uploadORsingleview;
    }

    @Override
    public RviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new RviewHolder(LayoutInflater.from(parent.getContext()), parent, image_uris, context);

    }

    @Override
    public void onBindViewHolder(RviewHolder holder, int position) {

        if(uploadORsingleview.equals("upload")) {

            String Imag1 = image_uris.get(position);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.color.colorPrimary);
            requestOptions.error(R.color.colorPrimary);

            Glide.with(context).load(Imag1)
                    .apply(requestOptions).thumbnail(0.5f).into(holder.media_poster);

        }else{

            try {
                Bitmap image = decodeFromFirebaseBase64(image_uris.get(position));
                holder.media_poster.setImageBitmap(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e("position holder: ", String.valueOf(position));

    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    public int getItemCount() {

        return image_uris.size();

    }

}
