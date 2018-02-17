package com.example.basimahmad.smartjournalism;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


        bookmark_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
                {
                    ((ImageButton) bookmark_button).setImageResource(getResources().getIdentifier("bookmark_select", "drawable", getPackageName()));
                }
                else
                {
                    ((ImageButton) bookmark_button).setImageDrawable(getDrawable(getResources().getIdentifier("bookmark_select", "drawable", getPackageName())));
                }                addBookmark();
            }
        });

    }

    private void addBookmark() {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Publishing ...");
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

    private void getNews(final String news_id) {

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching News ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NEWS_DETAIL, new Response.Listener<String>() {

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

                        TextView tv_date = (TextView) findViewById(R.id.news_timestamp);
                        tv_date.setText(jObj.getString("date_time"));

                        TextView tv_author = (TextView) findViewById(R.id.news_author);
                        tv_author.setText(jObj.getString("author_name"));

                        TextView tv_subject = (TextView) findViewById(R.id.news_subject);
                        tv_subject.setText(jObj.getString("subject"));

                        TextView tv_des = (TextView) findViewById(R.id.news_news);
                        tv_des.setText(jObj.getString("description"));

                        JSONArray files = jObj.getJSONArray("files");
                        for (int i=0; i<files.length(); i++) {
                            String f = files.getString(i);
                            ImageView nav_pic = (ImageView) findViewById(R.id.news_img1);
                            String imageUri = "http://www.krunchycorner.net/uploads/"+f;
                            Picasso.with(getApplicationContext()).load(imageUri)
                                    .into(nav_pic);

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
