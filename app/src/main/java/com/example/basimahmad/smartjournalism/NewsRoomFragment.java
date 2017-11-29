package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class NewsRoomFragment extends Fragment{
    View view;
    ArrayList<String> file_names = new ArrayList<String>();
    Button attachment;
    FancyButton publish;
    EditText _title, _subject, _description;
    private static final String TAG = "NewsRoomFragment";
    private ProgressDialog pDialog;
    private SessionManager session;

    public NewsRoomFragment() {
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
        view = inflater.inflate(R.layout.fragment_newsroom, container, false);
        attachment = (Button) view.findViewById(R.id.attachment);
        publish = (FancyButton) view.findViewById(R.id.btn_publish);
        _title = (EditText) view.findViewById(R.id.input_title);
        _subject = (EditText) view.findViewById(R.id.input_subject);
        _description = (EditText) view.findViewById(R.id.input_des);

        session = new SessionManager(getActivity());

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishNews();
            }
        });


        attachment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new AttachmentFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, fragment, "newsroom");
                ft.addToBackStack("newsroom");
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });


        return view;
    }

    public void publishNews() {
        Log.d(TAG, "publishNews");


        publish.setEnabled(false);
        String title = _title.getText().toString();
        String subject = _subject.getText().toString();
        String desc = _description.getText().toString();


        upload(title, subject, desc);


    }




    private void upload(final String title,
                              final String subject, final String desc) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Publishing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPLOADNEWS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Server Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d(TAG,  response);
                        Toast.makeText(getActivity(), "News is sent to the admin for approval!", Toast.LENGTH_LONG).show();


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        Log.d(TAG,  response);
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        publish.setEnabled(true);
                        publish.setClickable(true);
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
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                publish.setEnabled(true);
                publish.setClickable(true);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                AttachmentFragment af = new AttachmentFragment();
                file_names = af.file_names;

                if (file_names.size() == 0){
                    params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("subject", subject);
                    params.put("description", desc);
                }
                if (file_names.size() == 1){
                   params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("subject", subject);
                    params.put("description", desc);
                    params.put("file1", file_names.get(0));
                }
                if (file_names.size() == 2){
                    params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("subject", subject);
                    params.put("description", desc);
                    params.put("file1", file_names.get(0));
                    params.put("file2", file_names.get(1));
                }
                if (file_names.size() == 3){
                    params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("subject", subject);
                    params.put("description", desc);
                    params.put("file1", file_names.get(0));
                    params.put("file2", file_names.get(1));
                    params.put("file3", file_names.get(2));
                }

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