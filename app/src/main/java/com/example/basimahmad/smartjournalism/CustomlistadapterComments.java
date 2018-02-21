package com.example.basimahmad.smartjournalism;

/**
 * Created by Basim Ahmad on 2/20/2018.
 */
import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomlistadapterComments extends ArrayAdapter{
    ArrayList<String> color_names;
    ArrayList<String> image_id;
    ArrayList<String> name;

    Context context;
    public CustomlistadapterComments(Activity context,ArrayList<String> image_id, ArrayList<String> text, ArrayList<String> name){
        super(context, R.layout.list_row, text);
        // TODO Auto-generated constructor stub
        this.color_names = text;
        this.image_id = image_id;
        this.name = name;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View single_row = inflater.inflate(R.layout.list_row, null,
                true);
        TextView textView = (TextView) single_row.findViewById(R.id.textView_comment);
        TextView textViewname = (TextView) single_row.findViewById(R.id.textView_name);

        ImageView imageView = (ImageView) single_row.findViewById(R.id.imageView);
        textView.setText(color_names.get(position));
        textViewname.setText(name.get(position));
        String imageUri = image_id.get(position);
      //  imageView.setImageResource(image_id.get(position));
        Picasso.with(getContext()).load(imageUri).transform(new RoundedTransformation(200, 4))
                .placeholder(R.drawable.dpholderwhit1)
                .error(R.drawable.dpholderwhit1)
                .into(imageView);
        return single_row;
    }
}

