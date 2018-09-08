package com.example.mjbpi.proxilend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Provider;
import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    private EditText editText;
    private RadioGroup type;
    private RadioButton offer, need;
    private Button submit;

    private Double mLong, mLat;

    private String userName;
    private String mType;

    public static final String OFFER_TYPE = "OFFER";
    public static final String REQUEST_TYPE = "REQUEST";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private int radioId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initUI();
        initActionBar();

        Intent dataIntent = getIntent();
        Bundle extras = dataIntent.getExtras();
        mLong = extras.getDouble("LONGITUDE");
        mLat = extras.getDouble("LATITUDE");



        submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createEntry();
                        //getLocation();
                    }
            }
        );
    }

    private void createEntry(){
        String name = editText.getText().toString();

        // wir müssen überprüfen, ob ein name eingegeben wurde UND ob ein Feld angekreuzt wurde!
        if (radioId != -1 && !name.equals("")) {
                // Ein offer_button wird erstellt
                Entry entry = new Entry();
                entry.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                entry.setName(name);
                // Hier wird die aktuelle Zeit abgerufen und im Entry gesetzt!
                entry.setCreationDate(System.currentTimeMillis());
                // hier wird überprüft ob es ein Angebot oder eine Nachfrage ist
                if (radioId == R.id.offerButton) {
                    entry.setType(OFFER_TYPE);
                } else if (radioId == R.id.needButton) {
                    entry.setType(REQUEST_TYPE);
                }


                // Ein result intent gibt das Ergebnis der Main Activity zurück
                Intent resultIntent = new Intent();
                resultIntent.putExtra("entry_key", entry);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
        } else {
            // falls nicht alles eingegeben wurde soll ein Toast entstehen
            Toast.makeText(AddActivity.this, "Error: Bitte geben sie alle Daten ein!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUsername() {

        if (user != null) {
            userName = user.getDisplayName();
            boolean emailVerified = user.isEmailVerified();
        }
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //Zurück Knopf: Es wird kein Ergebnis zurückgegeben!
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onOptionsMenu(Menu menu) {
        return true;
    }

    public void initUI() {
        editText = (EditText) findViewById(R.id.name_entry);
        type = (RadioGroup) findViewById(R.id.type_group);

        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.offerButton) {
                    // Checked ID is an offer
                    radioId = checkedId;
                    Log.d("yay", "Entry is checked");
                } else if(checkedId == R.id.needButton) {
                    // Checked ID is a request
                    radioId = checkedId;
                } else  {
                    radioId = 420;

                }
            }
        });


        submit = (Button) findViewById(R.id.submit_button);


    }

    public void toastMessage(String message){
        Toast.makeText(
                AddActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }


}
