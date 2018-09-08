package com.example.mjbpi.proxilend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {



    private FirebaseDatabase mDatabase;
    private DatabaseReference myRootRef;
    private DatabaseReference mUserRef;

    private TextView mUsernameTextView;

    private String mId;

    private ArrayAdapter<Entry> mProfileArrayAdapter;
    private ArrayList<Entry> mProfileArrayList = new ArrayList<Entry>();
    private ArrayList<Entry> mEntryArrayList = new ArrayList<Entry>();

    private Map<String, User> mUserMap = new HashMap<String, User>();


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
        initActionBar();
        setupList();


        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            mId = extras.getString("ID");
            mEntryArrayList = extras.getParcelableArrayList("ENTRIES");
        }

        checkEntries(mEntryArrayList);


    }

    private void checkUser(){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            // wird immer aufgerufen, wenn "offer" sich in realtime ändert
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                loadUser(dataSnapshot);
                for (DataSnapshot child: children){
                    User downloadedUser = (User) child.getValue(User.class);
                    if ((downloadedUser.getId()).equals(mId)) {
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

    private void checkEntries(ArrayList<Entry> arrayList) {

        for (Entry entry: arrayList) {
            if (entry.getId().equals(mId)){
                mProfileArrayList.add(entry);
            }
        }
        mProfileArrayAdapter.notifyDataSetChanged();
    }

    private void setupList() {
        ListView profileViewList = (ListView) findViewById(R.id.profile_list);
        mProfileArrayAdapter = new EigenerAdapter(this, mProfileArrayList, mUserMap);
        profileViewList.setAdapter(mProfileArrayAdapter);
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUser(DataSnapshot ds) {
        Iterable<DataSnapshot> children = ds.getChildren();
        // damit nicht immer alles dazu addiert wird, muss die Liste immer zuerst geleert werden
        mUserMap.clear();

        for (DataSnapshot child: children){
            User user = child.getValue(User.class);
            mUserMap.put(user.getId(), user);

        }
        mProfileArrayAdapter.notifyDataSetChanged();

    }

}