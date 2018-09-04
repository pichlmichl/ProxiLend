package com.example.mjbpi.proxilend;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference myRootRef;
    private DatabaseReference mUserRef;
    private TextView mUsernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDatabase = FirebaseDatabase.getInstance();
        myRootRef = mDatabase.getReference();
        mUserRef = myRootRef.child("User");

        mUsernameTextView = (TextView) findViewById(R.id.textName);
        mUsernameTextView.setText("Loading...");
        checkUser();
    }

    private void checkUser(){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            // wird immer aufgerufen, wenn "offer" sich in realtime ändert
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child: children){

                    User downloadedUser = (User) child.getValue(User.class);
                    if (downloadedUser.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mUsernameTextView.setText(downloadedUser.getUserName());
                        break;
                    }
                }
            }

            // error
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.settings_profile:
                //öffne profileinstellungen
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onOptionsMenu(Menu menu) {
        return true;
    }

}
