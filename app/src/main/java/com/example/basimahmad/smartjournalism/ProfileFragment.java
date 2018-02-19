package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.basimahmad.smartjournalism.adapter.FeedListAdapter;
import com.example.basimahmad.smartjournalism.data.FeedItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment{
    View view;
    private ProgressDialog pDialog;
    private static final String TAG = "PROFILE_NEWSFEED";
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "https://api.androidhive.info/feed/feed.json";
    private SessionManager session;
    public ProfileFragment() {
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
        view = inflater.inflate(R.layout.activity_profile, container, false);


        session = new SessionManager(getContext());
        // Progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);


        getuser();



        return view;
    }


    private void getuser() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching User Data ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_NAV_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("MainActivity", "Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        try {

                            JSONObject jsonObject=new JSONObject((String) response);
                            String first_name=jsonObject.getString("first_name");
                            String last_name=jsonObject.getString("last_name");
                            String pic_link=jsonObject.getString("pic");
                            Log.d("MainActivity", "Pic: " + pic_link);
                            String name = first_name +" "+last_name;


                            TextView profile_name = (TextView) view.findViewById(R.id.profile_name);
                            profile_name.setText(name);

                            TextView profile_news_sub = (TextView) view.findViewById(R.id.profile_news_submitted);
                            TextView profile_news_approved = (TextView) view.findViewById(R.id.profile_news_approved);                                  TextView profile_email = (TextView) view.findViewById(R.id.profile_email);
                            TextView profile_mb = (TextView) view.findViewById(R.id.profile_mb);
                            TextView profile_address = (TextView) view.findViewById(R.id.profile_address);

                            profile_news_sub.setText(String.valueOf(jsonObject.getString("submitted_news")));
                            profile_news_approved.setText(String.valueOf(jsonObject.getString("approved_news")));
                            profile_email.setText(String.valueOf(jsonObject.getString("email")));
                            profile_mb.setText(String.valueOf(jsonObject.getString("mobile")));
                            profile_address.setText(String.valueOf(jsonObject.getString("address")));




                            ImageView nav_pic = (ImageView)view.findViewById(R.id.profile_pic);
                            String imageUri = "http://www.krunchycorner.net/profilePic/"+pic_link;
                            Picasso.with(getContext()).load(imageUri).transform(new RoundedTransformation(200, 4))
                                    .placeholder(R.drawable.dpholderwhit1)
                                    .error(R.drawable.dpholderwhit1)
                                    .into(nav_pic);











                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();

                    }
                }catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("MainActivity", "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(session.getUserID()));

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