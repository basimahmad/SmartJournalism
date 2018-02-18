package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/5/2017.
 */
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseApp;
        import com.spark.submitbutton.SubmitButton;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.HashMap;
        import java.util.Map;

        import butterknife.ButterKnife;
        import butterknife.InjectView;
        import mehdi.sakout.fancybuttons.FancyButton;

public class SignupActivity extends Activity {
    private static final String TAG = "SignupActivity";
    private ProgressDialog pDialog;
    private SessionManager session;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;

    @InjectView(R.id.btn_signup) FancyButton _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    private EditText _firstNameText,_lastNameText,_mbText,_addressText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        //Previous versions of Firebase
            Firebase.setAndroidContext(this);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        _firstNameText = (EditText) findViewById(R.id.input_first_name);
        _lastNameText = (EditText) findViewById(R.id.input_last_name);
        _mbText = (EditText) findViewById(R.id.input_mobile);
        _addressText = (EditText) findViewById(R.id.input_address);



        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SignupActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });




    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        String fname = _firstNameText.getText().toString();
        String lname = _lastNameText.getText().toString();

        String mb = _mbText.getText().toString();
        String address = _emailText.getText().toString();


        registerUser(email, password, fname, lname, mb, address);
        String name = fname+" "+lname;

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        String fname = _firstNameText.getText().toString();
        String lname = _lastNameText.getText().toString();

        String mb = _mbText.getText().toString();
        String address = _emailText.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (fname.isEmpty()) {
            _firstNameText.setError("enter your first name");
            valid = false;
        } else {
            _firstNameText.setError(null);
        }

        if (lname.isEmpty()) {
            _lastNameText.setError("enter your last name");
            valid = false;
        } else {
            _lastNameText.setError(null);
        }

        if (mb.isEmpty()) {
            _mbText.setError("enter your mobile number");
            valid = false;
        } else {
            _mbText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("enter your address");
            valid = false;
        } else {
            _addressText.setError(null);
        }
        return valid;
    }

    private void registerUser(final String email, final String password, final String fname, final String lname,
                              final String mb, final String address) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
       // showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.getString("email");
                        registerFirebase(uid, email);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        _signupButton.setEnabled(true);
                        _signupButton.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                _signupButton.setEnabled(true);
                _signupButton.setClickable(true);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("fname", fname);
                params.put("lname", lname);
                params.put("mb", mb);
                params.put("address", address);

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

    private void registerFirebase(final String email, final String name){

        Log.d("FIREBASE_CHECK", email+"---"+name);

        String url = "https://citizen-journalism-app.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Log.d("FIREBASE_CHECK", "1");
            //    hideDialog();

                Firebase reference = new Firebase("https://citizen-journalism-app.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child(email).child("email").setValue(name);
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(email)) {
                            reference.child(email).child("email").setValue(name);
                            Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Something Went Wrong. Try Again!", Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }




                // Launch login activity
                Intent intent = new Intent(
                        SignupActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();


            }

        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("FIREBASE_CHECK", "2");
             //   hideDialog();

                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SignupActivity.this);
        rQueue.add(request);
    }


}