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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "text";
    private ArrayAdapter<Offer> offerArrayAdapter;
    private final ArrayList<Offer> offerArrayList = new ArrayList<Offer>();
    private String entryID;

    private FirebaseAuth mAuth;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRootRef = mDatabase.getReference();

    // wir erstellen eine Child-Reference von der Datenbank
    // hat Zugriff auf den /offer Pfad in der Datenbank
    DatabaseReference mOfferRef = myRootRef.child("Offers");
    DatabaseReference mRequestRef = myRootRef.child("Requests");
    DatabaseReference mUserRef = myRootRef.child("User");

    String username;

    final static int REQUEST_CODE_ADD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        checkAccount();

        setupList();
        //entryID = myRootRef.child("Offers").push().getKey();

    }

    private void checkAccount() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();



    }

    private void showData(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

        // damit nicht immer alles dazu addiert wird, muss die Liste immer zuerst geleert werden
        offerArrayList.clear();

        for (DataSnapshot child: children){

            Offer offer = (Offer) child.getValue(Offer.class);
            String name = offer.getName();
            //toastMessage("Name ist:" + name);
            String offerUploaderId = offer.getId();
            //toastMessage("Id ist:" + offerUploaderId);
            offer.setmName(offerUploaderId);
            offer.setmId();

            offerArrayList.add(offer);
            offerArrayAdapter.notifyDataSetChanged();



        }
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
                        username = downloadedUser.getmUserName();
                        break;
                    }
                }
            }

            // error
            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
            checkUser();
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            profileIntent.putExtra("USERNAME", username);
            startActivity(profileIntent);
            return(true);

        case R.id.about:
            return(true);

        case R.id.logout:
            mAuth.signOut();
            toastMessage("Sie wurden abgemeldet");
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            return(true);


        case R.id.refresh:
            mOfferRef.addListenerForSingleValueEvent(new ValueEventListener() {

                // wird immer aufgerufen, wenn "offer" sich in realtime ändert
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot);
                    // offerArrayList.add(offer);
                }

                // error
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return (true);

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
                //offerArrayAdapter.notifyDataSetChanged();


                mOfferRef.push().setValue(resultOffer);



            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Hier steht was passiert, wenn kein Ergebnis zurückgegeben wird
            }
        }
    }

    public void toastMessage(String message){
        Toast.makeText(
                MainActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }

}
