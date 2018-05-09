package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.basimahmad.smartjournalism.R;
import com.example.basimahmad.smartjournalism.adapter.MessagesAdapter;
import com.example.basimahmad.smartjournalism.helper.DividerItemDecoration;
import com.example.basimahmad.smartjournalism.model.Message;
import com.example.basimahmad.smartjournalism.network.ApiClient;
import com.example.basimahmad.smartjournalism.network.ApiInterface;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class InboxFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener{
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private View view;
    private SessionManager session;

    public InboxFragment() {
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
        view = inflater.inflate(R.layout.fragment_inbox, container, false);

        session = new SessionManager(getActivity());


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MessagesAdapter(getActivity(), messages, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("INCHECK", "1");
                        messages.clear();

                        getInbox();
                    }
                }
        );

        return view;
    }

    /**
     * Fetches mail messages by making HTTP request
     * url: https://api.androidhive.info/json/inbox.json
     */
    private void getInbox() {




        Firebase.setAndroidContext(getContext());
        Firebase ref = new Firebase("https://citizen-journalism-app.firebaseio.com/messages");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    messages.clear();
                    Log.d("COUNTMESSAGES" ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Log.d("KEYNAME", postSnapshot.getKey());
                    String key = postSnapshot.getKey();
                    int position = key.indexOf("_");
                    Log.d("KEYPOS", String.valueOf(position));
                    String substr=key.substring(0,position);
                    final String substr1=key.substring(position+1);
                    Log.d("KEYSTR", substr1);
                    if(substr.equals(String.valueOf(session.getUserID()))){
                        Log.d("INBOXCHECK", "found");
                        Firebase ref1 = new Firebase("https://citizen-journalism-app.firebaseio.com/messages/"+key);
                        messages.clear();
                        swipeRefreshLayout.setRefreshing(true);
                        ref1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                Log.d("COUNTMESSAGES1" ,""+snapshot.getChildrenCount());
                                Message messageObj = new Message();

                                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                    Log.d("KEYNAME1", postSnapshot.getKey());

                                   /* Message message = snapshot.getValue(Message.class);
                                    Log.d("COUNTMESSAGE1" ,"1");
                                    message.setColor(getRandomMaterialColor("400"));
                                    messages.add(message);*/

                                    String isImpStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("isImportant").getValue());
                                    String isReadStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("isRead").getValue());
                                    String msgStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("message").getValue());
                                    String picStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("picture").getValue());
                                    String userIdStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("user").getValue());
                                    String nameStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("to").getValue());
                                    String timeStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("timestamp").getValue());
                                    String toIdStr = String.valueOf(snapshot.child(postSnapshot.getKey()).child("to_user").getValue());

                                    boolean isImp = Boolean.parseBoolean(isImpStr);
                                    boolean isRead = Boolean.parseBoolean(isReadStr);

                                    messageObj.setImportant(isImp);
                                    messageObj.setRead(isRead);
                                    messageObj.setMessage(msgStr);
                                    if(substr1.equals("0")){
                                        messageObj.setPicture("http://www.krunchycorner.net/profilePic/admin.png");
                                    }
                                    else {
                                        messageObj.setPicture("http://www.krunchycorner.net/profilePic/" + substr1 + ".jpg");
                                    }
                                    messageObj.setTimestamp(timeStr);
                                    messageObj.setFrom(nameStr);
                                    messageObj.setTo_id(toIdStr);
                                    messageObj.setKey(postSnapshot.getKey());
                                    messageObj.setColor(getRandomMaterialColor("400"));


                                    Log.d("COUNTMESSAGE1", "======="+messages.size());


                                }
                                messages.add(messageObj);
                                mAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.e("The read failed: " ,firebaseError.getMessage());
                            }
                        });
                        swipeRefreshLayout.setRefreshing(false);

                    }



                }


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }

        });
        Log.d("COUNTMESSAGE1", "=======++"+messages.size());
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getContext().getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        Log.d("INCHECK", "2");
        messages.clear();

        getInbox();
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            //actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Message message = messages.get(position);
        message.setImportant(!message.isImportant());
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            Message message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            mAdapter.notifyDataSetChanged();

            Toast.makeText(getActivity(), "Read: " + String.valueOf(message.getTo_id()), Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("SMART", MODE_PRIVATE).edit();
            editor.putString("profile_user_id", String.valueOf(message.getTo_id()));
            editor.apply();

//            Fragment fragment = new ChatFragment();
//
//            if (fragment != null) {
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.add(R.id.content_frame, fragment, "chat");
//                ft.addToBackStack("chat");
//                ft.replace(R.id.content_frame, fragment);
//                ft.commit();
//            }

            Intent i = new Intent(getContext(), ChatActivity.class);
            startActivity(i);

        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            //actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

//        if (count == 0) {
//            actionMode.finish();
//        } else {
//            actionMode.setTitle(String.valueOf(count));
//            actionMode.invalidate();
//        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
}

