package com.example.mjbpi.proxilend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity{

    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText usernameEditText;

    private Button registrationButton;
    private Button loginButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRootRef = mDatabase.getReference();
    DatabaseReference myUserRef = myRootRef.child("User");

    private static final String TAG = "LoginActivity";

    private String email;
    private String password;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //User ist angemeldet

                }else{
                    //User ist abgemeldet
                    //toastMessage("Bitte alles richtig eingeben!");

                }
            }
        };

        initUi();

         loginButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                    getInputs();
                    signIn();
                  }
           });


        registrationButton.setOnClickListener( new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                    getInputs();
                    createAccount();

                  }
           });

    }

    public void initUi(){
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        loginButton = (Button) findViewById(R.id.sign_in);
        registrationButton = (Button) findViewById(R.id.registration);
    }

    public void getInputs(){
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        username = usernameEditText.getText().toString();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        //updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void updateUI(){}

    public String getCurrentId() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return uid;
    }

    public void createAccount() {

        if(!email.equals("") && !password.equals("")) {
            if(password.length() > 5) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                if (task.isSuccessful()) {
                                    FirebaseUser user = task.getResult().getUser();
                                    Log.d(TAG, "onComplete: uid=" + user.getUid());
                                    createUser();
                                }
                            }
                        });
            } else {
                toastMessage(getString(R.string.password_hint_length));
            }
        }
    }


    public void toastMessage(String message){
        Toast.makeText(
                LoginActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }

    public void signIn() {

        if(!email.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                toastMessage("Angemeldet mit: " + email);
                                finish();
                            } else {
                                toastMessage("Falsches Passwort oder falsche Email!");
                            }
                        }
                    });
        }else{
            toastMessage("Bitte allles eingeben!");
            }
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void createUser(){
        User newUser = new User();
        String uid = getCurrentId();

        newUser.setId(uid);
        newUser.setMail(email);
        newUser.setUserName(username);

        myUserRef.push().setValue(newUser);
        toastMessage("Account wurde erstellt mit folgender Email: " + email);
        finish();
    }
}
