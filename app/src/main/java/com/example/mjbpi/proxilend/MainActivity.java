package com.example.mjbpi.proxilend;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener{

    static final int REQUEST_LOCATION = 1;

    private static final String TAG = "text";
    private ArrayAdapter<Entry> mOfferArrayAdapter;
    private ArrayList<Entry> mEntryArrayList = new ArrayList<Entry>();

    private PopupWindow popupWindow;

    private LocationManager locationManager;

    private Entry mCurrentEntry;

    private Double mUserLat;
    private Double mUserLong;

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

    private static final int MAX_DISTANCE = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        checkAccount();
        setupList();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
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

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location bestLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            //try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            //}
            //catch(SecurityException e) {
            //    e.printStackTrace();
            //}

            if (bestLocation != null) {

                toastMessage("Ihr Standpunkt wurde erfolgreich gesetzt.");
            } else {

                toastMessage("Bitte schalten sie ihr GPS ein!");
            }

        }
    }

    private void refreshDataLists(){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            // wird immer aufgerufen, wenn "user" sich in realtime ändert
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
            // wird immer aufgerufen, wenn "entry" sich in realtime ändert
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


    }

    private void showData(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();

        // damit nicht immer alles dazu addiert wird, muss die Liste immer zuerst geleert werden
        mEntryArrayList.clear();

        //Jeder Eintrag wird geladen und dann in die ArrayList hinzugefügt
        for (DataSnapshot child: children){
            Entry entry = child.getValue(Entry.class);
            int currentDistance = calculateDistance(entry.getLongitude(), entry.getLatitude());
            //Folgende Methode prüft ob das Angebot in der Nähe ist
            if (currentDistance < MAX_DISTANCE) {
                //Die individuelle ID wurde von der Online Datenbank gesetzt und wird hier dem Eintrag gesetzt
                entry.setKey(child.getKey());
                //Die soeben errechnete Distanz wird gesetzt um im Layout angezeigt werden zu können
                entry.setDistance(currentDistance);
                mEntryArrayList.add(entry);
            } else {
                //Angebot ist nicht in der Nähe und wird nicht angezeigt
            }
        }
        mOfferArrayAdapter.notifyDataSetChanged();

    }

    public int calculateDistance(Double longEntry, Double latEntry) {
        /**
         Diese Methode basiert auf der Harvesine Formel um Abstandsberechnungen auf der Erde durchzuführen.
         Der Code basiert auf einer Antwort von 'Sean' in einem Blogeintrag (23. September 2008) auf StackOverFlow.com
         https://stackoverflow.com/questions/120283/how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi
         Da es sich um eine komplexe Formel handelt, die mit Winkelberechnung und Sinus- und Cosinusberechnung arbeitet,
         bitten wir um Verständnis für die fehlende Dokumentation.
         */
        if (mUserLong == null || mUserLat == null) {
            toastMessage("Kein ort verfügbar!");
        } else {
            double earthRadius = 6371000; // Erdradius in Meter
            double dLat = Math.toRadians(latEntry - mUserLat);
            double dLng = Math.toRadians(longEntry - mUserLong);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(mUserLat)) *
                            Math.cos(Math.toRadians(latEntry)) *
                            Math.sin(dLng / 2) *
                            Math.sin(dLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            int distance = (int) (earthRadius * c);
            return distance; //Distanz in Metern
        }

        return 0;
    }

    private void loadUser(DataSnapshot ds) {
        Iterable<DataSnapshot> children = ds.getChildren();
        // damit nicht immer alles dazu addiert wird, muss die Liste immer zuerst geleert werden
        mUserMap.clear();

        for (DataSnapshot child: children){
            User user = child.getValue(User.class);
            mUserMap.put(user.getId(), user);

        }
        mOfferArrayAdapter.notifyDataSetChanged();

    }

    private void showWindow(int position){
        mCurrentEntry = mEntryArrayList.get(position);
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.popup_layout, (ViewGroup) findViewById(R.id.popup_1));
            popupWindow = new PopupWindow(layout, 600, 1000, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

            Button interestButton = (Button) layout.findViewById(R.id.interest);
            Button closePopup = (Button) layout.findViewById(R.id.close_popup);
            Button deleteEntry = (Button) layout.findViewById(R.id.delete_entry);
            Button showProfile = (Button) layout.findViewById(R.id.show_profile);

            interestButton.setOnClickListener(this);
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

        offerViewList.setAdapter(mOfferArrayAdapter);

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

    private void showNotification(String title, String content) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.proxilend_icon_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_popup:
                popupWindow.dismiss();
                break;

            case R.id.show_profile:
                Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                profileIntent.putExtra("ID", mCurrentEntry.getId());
                profileIntent.putExtra("ENTRIES", mEntryArrayList);
                startActivity(profileIntent);
                break;
            case R.id.interest:
                if (!mCurrentEntry.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {


                } else {
                    toastMessage("Sie können sich nicht für ihre eigenen Einträge interessieren!");
                }
                break;
            case R.id.delete_entry:
                if (mCurrentEntry.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mEntryRef.child(mCurrentEntry.getKey()).removeValue();
                    refreshDataLists();
                } else {
                    toastMessage("Sie können nur eigene Einträge löschen!");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

        case R.id.add:
            Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
            if (mUserLong == null || mUserLat == null) {
                toastMessage("Sie benötigen einen Standort um einen Eintrag machen zu können!");
            } else {
                addIntent.putExtra("LATITUDE", mUserLat);
                addIntent.putExtra("LONGITUDE", mUserLong);
                startActivityForResult(addIntent, REQUEST_CODE_ADD);
            }
            return(true);

        case R.id.profile:
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            profileIntent.putExtra("ID", FirebaseAuth.getInstance().getUid());
            profileIntent.putExtra("ENTRIES", mEntryArrayList);
            startActivity(profileIntent);
            return(true);

        case R.id.about:

            Intent aboutIntent = new Intent(MainActivity.this, About.class);
            startActivity(aboutIntent);
            return(true);

            case R.id.refresh_location:
                getLocation();
            return(true);

        case R.id.logout:
            mAuth.signOut();
            toastMessage("Sie wurden abgemeldet");
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            return(true);


        case R.id.refresh:
            //Test Button
            //showNotification("Angebot in ihrer Nähe!","Auf geht's!");

            mEntryRef.addListenerForSingleValueEvent(new ValueEventListener() {

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
                resultEntry.setLatitude(mUserLat);
                resultEntry.setLongitude(mUserLong);
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
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mUserLat = location.getLatitude();
        mUserLong = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
