package com.pingus.vent.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pingus.vent.Model.ChatArrayAdapter;
import com.pingus.vent.Model.ChatMessage;
import com.pingus.vent.Model.User;
import com.pingus.vent.R;

public class ChatroomActivity extends AppCompatActivity {
    private DatabaseReference database;
    private FirebaseUser user;
    private EditText messageText;
    private String userName;
    private ListView messageList;
    private ChatArrayAdapter lvAdapter;
    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        //instantiate gui elements
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        messageText = (EditText) findViewById(R.id.messageEditText);
        messageList = (ListView) findViewById(R.id.listChatRoom);

        //setup toolbar with back button and name
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("CHATROOM_NAME"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //setup listview and adapter
        messageList.setDivider(null);
        messageList.setDividerHeight(0);
        lvAdapter = new ChatArrayAdapter(this, R.layout.message_left);
        //add user listener to get username of current user to pass to Adapter
        database = FirebaseDatabase.getInstance().getReference().child("chatroomlist").child(getIntent().getStringExtra("CHATROOM_NAME")).child("messages");
        user = FirebaseAuth.getInstance().getCurrentUser();
        database.getRoot().child("users").child(user.getUid()).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = (String) dataSnapshot.getValue();
                if (userName == null) {
                    database.getRoot().child("users").child(user.getUid()).setValue(new User("no_username"));
                }
                lvAdapter.setUsername(userName);
                lvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        messageList.setAdapter(lvAdapter);
        messageList.setVisibility(View.VISIBLE);

        //set database adapter to listen for new messsages added
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatMessage(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatMessage(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * onClick method for send button
     * sends chat message to database
     * @param view the view that's being used, must be there ¯\_(ツ)_/¯
     */
    public void send(View view) {
        ChatMessage message = new ChatMessage(messageText.getText().toString(), userName);
        if(message.getMessageText().isEmpty()) {
            return;
        }
        message.setLeft(false);
        database.push().setValue(message);
        messageText.setText("");
    }

    /**
     * appends a new chat message to the message list when added
     * @param dataSnapshot the datasnapshot of the recently added child
     */
    private void appendChatMessage(DataSnapshot dataSnapshot) {
        //cm = recently added chat message, return if null for some reason
        ChatMessage cm = dataSnapshot.getValue(ChatMessage.class);
        if (cm == null) {
            return;
        }
        lvAdapter.add(cm);
        lvAdapter.notifyDataSetChanged();
        messageList.setSelection(lvAdapter.getCount() - 1);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}