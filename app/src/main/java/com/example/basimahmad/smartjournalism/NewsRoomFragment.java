package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NewsRoomFragment extends Fragment{
    View view;
    Button attachment;
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
        attachment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new AttachmentFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
        return view;
    }

}