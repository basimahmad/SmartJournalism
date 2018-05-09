package com.example.basimahmad.smartjournalism.Notifications;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.basimahmad.smartjournalism.AppConfig;
import com.example.basimahmad.smartjournalism.AppController;
import com.example.basimahmad.smartjournalism.AttachmentFragment;
import com.example.basimahmad.smartjournalism.LiveBroadcast;
import com.example.basimahmad.smartjournalism.R;
import com.example.basimahmad.smartjournalism.SessionManager;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment{

    View view;
    CustomNotificationAdapter adapter;
    ListView lv;

    private ProgressDialog pDialog;


    private String TAG = "NOTIFICATION_CLASS";

    public NotificationFragment() {
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
        view = inflater.inflate(R.layout.fragemnt_notification, container, false);

        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        getNews();

        return view;
    }


    private ArrayList getNews() {
        final ArrayList<NotificationModel> spacecrafts=new ArrayList<>();
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching News ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_ALL_NOTIFICATION, new Response.Listener<String>() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Server Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray json = new JSONArray(response);
// ...


                    for(int i=0;i<json.length();i++){
                        JSONObject jObj = json.getJSONObject(i);
                        final NotificationModel s=new NotificationModel();

                        Log.d(TAG, "Server Array: " + jObj.toString());

                        s.setTitle(jObj.getString("title"));
                        s.setDescription(jObj.getString("description"));
                        s.setIntent(jObj.getString("intent"));
                        s.setImage(jObj.getString("image"));
                        s.setDateTime(jObj.getString("date"));



                        spacecrafts.add(s);

                        Log.d(TAG, "Array Size: " + spacecrafts.size());


                    }


                    lv= (ListView) view.findViewById(R.id.lv);
                    adapter=new CustomNotificationAdapter(getActivity(), spacecrafts);
                    lv.setAdapter(adapter);



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

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        return spacecrafts;
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