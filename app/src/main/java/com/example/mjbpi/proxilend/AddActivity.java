package com.example.mjbpi.proxilend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AddActivity extends AppCompatActivity {

    private EditText editText;
    private RadioGroup type;
    private RadioButton offer, need;
    private Button submit;

    private int radioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initUI();
        initActionBar();

        submit.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String name = editText.getText().toString();

                    // hier wird überprüft ob es ein Angebot oder eine Nachfrage ist
                    if (radioId == R.id.offerButton) {
                        // Ein offer wird erstellt
                        Offer offer = new Offer(name);
                        // Hier wird die aktuelle Zeit abgerufen und im Offer gesetzt!
                        offer.setCreationDate(System.currentTimeMillis());
                        // Ein result intent gibt das Ergebnis der Main Activity zurück
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("offer", offer);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();

                    } else if(radioId == R.id.needButton) {
                        Request request = new Request(name);
                    }
                }

            }
        );
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
                } else if(checkedId == R.id.needButton) {
                    // Checked ID is a request
                    radioId = checkedId;
                }
            }
        });

        submit = (Button) findViewById(R.id.submit_button);


    }


}
