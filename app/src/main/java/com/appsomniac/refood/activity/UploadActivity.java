package com.appsomniac.refood.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.appsomniac.refood.R;
import com.appsomniac.refood.base.MainActivity;
import com.appsomniac.refood.adapter.uploadActivity.Radapter;
import com.appsomniac.refood.model.FoodPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    ArrayList<String> image_uris;
    ArrayList<String> al_image_encoded;
    private RecyclerView rv;
    public Button add_more, post_btn;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    EditText editFoodType, editFoodQuantity, editUserLocation, editUserContact, editFoodDescription;
    Spinner foodTypeSpinner;
    Radapter adapter;
    RecyclerView my_recycler_view;
    private static final String IMAGE_DIRECTORY = "/reFood";
    private int GALLERY = 1, CAMERA = 2;
    String[] permissions = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeViews();

        image_uris = getIntent().getStringArrayListExtra("image_uris");
        al_image_encoded = getIntent().getStringArrayListExtra("al_image_encoded");

        if(image_uris.size()<=3) {

            add_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermissions();
                    showPictureDialog();
                }
            });
        }else
            if(image_uris.size()>=4){
                add_more.setEnabled(false);
            }


        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editFoodQuantity.getText().length()>=0 && editFoodType.getText().length()>=1 && editUserLocation.getText().length()>=4
                        && editUserContact.getText().length()>=7 && image_uris.size()>=2){

                    postToDatabase();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                }else {

                    if(image_uris.size()<2){
                        Toast.makeText(getApplicationContext(), "Add atleast 2 photos of the food.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Complete Details first!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        ImageView foodQuantityHint = (ImageView) findViewById(R.id.foodQuantityHint);
        foodQuantityHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Eg: 1 bottle, 2 plates", Toast.LENGTH_SHORT).show();

            }
        });

        ImageView foodDescriptionHint = (ImageView) findViewById(R.id.foodDescriptionHint);
        foodDescriptionHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Eg: 1 bottle frooti available.", Toast.LENGTH_SHORT).show();

            }
        });

        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        my_recycler_view.setNestedScrollingEnabled(true);

        adapter = new Radapter(this, image_uris, "upload");
        my_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        my_recycler_view.setAdapter(adapter);

    }

    public void postToDatabase(){

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userID = user.getUid();

        //no need to add the same the same posts to user structre as it can be related by all_posts using user_id.
        //myRef.child("users").child(userID).child("posts").push().setValue(post);

        //to get the same push() key, call push() only once;
        DatabaseReference newRef = myRef.child("all_posts").push();

        String refKey = newRef.getKey();

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String user_name = prefs.getString("name", "User");

        FoodPost post = new FoodPost(editFoodType.getText().toString(), editFoodQuantity.getText().toString(), user_name
                , userID, editUserLocation.getText().toString(), editUserContact.getText().toString(), editFoodDescription.getText().toString()
                , al_image_encoded, refKey);

        newRef.setValue(post);
    }

    public void initializeViews(){

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add_more = (Button) findViewById(R.id.button_add_more);
        post_btn = (Button) findViewById(R.id.button_post);
        editFoodType = (EditText) findViewById(R.id.foodTypeText);
        editFoodQuantity = (EditText) findViewById(R.id.foodQuantity);
        editUserContact = (EditText) findViewById(R.id.senderContact);
        editUserLocation = (EditText) findViewById(R.id.senderLocation);
        editFoodDescription = (EditText) findViewById(R.id.foodDescription);
        foodTypeSpinner = (Spinner) findViewById(R.id.foodTypeSpinner);

        addListenerOnSpinnerItemSelection();
    }

    public void addListenerOnSpinnerItemSelection() {

        foodTypeSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            editFoodType.setText(foodTypeSpinner.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {
//                "Select photo from gallery",
//                "Capture photo from camera" };

        String[] pictureDialogItems = {
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    Log.e("Bitmap: ", String.valueOf(bitmap));

                    String path = saveImage(bitmap);
                    Toast.makeText(UploadActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(UploadActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Log.e("Bitmap: ", String.valueOf(thumbnail));
            saveImage(thumbnail);
            Toast.makeText(UploadActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
//            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            image_uris.add(f.getAbsolutePath());
            if(image_uris.size()>=4){
                add_more.setEnabled(false);
            }
            adapter.notifyDataSetChanged();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        image_uris.clear();
        adapter.notifyDataSetChanged();
        finish();
        super.onBackPressed();
        //finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }
}
