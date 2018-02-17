package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 11/6/2017.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

public class CategoriesFragment extends Fragment{
    View view;
    ImageButton live_broadcast, politics, sports, entertainment, technology, religion, weather;
    public CategoriesFragment() {
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
        view =  inflater.inflate(R.layout.fragment_categories, container, false);
        live_broadcast = (ImageButton) view.findViewById(R.id.live);
        politics = (ImageButton) view.findViewById(R.id.politics);
        sports = (ImageButton) view.findViewById(R.id.sports);
        entertainment = (ImageButton) view.findViewById(R.id.Entertainment);
        technology = (ImageButton) view.findViewById(R.id.Technology);
        religion = (ImageButton) view.findViewById(R.id.Religion);
        weather = (ImageButton) view.findViewById(R.id.Weather);

        live_broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        politics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category", "Politics");
                Log.d("Categories", "politics clicked");
                Fragment fragment = new NewsFeedCategoryFragment();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category", "Sports");
                Log.d("Categories", "Sports clicked");
                Fragment fragment = new NewsFeedCategoryFragment();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        entertainment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category", "Entertainment");
                Log.d("Categories", "Entertainment clicked");
                Fragment fragment = new NewsFeedCategoryFragment();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category", "Technology");
                Log.d("Categories", "Technology clicked");
                Fragment fragment = new NewsFeedCategoryFragment();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        religion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category", "Religion");
                Log.d("Categories", "Religion clicked");
                Fragment fragment = new NewsFeedCategoryFragment();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("category", "Weather");
                Log.d("Categories", "Weather clicked");
                Fragment fragment = new NewsFeedCategoryFragment();
                fragment.setArguments(bundle);
                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }
            }
        });

        return view;

    }

}