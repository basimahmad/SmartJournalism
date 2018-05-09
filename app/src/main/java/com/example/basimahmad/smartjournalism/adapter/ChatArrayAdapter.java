package com.example.basimahmad.smartjournalism.adapter;

/**
 * Created by basim on 09/05/2018.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.basimahmad.smartjournalism.R;
import com.example.basimahmad.smartjournalism.RoundedTransformation;
import com.example.basimahmad.smartjournalism.model.ChatMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText;
    private ImageView userImg;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.right, parent, false);
        }else{
            row = inflater.inflate(R.layout.left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        userImg = (ImageView) row.findViewById(R.id.user_img);
        Picasso.with(getContext()).load(chatMessageObj.pic).transform(new RoundedTransformation(200, 4))
                .placeholder(R.drawable.dpholderwhit1)
                .error(R.drawable.dpholderwhit1)
                .into(userImg);
        chatText.setText(chatMessageObj.message);
        return row;
    }
}

