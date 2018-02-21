package com.example.basimahmad.smartjournalism;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Basim Ahmad on 2/6/2018.
 */

public class NewsDetailClass extends Activity{
    String TAG = "NEWS_DETAIL";
    private ProgressDialog pDialog;
    ImageButton bookmark_button;
    String news_id = "";
    SessionManager session;
    Firebase reference1, reference2;
    ImageView sendButton;
    EditText messageArea;
    String user_name, user_dp;

    boolean bookmark_check = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_detail);
        bookmark_button = (ImageButton) findViewById(R.id.bookmark_button);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent myIntent = getIntent();
        news_id = myIntent.getStringExtra("news_id");
        getNews(news_id);

        session = new SessionManager(NewsDetailClass.this);
      final ArrayList<String> color_name = new ArrayList<String>();
       final ArrayList<String> image_id = new ArrayList<String>();
        final ArrayList<String> name = new ArrayList<String>();


        SharedPreferences prefs = getSharedPreferences("SMART", MODE_PRIVATE);
        user_name = prefs.getString("user_name", null);
        if (user_name == null) {
            Toast.makeText(NewsDetailClass.this,
                    "User not found", Toast.LENGTH_LONG).show();
        }
        else {
        }
        user_dp = prefs.getString("user_dp", null);
        if (user_dp == null) {
            Toast.makeText(NewsDetailClass.this,
                    "User not found", Toast.LENGTH_LONG).show();
        }
        else {
        }

        ImageView img1 = (ImageView) findViewById(R.id.news_img1);
        img1.setVisibility(View.GONE);
        ImageView img2 = (ImageView) findViewById(R.id.news_img2);
        img2.setVisibility(View.GONE);
        ImageView img3 = (ImageView) findViewById(R.id.news_img3);
        img3.setVisibility(View.GONE);


        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if(bookmark_check){
                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
                    {
                        ((ImageButton) bookmark_button).setImageResource(getResources().getIdentifier("bookmark", "drawable", getPackageName()));
                    }
                    else
                    {
                        ((ImageButton) bookmark_button).setImageDrawable(getDrawable(getResources().getIdentifier("bookmark", "drawable", getPackageName())));
                    }

                    removeBookmark();

                }
                else {

                    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
                    {
                        ((ImageButton) bookmark_button).setImageResource(getResources().getIdentifier("bookmark_select", "drawable", getPackageName()));
                    }
                    else
                    {
                        ((ImageButton) bookmark_button).setImageDrawable(getDrawable(getResources().getIdentifier("bookmark_select", "drawable", getPackageName())));
                    }

                    addBookmark();

                }



            }
        });




        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);


        Firebase.setAndroidContext(NewsDetailClass.this);
        reference1 = new Firebase("https://citizen-journalism-app.firebaseio.com/comments/" + session.getUserID());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("comment", messageText);
                    map.put("name", user_name);
                    map.put("image", user_dp);
                    map.put("user", String.valueOf(session.getUserID()));
                    map.put("news", news_id);

                    reference1.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("comment").toString();
                String userName = map.get("name").toString();
                String comment = map.get("comment").toString();
                String image = map.get("image").toString();
                String news = map.get("news").toString();

                if(news.equals(news_id)) {
                    color_name.add(comment);
                    image_id.add(image);
                    name.add(userName);

                    CustomlistadapterComments adapter = new CustomlistadapterComments(NewsDetailClass.this, image_id, color_name, name);
                    ListView lv = (ListView) findViewById(R.id.listViewComment);
                    lv.setAdapter(adapter);
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

    }

    private void addBookmark() {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Adding Bookmark ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ADD_BOOKMARK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Server Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d(TAG,  response);
                        Toast.makeText(NewsDetailClass.this, "Bookmark Added", Toast.LENGTH_LONG).show();
                        bookmark_check = true;


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        Log.d(TAG,  response);
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(NewsDetailClass.this,
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());
                Log.d(TAG,  error.getMessage());
                Toast.makeText(NewsDetailClass.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", String.valueOf(session.getUserID()));
                params.put("news_id", news_id);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void removeBookmark() {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Removing Bookmark ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REMOVE_BOOKMARK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Server Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d(TAG,  response);
                        Toast.makeText(NewsDetailClass.this, "Bookmark Removed", Toast.LENGTH_LONG).show();
                        bookmark_check = false;



                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        Log.d(TAG,  response);
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(NewsDetailClass.this,
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Server Error: " + error.getMessage());
                Toast.makeText(NewsDetailClass.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", String.valueOf(session.getUserID()));
                params.put("news_id", news_id);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void getNews(final String news_id) {

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching News ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NEWS_DETAIL, new Response.Listener<String>() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Server Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        TextView tv_title = (TextView) findViewById(R.id.news_title);
                        tv_title.setText(jObj.getString("title"));

                        bookmark_check = jObj.getBoolean("bookmark");
                        Log.d("BOOKMARK", String.valueOf(jObj.getBoolean("bookmark")));
                        if(bookmark_check){
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
                            {
                                ((ImageButton) bookmark_button).setImageResource(getResources().getIdentifier("bookmark_select", "drawable", getPackageName()));
                            }
                            else
                            {
                                ((ImageButton) bookmark_button).setImageDrawable(getDrawable(getResources().getIdentifier("bookmark_select", "drawable", getPackageName())));
                            }

                        }
                        else {
                            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
                            {
                                ((ImageButton) bookmark_button).setImageResource(getResources().getIdentifier("bookmark", "drawable", getPackageName()));
                            }
                            else
                            {
                                ((ImageButton) bookmark_button).setImageDrawable(getDrawable(getResources().getIdentifier("bookmark", "drawable", getPackageName())));
                            }

                        }

                        TextView tv_date = (TextView) findViewById(R.id.news_timestamp);
                        tv_date.setText(jObj.getString("date_time"));

                        TextView tv_author = (TextView) findViewById(R.id.news_author);
                        tv_author.setText(jObj.getString("author_name"));

                        TextView tv_subject = (TextView) findViewById(R.id.news_subject);
                        tv_subject.setText(jObj.getString("subject"));

                        TextView tv_des = (TextView) findViewById(R.id.news_news);
                        tv_des.setText(jObj.getString("description"));

                        int[] img_id = {R.id.news_img1, R.id.news_img2, R.id.news_img3};
                        JSONArray files = jObj.getJSONArray("files");
                        for (int i=0; i<files.length(); i++) {
                            String f = files.getString(i);
                            ImageView nav_pic = (ImageView) findViewById(img_id[i]);
                            String imageUri = "http://www.krunchycorner.net/uploads/"+f;
                            Picasso.with(getApplicationContext()).load(imageUri)
                                    .into(nav_pic);
                            nav_pic.setVisibility(View.VISIBLE);
                            Log.d(TAG,  f);
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                }catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(session.getUserID()));
                params.put("news_id", news_id);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
