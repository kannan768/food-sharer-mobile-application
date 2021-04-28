package com.appsomniac.refood.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.appsomniac.refood.R;
import com.appsomniac.refood.adapter.uploadActivity.Radapter;
import com.appsomniac.refood.base.MainActivity;
import com.appsomniac.refood.classFragments.DashboardFragment;
import com.appsomniac.refood.model.FoodPost;
import com.google.android.gms.maps.model.Dash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingleItemActivity extends AppCompatActivity {

    private FoodPost foodPost;
    private int position;
    Radapter adapter;
    RecyclerView my_recycler_view;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("position", 0);
        setBottomNavView();

        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);
        my_recycler_view.setHasFixedSize(true);
        my_recycler_view.setNestedScrollingEnabled(true);

        adapter = new Radapter(this, DashboardFragment.all_posts.get(position).getAl_imageEncoded(), "singleview");
        my_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        my_recycler_view.setAdapter(adapter);

        setItemValues();

    }

    public void setItemValues(){

        TextView postedBy = (TextView) findViewById(R.id.postedBy_value);
        postedBy.setText(DashboardFragment.all_posts.get(position).getFoodPostedByName());

        TextView contact = (TextView) findViewById(R.id.contact_value);
        contact.setText(DashboardFragment.all_posts.get(position).getContact());

        TextView foodType = (TextView) findViewById(R.id.foodtype_value);
        foodType.setText(DashboardFragment.all_posts.get(position).getFoodType());

        TextView quantity = (TextView) findViewById(R.id.quantity_value);
        quantity.setText(DashboardFragment.all_posts.get(position).getFoodQunatity());

        TextView description = (TextView) findViewById(R.id.description_value);
        description.setText(DashboardFragment.all_posts.get(position).getFoodDescription());

        TextView address = (TextView) findViewById(R.id.address_value);
        address.setText(DashboardFragment.all_posts.get(position).getFoodLocation());
    }

    public void setBottomNavView(){

        BottomNavigationView bottomBar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.call:

                        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                        dialIntent.setData(Uri.parse("tel:" + DashboardFragment.all_posts.get(position).getContact()));
                        startActivity(dialIntent);

                        break;
                    case R.id.delete:

                        checkUserAndDeleteFood();
                        break;
                }
                return true;
            }
        });
    }

    public void checkUserAndDeleteFood(){

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userID = user.getUid();

        if(userID.equals(DashboardFragment.all_posts.get(position).getFoodPostedByUserId())){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("all_posts").child(DashboardFragment.all_posts.get(position).getRefKey());
            reference.removeValue();

            startActivity(new Intent(this, MainActivity.class));

        }else{
            Toast.makeText(getApplicationContext(), "You can't delete this item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return true;
    }
}