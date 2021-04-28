package com.appsomniac.refood.adapter.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.appsomniac.refood.R;
import com.appsomniac.refood.adapter.uploadActivity.RviewHolder;
import com.appsomniac.refood.model.FoodPost;
import com.appsomniac.refood.other.RecyclerViewAnimator;
import com.appsomniac.refood.other.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DashboardRadapter extends RecyclerView.Adapter<DashboardRviewholder> {

    private RecyclerViewAnimator mAnimator;
    RecyclerView recyclerView;

    public static ArrayList<FoodPost> all_posts;
    public Context context;

    public DashboardRadapter(Context context, ArrayList<FoodPost> all_posts, RecyclerView recyclerView) {

        this.context = context;
        this.all_posts = all_posts;
       // mAnimator = new RecyclerViewAnimator(recyclerView);
        this.recyclerView = recyclerView;

    }

    @Override
    public DashboardRviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);

        DashboardRviewholder vh = new DashboardRviewholder(LayoutInflater.from(parent.getContext()), parent, context, all_posts);
       // mAnimator.onCreateViewHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(DashboardRviewholder holder, int position) {

        Bitmap image = null;
        try {
            image = decodeFromFirebaseBase64(all_posts.get(position).getAl_imageEncoded().get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.item_image.setImageBitmap(image);

        byte[] imageByteArray = Base64.decode(all_posts.get(position).getAl_imageEncoded().get(0), Base64.DEFAULT);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.color.colorPrimary);
        requestOptions.error(R.color.colorPrimary);

        Glide.with(context).asBitmap().load(imageByteArray)
                .apply(requestOptions).thumbnail(0.5f).into(holder.item_image);
//
        String postedBy = all_posts.get(position).getFoodType();
        String contact = all_posts.get(position).getContact();

        holder.postedBy.setText(postedBy);
        holder.contact.setText(contact);

        runEnterAnimation(holder.itemView);


        /**
         * Item's entrance animations during scroll are performed here.
         */
       // mAnimator.onBindViewHolder(holder.itemView, position);
        //recyclerView.getAdapter().notifyItemInserted(position);


        Log.e("position holder: ", String.valueOf(position));

    }

    private void runEnterAnimation(View view) {
        view.setTranslationY(Utils.getScreenHeight(context));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    @Override
    public int getItemCount() {

        return all_posts.size();

    }

}
