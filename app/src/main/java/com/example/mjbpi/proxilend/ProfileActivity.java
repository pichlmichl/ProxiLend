package com.example.mjbpi.proxilend;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
