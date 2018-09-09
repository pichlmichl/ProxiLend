package com.example.mjbpi.proxilend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private Button mSend;
    private EditText mInput;

    private Map<String, User> mUserMap = new HashMap<String, User>();

    private ArrayList<ChatMessage> mChatArrayList  = new ArrayList<ChatMessage>();

    private ArrayAdapter<ChatMessage> mChatArrayAdapter;

    private FirebaseAuth mAuth;

    private String mStrangerId;
    private String mStrangerName;
    private String mUserName;

    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRootRef = mDatabase.getReference();
    DatabaseReference mChatRef = mRootRef.child("Messages");
    DatabaseReference mUserRef = mRootRef.child("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setupList();
        initActionBar();
        refreshChatList();

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        mStrangerId = extras.getString("ID");
        mStrangerName = extras.getString("NAME");

        mSend = (Button) findViewById(R.id.send_message);
        mInput = (EditText) findViewById(R.id.input_chat_message);

        checkUser();

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInput = (EditText)findViewById(R.id.input_chat_message);
                ChatMessage mes = new ChatMessage();
                mes.setCreationDate(System.currentTimeMillis());
                mes.setMessage(mInput.getText().toString());
                mes.setChatroomKey(FirebaseAuth.getInstance().getCurrentUser().getUid() + mStrangerId);
                mes.setName(mUserName);
                mes.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mChatRef.push().setValue(mes);
                mInput.setText("");
                refreshChatList();
            }
        });



    }
    private void checkUser(){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child: children){
                    User downloadedUser = (User) child.getValue(User.class);
                    if ((downloadedUser.getId()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        mUserName = downloadedUser.getUserName();
                        break;
                    }
                }
            }

            // error
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void refreshChatList() {
     mChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    // damit nicht immer alles dazu addiert wird, muss die Liste immer zuerst geleert werden
                    mChatArrayList.clear();
                    //Jeder Eintrag wird geladen und dann in die ArrayList hinzugefügt
                    for (DataSnapshot child: children){
                        ChatMessage message = child.getValue(ChatMessage.class);
                        //Hier wird überprüft welche Nachrichten angezeigt werden dürfen
                        if (message.getChatroomKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid() + mStrangerId) ||
                            message.getChatroomKey().equals(mStrangerId + FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                mChatArrayList.add(message);
                        } else {

                        }
                    }
                    mChatArrayAdapter.notifyDataSetChanged();
                }

                // error
                @Override
                public void onCancelled(DatabaseError databaseError) {
               }
            });
     }

    public void toastMessage(String message){
        Toast.makeText(
                ChatActivity.this, message,
                Toast.LENGTH_LONG).show();
    }

    private void setupList(){
        ListView chatList = (ListView) findViewById(R.id.message_list);
        mChatArrayAdapter = new ChatAdapter(this, mChatArrayList, mUserMap);
        chatList.setAdapter(mChatArrayAdapter);
    }

    private void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.refresh_chat:
                refreshChatList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
