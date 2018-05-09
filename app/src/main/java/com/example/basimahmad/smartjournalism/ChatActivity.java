package com.example.basimahmad.smartjournalism;

/**
 * Created by basim on 09/05/2018.
 */
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.basimahmad.smartjournalism.R;
import com.example.basimahmad.smartjournalism.adapter.ChatArrayAdapter;
import com.example.basimahmad.smartjournalism.model.ChatMessage;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private ImageView buttonSend;
    private boolean side = false;
    Firebase reference1, reference2;
    SessionManager session;
    String chatWith = "";
    String chatWithName="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);

        buttonSend = (ImageView) findViewById(R.id.sendButton);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);


        SharedPreferences prefs = getSharedPreferences("SMART", MODE_PRIVATE);
        chatWith = prefs.getString("profile_user_id", null);
        if (chatWith == null) {
            Toast.makeText(ChatActivity.this,
                    "User not found", Toast.LENGTH_LONG).show();
        }
        else {
        }

        chatWithName = prefs.getString("profile_chatWith_name", null);
        if (chatWithName == null) {
            Toast.makeText(ChatActivity.this,
                    "User not found", Toast.LENGTH_LONG).show();
        }
        else {
        }

        session = new SessionManager(ChatActivity.this);

        Firebase.setAndroidContext(ChatActivity.this);
        reference1 = new Firebase("https://citizen-journalism-app.firebaseio.com/messages/" + session.getUserID() + "_" + chatWith);
        reference2 = new Firebase("https://citizen-journalism-app.firebaseio.com/messages/" + chatWith + "_" + session.getUserID());

        Log.d("REF_CHECK_CHECT", session.getUserID() + "_" + chatWith);


        chatText = (EditText) findViewById(R.id.messageArea);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String map_key = dataSnapshot.getKey();
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                int prev_message = 0;
                if(userName.equals(String.valueOf(session.getUserID()))){
                    chatArrayAdapter.add(new ChatMessage(true, map.get("message").toString()+"\n"+map.get("timestamp").toString(), map.get("picture").toString()));
                }
                else{
                    chatArrayAdapter.add(new ChatMessage(false, map.get("message").toString()+"\n"+map.get("timestamp").toString(), map.get("picture").toString()));
                }
                updateReadStatus(map_key);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private boolean sendChatMessage() {
        //chatArrayAdapter.add(new ChatMessage(false, chatText.getText().toString()));
        SharedPreferences prefs = getSharedPreferences("SMART", MODE_PRIVATE);
        String dp = prefs.getString("user_dp", null);
        String name_user = prefs.getString("user_name", null);
        Log.d("DPNAME",dp+"==="+name_user);


        String currentDateTimeString = null;
        String messageText = chatText.getText().toString();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            Log.d("DATETIME", currentDateTimeString);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM'/'dd'/'y hh:mm");
        String currentDateandTime = sdf.format(new Date());
        Log.d("DATETIME", currentDateandTime);
        if(!messageText.equals("")){
            Map<String, String> map = new HashMap<String, String>();
            map.put("isImportant", "false");
            map.put("picture", dp);
            map.put("user", String.valueOf(session.getUserID()));
            map.put("user_name",name_user);
            map.put("message", messageText);
            map.put("timestamp",currentDateandTime);
            map.put("isRead", "false");
            map.put("from", name_user);
            map.put("to", chatWithName);
            map.put("to_user", chatWith);
            reference1.push().setValue(map);

            map.put("to", name_user);
            map.put("to_user", String.valueOf(session.getUserID()));

            reference2.push().setValue(map);
        }
        chatText.setText("");
        return true;
    }

    private void updateReadStatus(String k){
        try {
            reference1.child(k).child("isRead").setValue("true");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}