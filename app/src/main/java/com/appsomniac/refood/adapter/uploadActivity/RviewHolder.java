package com.appsomniac.refood.adapter.uploadActivity;

import android.content.Context;
import android.os.Build;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeImageTransform;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsomniac.refood.R;

import java.util.ArrayList;

import static android.support.design.R.attr.expanded;

public class RviewHolder extends RecyclerView.ViewHolder {

    public ImageView media_poster;
    public TextView description;
    public Context mContext;

    public RviewHolder(LayoutInflater inflater, ViewGroup parent, final ArrayList<String> image_uris, Context context) {

        super(inflater.inflate(R.layout.upload_single_card, parent, false));
        media_poster = itemView.findViewById(R.id.media_poster);
        this.mContext = context;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

            }
        });
    }


}