package com.example.mjbpi.proxilend;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "text";
    private ArrayAdapter<Offer> offerArrayAdapter;
    private ArrayList<Offer> offerArrayList = new ArrayList<Offer>();
    int i= 1;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRootRef = mDatabase.getReference();

    // wir erstellen eine Child-Reference von der Datenbank
    // hat Zugriff auf den /offer Pfad in der Datenbank
    DatabaseReference mOfferRef = myRootRef.child("Offers");
    DatabaseReference mRequestRef = myRootRef.child("Requests");

    final static int REQUEST_CODE_ADD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initOnlineDatabase();
        setupList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mOfferRef.addValueEventListener(new ValueEventListener() {

            // wird immer aufgerufen, wenn "offer" sich in realtime ändert
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    //Offer offer = dataSnapshot.getValue(Offer.class);
                   // offerArrayList.add(offer);
            }

            // error
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void setupList(){

        ListView offerViewList = (ListView) findViewById(R.id.offer_list);
        offerArrayAdapter = new EigenerAdapter(this, offerArrayList);

        offerViewList.setAdapter(offerArrayAdapter);

        offerViewList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //was passiert wenn einzelne Sachen angeklickt werden

                }
            }
        );


        offerViewList.setOnItemLongClickListener(
            new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        // was passiert wenn lannge drauf drückt wird
                        return true;
                }
            }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

        case R.id.add:
            Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
            startActivityForResult(addIntent, REQUEST_CODE_ADD);
            return(true);

        case R.id.profile:
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
            return(true);

        case R.id.about:
            return(true);

        case R.id.prefs:
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Hier wird gecheckt welche Activity mit welcher Aufgabe fertig ist
        if (requestCode == REQUEST_CODE_ADD) {

            // REQUEST_CODE_ADD heißt, es geht um die Eintrag erstellen Funktion
            if(resultCode == Activity.RESULT_OK){
                Offer resultOffer = data.getParcelableExtra("offer");
                //offerArrayList.add(resultOffer);


                mOfferRef.push().setValue(resultOffer);

                //offerArrayAdapter.notifyDataSetChanged();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Hier steht was passiert, wenn kein Ergebnis zurückgegeben wird
            }
        }
    }

    private void writeNewUser(String userId, String name, String email) {
        //User user = new User(name, email);

        //mDatabase.child("users");
        //.child(userId).setValue(user);
    }



}
