package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedBookmarkFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "NEWSFEED";
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String URL_FEED = "http://www.krunchycorner.net/feed.json";
    ArrayList<String> news_list;
    SessionManager session;
    private ProgressDialog pDialog;

    public NewsFeedBookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    final View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);


        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        session = new SessionManager(getContext());

        news_list = new ArrayList<String>();
        listView = (ListView) view.findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Refresh: ","pull");
                        getNewsID();
                    }
                }
        );

            return view;
    }

    public void getNewsFeed(){

            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                        Log.d("Refresh: ","get data");

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        //}
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = (feedArray.length() - 1); i >= 0; i--) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                if(news_list.contains(feedObj.getString("id"))) {
                    FeedItem item = new FeedItem();
                    item.setId(feedObj.getInt("id"));
                    item.setName(feedObj.getString("name"));
                    item.setCategory(feedObj.getString("category"));
                    // Image might be null sometimes
                    String image = feedObj.isNull("image") ? null : feedObj
                            .getString("image");
                    item.setImge(image);
                    item.setStatus(feedObj.getString("status"));
                    item.setProfilePic(feedObj.getString("profilePic"));
                    item.setTimeStamp(feedObj.getString("timeStamp"));

                    // url might be null sometimes
                    String feedUrl = feedObj.isNull("url") ? null : feedObj
                            .getString("url");
                    item.setUrl(feedUrl);
                    item.setUserId(feedObj.getInt("userid"));

                    feedItems.add(item);
                }
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        Log.d("Refresh: ","pull");
        feedItems.clear();
        getNewsID();
    }

    private void getNewsID() {

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching Bookmarks ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BOOKAMERKS_LIST, new Response.Listener<String>() {

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
                        news_list.clear();
                        JSONArray news_id = jObj.getJSONArray("news_id");
                        for (int i=0; i<news_id.length(); i++) {
                            news_list.add(news_id.getString(i));
                            Log.d("NEWSLIST", String.valueOf(news_list.size()));
                        }

                        getNewsFeed();

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
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(session.getUserID()));

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



