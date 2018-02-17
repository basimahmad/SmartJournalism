package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment{
    View view;
    private ProgressDialog pDialog;
    private SessionManager session;
    public EditProfileFragment() {
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
        view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        session = new SessionManager(getContext());
        // Progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);


        getuser();


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> adapter = new ArrayList<String>();
                adapter.add("Gallery");
                adapter.add("Camera");
                new LovelyChoiceDialog(getActivity())
                        .setTopColorRes(R.color.colorPrimary)
                        .setTopTitle("Select Source")
                        .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                            @Override
                            public void onItemSelected(int position, String item) {

                                if(position == 0){
                                    imageBrowse1();
                                }
                                else if(position == 1){
                                    imageBrowseCamera2();
                                }
                            }


                        })
                        .show();
            }
        });


        return  view;
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


                            TextView profile_name = (TextView) view.findViewById(R.id.edit_name);
                            TextView profile_name_edit_last = (TextView) view.findViewById(R.id.edit_input_last_name);
                            TextView profile_name_edit = (TextView) view.findViewById(R.id.edit_input_first_name);

                            profile_name.setText(name);
                            profile_name_edit.setText(first_name);
                            profile_name_edit_last.setText(last_name);


                            TextView profile_email = (TextView) view.findViewById(R.id.edit_input_email);
                            TextView profile_mb = (TextView) view.findViewById(R.id.edit_input_phone);
                            TextView profile_address = (TextView) view.findViewById(R.id.edit_input_address);

                            profile_email.setText(String.valueOf(jsonObject.getString("email")));
                            profile_mb.setText(String.valueOf(jsonObject.getString("mobile")));
                            profile_address.setText(String.valueOf(jsonObject.getString("address")));




                            ImageView nav_pic = (ImageView)view.findViewById(R.id.edit_profile_img_back);
                            String imageUri = "http://www.krunchycorner.net/profilePic/"+pic_link;
                            Picasso.with(getContext()).load(imageUri)
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


    private void imageBrowse1() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        getActivity().startActivityForResult(galleryIntent, 31);
    }
    private void imageBrowseCamera2() {
        String fileName = "temp.jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        MainActivity.imageuri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Log.d("filePath", "--"+ MainActivity.imageuri.toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,  MainActivity.imageuri);
        getActivity().startActivityForResult(intent, 32);

    }

}