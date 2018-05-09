package com.example.basimahmad.smartjournalism;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.basimahmad.smartjournalism.Notifications.NotificationFragment;
import com.example.basimahmad.smartjournalism.SharedPrefManagerPackage.SharedPrefManager;
import com.example.basimahmad.smartjournalism.utils.NotificationUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.messaging.FirebaseMessaging;

import cz.msebera.android.httpclient.util.TextUtils;

import static com.example.basimahmad.smartjournalism.AppConfig.URL_REGISTER_DEVICE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static Uri imageuri;

    FragmentManager manager;
    NavigationView navigationView;
    private ProgressDialog pDialog;
    static Context context;
    private SessionManager session;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        manager = getFragmentManager();



        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        String token = SharedPrefManager.getInstance(this).getDeviceToken();

        //if token is not null
        if (token != null) {
         Log.d("TOKEN_CHECK", token);
            sendTokenToServer(email, token);
        } else {
            //if token is null that means something wrong
            Log.d("TOKEN_CHECK", "Token not generated");
        }




        MainActivity.context = getApplicationContext();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        session = new SessionManager(getApplicationContext());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

             get_nav_user();
        //add this line to display menu1 when the activity is loaded
        displaySelectedScreen(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("NOTI", "1");
            Fragment fragment = new NotificationFragment();
            if (fragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.content_frame, fragment, "notification");
                ft.addToBackStack("notification");
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public static void gotoNews(String id) {
        Intent i = new Intent(context, NewsDetailClass.class);
        i.putExtra("news_id",id);
        context.startActivity(i);
    }

    public void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        String name = "home";
        switch (itemId) {
            case R.id.nav_home:
                name = "home";
                fragment = new NewsFeedFragment();
                break;
            case R.id.nav_profile:
                name = "profile";
                fragment = new ProfileFragment();
                break;
            case R.id.nav_newsroom:
                name = "newsroom";
                fragment = new NewsRoomFragment();
                break;
            case R.id.nav_inbox:
                name = "inbox";
                fragment = new InboxFragment();
                break;
            case R.id.nav_categories:
                name = "categories";
                fragment = new CategoriesFragment();
                break;
            case R.id.nav_saved_articles:
                name = "bookmarks";
                fragment = new NewsFeedBookmarkFragment();
                break;
            case R.id.nav_updateaccount:
                name = "update_account";
                fragment = new EditProfileFragment();
                break;
            case R.id.nav_publishednews:
                name = "published_news";
                fragment = new PublishedNewsFeedFragment();
                break;
            case R.id.nav_logout:
                Log.d("log", "logout");
                session.setLogin(false);
                session.removeUserID();
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, fragment, name);
            ft.addToBackStack(name);
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }










    private void get_nav_user() {

        final SharedPreferences.Editor editor = getSharedPreferences("SMART", MODE_PRIVATE).edit();

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Fetching Data ...");
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

                            View hView =  navigationView.getHeaderView(0);
                            TextView nav_user = (TextView)hView.findViewById(R.id.nav_user_name);
                            nav_user.setText(name);
                            ImageView nav_pic = (ImageView)hView.findViewById(R.id.nav_profile_pic);
                            String imageUri = "http://www.krunchycorner.net/profilePic/"+pic_link;
                            Picasso.with(getApplicationContext()).load(imageUri).transform(new RoundedTransformation(200, 4))
                                    .placeholder(R.drawable.dpholderwhit1)
                                    .error(R.drawable.dpholderwhit1)
                                    .into(nav_pic);

                            editor.putString("user_name", name);
                            editor.putString("user_dp", imageUri);

                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                Log.e("MainActivity", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
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

   /* public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("REQ", String.valueOf(requestCode));
        if (resultCode == RESULT_OK) {
            if (requestCode == Template.Code.FILE_MANAGER_CODE) {
                Log.d("IMAGEUPLOAD", "sda");
            } else {
                Log.d("IMAGEUPLOAD", "123");
            }

        } else {
           // resetView();
        }


    }*/

    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void sendTokenToServer(final String email, final String token) {
        Log.d("RED_DEVICE", email + "----" + token);

        // Tag used to cancel the request
        String tag_string_req = "req_login";


        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_REGISTER_DEVICE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    Toast.makeText(MainActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();


            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("token", token);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }

}