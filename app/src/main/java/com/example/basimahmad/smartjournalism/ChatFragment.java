package com.example.basimahmad.smartjournalism;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Basim Ahmad on 2/19/2018.
 */

public class ChatFragment  extends Fragment {
    View view;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    SessionManager session;
    String chatWith = "";
    String chatWithName="";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activit_chat, container, false);
        session = new SessionManager(getContext());


        layout = (LinearLayout) view.findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)view.findViewById(R.id.layout2);
        sendButton = (ImageView)view.findViewById(R.id.sendButton);
        messageArea = (EditText)view.findViewById(R.id.messageArea);
        scrollView = (ScrollView)view.findViewById(R.id.scrollView);


        SharedPreferences prefs = getContext().getSharedPreferences("SMART", MODE_PRIVATE);
        chatWith = prefs.getString("profile_user_id", null);
        if (chatWith == null) {
            Toast.makeText(getContext(),
                    "User not found", Toast.LENGTH_LONG).show();
        }
        else {
        }

        chatWithName = prefs.getString("profile_chatWith_name", null);
        if (chatWithName == null) {
            Toast.makeText(getContext(),
                    "User not found", Toast.LENGTH_LONG).show();
        }
        else {
        }

        Firebase.setAndroidContext(getContext());
        reference1 = new Firebase("https://citizen-journalism-app.firebaseio.com/messages/" + session.getUserID() + "_" + chatWith);
        reference2 = new Firebase("https://citizen-journalism-app.firebaseio.com/messages/" + chatWith + "_" + session.getUserID());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", String.valueOf(session.getUserID()));
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();
                int prev_message = 0;
                if(userName.equals(String.valueOf(session.getUserID()))){
                    addMessageBox("You:\n" + message, 1);
                }
                else{
                    addMessageBox(chatWithName + ":\n" + message, 2);
                }
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

        return  view;
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(getContext());
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
           //  textView.setBackgroundResource(R.color.gray);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
           //  textView.setBackgroundResource(R.color.colorAccent);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }


}