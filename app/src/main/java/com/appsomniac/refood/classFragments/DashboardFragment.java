package com.appsomniac.refood.classFragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appsomniac.refood.R;
import com.appsomniac.refood.activity.UploadActivity;
import com.appsomniac.refood.adapter.dashboard.DashboardRadapter;
import com.appsomniac.refood.model.FoodPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardFragment extends Fragment {

    View dashboard_fragment;
    FloatingActionButton fab_camera;
    DashboardRadapter adapter;
    RecyclerView my_recycler_view;
    private Button btn;
    private ImageView imageview;
    ProgressBar progressBar;

    LayoutAnimationController controller;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    public static ArrayList<FoodPost> all_posts;

    private ArrayList<String> image_uris;
    private ArrayList<String> al_image_encoded;

    private static final String IMAGE_DIRECTORY = "/reFood";
    private int GALLERY = 1, CAMERA = 2;
    private RecyclerView rv;
    String[] permissions = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public DashboardFragment() {
        //empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dashboard_fragment = inflater.inflate(R.layout.dashboard_layout, container, false);

        checkPermissions();

        fab_camera = getActivity().findViewById(R.id.fab);
        image_uris=new ArrayList<String>();
        al_image_encoded = new ArrayList<>();
        progressBar = dashboard_fragment.findViewById(R.id.dashboard_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        getAllPostsFromDatabase();
        setListenersOnFab();

        return dashboard_fragment;
    }

    public void getAllPostsFromDatabase(){

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("all_posts");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                all_posts = new ArrayList<FoodPost>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    FoodPost posts = dataSnapshot1.getValue(FoodPost.class);

                    all_posts.add(posts);
                }

                if(all_posts.size()!=0) {

                    my_recycler_view = (RecyclerView) dashboard_fragment.findViewById(R.id.dashboard_frag_recycler_view);
                    my_recycler_view.setHasFixedSize(true);
                    my_recycler_view.setNestedScrollingEnabled(true);

                    //need to get the arrayList of the post POJOs.
                    adapter = new DashboardRadapter(getContext(), all_posts, my_recycler_view);
                    my_recycler_view.setLayoutManager(new GridLayoutManager(getContext(), 2));

                    my_recycler_view.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);

                    //This methos is for FADEin FADEout animation of each card
                    setAnimationAndAdapter();

                }else
                    if(all_posts.size()==0){

                        RelativeLayout placeholder = dashboard_fragment.findViewById(R.id.empty_placeholder);
                        progressBar.setVisibility(View.GONE);
                        placeholder.setVisibility(View.VISIBLE);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setAnimationAndAdapter(){

        //Animation to recyclerView
//        AnimationSet set = new AnimationSet(true);
//        Animation animation = new AlphaAnimation(0.0f, 1.0f);
//        animation.setDuration(500);
//        set.addAnimation(animation);
//
//        animation = new TranslateAnimation(
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
//        );
//        animation.setDuration(100);
//        set.addAnimation(animation);

//        controller = new LayoutAnimationController(set, 0.5f);
//        my_recycler_view.setAdapter(adapter);
//        my_recycler_view.setLayoutAnimation(controller);
//        progressBar.setVisibility(View.GONE);

    }

    public void setListenersOnFab() {
        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissions();
                showPictureDialog();
            }
        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            return;
        }
    }
    private void showPictureDialog(){
        takePhotoFromCamera();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getContext(), UploadActivity.class);
                    i.putStringArrayListExtra("image_uris", image_uris);
                    i.putStringArrayListExtra("al_image_encoded", al_image_encoded);
                    startActivity(i);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            saveImage(thumbnail);
            Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getContext(), UploadActivity.class);
            i.putStringArrayListExtra("image_uris", image_uris);
            i.putStringArrayListExtra("al_image_encoded", al_image_encoded);
            startActivity(i);

            getActivity().finish();

        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);

        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        al_image_encoded.add(imageEncoded);

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(baos.toByteArray());
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            image_uris.add(f.getAbsolutePath());
            //Toast.makeText(getContext(), "Absolute Path: "+ f.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            // rd.notifyDataSetChanged();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}