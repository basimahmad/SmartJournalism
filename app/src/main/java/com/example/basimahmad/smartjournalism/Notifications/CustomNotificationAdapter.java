package com.example.basimahmad.smartjournalism.Notifications;

import android.content.Context;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.basimahmad.smartjournalism.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by basim on 22/04/2018.
 */

public class CustomNotificationAdapter extends BaseAdapter {
    Context c;
    ArrayList<NotificationModel> spacecrafts;

    public CustomNotificationAdapter(Context c, ArrayList<NotificationModel> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public int getCount() {
        return spacecrafts.size();
    }

    @Override
    public Object getItem(int i) {
        return spacecrafts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
        {
            view= LayoutInflater.from(c).inflate(R.layout.notification_model,viewGroup,false);
        }

        final NotificationModel s= (NotificationModel) this.getItem(i);

        ImageView img= (ImageView) view.findViewById(R.id.spacecraftImg);
        TextView nameTxt= (TextView) view.findViewById(R.id.nameTxt);
        TextView propTxt= (TextView) view.findViewById(R.id.propellantTxt);

        nameTxt.setText(s.getTitle());
        propTxt.setText(s.getDescription());
        //img.setImageResource();


        String imageUri = "http://www.krunchycorner.net/uploads/"+s.getImage();
        Picasso.with(getApplicationContext()).load(imageUri).placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(img);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, s.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}