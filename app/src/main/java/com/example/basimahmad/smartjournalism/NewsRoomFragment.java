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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.example.basimahmad.smartjournalism.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import android.widget.AdapterView.OnItemSelectedListener;
import com.example.basimahmad.smartjournalism.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class NewsRoomFragment extends Fragment{
    private static final String URL_Categories = "http://www.krunchycorner.net/categories.php";

    View view;
    ArrayList<String> file_names = new ArrayList<String>();
    Button attachment;
    FancyButton publish;
    private Spinner category;
    EditText _title, _subject, _description;
    private static final String TAG = "NewsRoomFragment";
    private ProgressDialog pDialog;
    private SessionManager session;
    ArrayList<String> bankNames1= new ArrayList<String>();
    String[] bankNames = {"Politics", "Entertainment", "Sports", "Weather", "Religion"};
    String _category = "Politics";
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
        category = (Spinner) view.findViewById(R.id.category);

        session = new SessionManager(getActivity());

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        //loadCategories();
        //Creating the ArrayAdapter instance having the bank name list
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,bankNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        category.setAdapter(aa);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishNews();
            }
        });

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Toast.makeText(getActivity(), bankNames[position], Toast.LENGTH_LONG).show();
                _category = bankNames[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                    params.put("category", _category);
                    params.put("subject", subject);
                    params.put("description", desc);
                }
                if (file_names.size() == 1){
                   params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("category", _category);
                    params.put("subject", subject);
                    params.put("description", desc);
                    params.put("file1", file_names.get(0));
                }
                if (file_names.size() == 2){
                    params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("category", _category);
                    params.put("subject", subject);
                    params.put("description", desc);
                    params.put("file1", file_names.get(0));
                    params.put("file2", file_names.get(1));
                }
                if (file_names.size() == 3){
                    params.put("uid", String.valueOf(session.getUserID()));
                    params.put("title", title);
                    params.put("category", _category);
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

    private void loadCategories() {

        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_Categories,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                bankNames1.add(
                                        product.getString("category")
                                );
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}