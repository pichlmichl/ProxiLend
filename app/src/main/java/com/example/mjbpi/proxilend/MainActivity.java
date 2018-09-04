package com.example.mjbpi.proxilend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "text";
    private ArrayAdapter<Entry> mOfferArrayAdapter;
    private ArrayList<Entry> mEntryArrayList = new ArrayList<Entry>();

    private PopupWindow popupWindow;

    private Button showProfile;
    private Button closePopup;
    private Button deleteEntry;

    private Entry mCurrentEntry;

    private Map<String, User> mUserMap = new HashMap<String, User>();

    private FirebaseAuth mAuth;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRootRef = mDatabase.getReference();

    // wir erstellen eine Child-Reference von der Datenbank
    // hat Zugriff auf den /entry Pfad in der Datenbank
    DatabaseReference mEntryRef = myRootRef.child("Entries");
    // hat Zugriff auf den /user Pfad in der Datenbank
    DatabaseReference mUserRef = myRootRef.child("User");

    final static int REQUEST_CODE_ADD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initUi();
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
        refreshDataLists();
    }

    private void refreshDataLists(){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            // wird immer aufgerufen, wenn "offer" sich in realtime ändert
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadUser(dataSnapshot);
                // offerArrayList.add(offer);
            }

            // error
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mEntryRef.addListenerForSingleValueEvent(new ValueEventListener() {



    }

    private void showData(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

        // damit nicht immer alles dazu addiert wird, muss die Liste immer zuerst geleert werden
        mEntryArrayList.clear();

        for (DataSnapshot child: children){
            Entry entry = child.getValue(Entry.class);
            entry.setKey(child.getKey());
            mEntryArrayList.add(entry);

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

    private void showWindow(int position){
        mCurrentEntry = mEntryArrayList.get(position);
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_1));
            popupWindow = new PopupWindow(layout, 600, 1000, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

            closePopup = (Button) layout.findViewById(R.id.close_popup);
            deleteEntry = (Button) layout.findViewById(R.id.delete_entry);
            showProfile = (Button) layout.findViewById(R.id.show_profile);

            deleteEntry.setOnClickListener(this);
            showProfile.setOnClickListener(this);
            closePopup.setOnClickListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupList(){
        ListView offerViewList = (ListView) findViewById(R.id.offer_list);
        mOfferArrayAdapter = new EigenerAdapter(this, mEntryArrayList, mUserMap);

        offerViewList.setAdapter(offerArrayAdapter);

        offerViewList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //was passiert wenn einzelne Sachen angeklickt werden
                    showWindow(position);

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
            Intent aboutIntent = new Intent(MainActivity.this, About.class);
            startActivity(aboutIntent);
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
                Entry resultEntry = data.getParcelableExtra("entry_key");
                mEntryRef.push().setValue(resultEntry);



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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_popup:
                popupWindow.dismiss();
                break;
            case R.id.show_profile:


                break;
            case R.id.delete_entry:
                mEntryRef.child(mCurrentEntry.getKey()).removeValue();
                refreshDataLists();


                break;
            default:
                break;
        }
    }
}
