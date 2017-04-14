package com.pingus.vent.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pingus.vent.R;

import java.util.ArrayList;

/**
 * Created by August on 4/13/2017.
 */

public class GroupsArrayAdapter extends ArrayAdapter<ChatGroup> {

    private Context context;

    private ArrayList<ChatGroup> chatList = new ArrayList<>();

    private ChatMessage message;

    @Override
    public void add(ChatGroup object) {
        chatList.add(object);
        super.add(object);
    }
    public GroupsArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        message = null;
    }
    @Override
    public int getCount() {
        return this.chatList.size();
    }

    @Override
    public ChatGroup getItem(int index) {
        return this.chatList.get(index);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ChatGroup cg = getItem(position);

        if (cg == null) {
            return row;
        }
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.chatgroup_list_item, parent, false);
        TextView header = (TextView) row.findViewById(R.id.header);
        TextView recentMessage = (TextView) row.findViewById(R.id.recent_message);
        header.setText(cg.getName());
        if (cg.getName() == null) {
            Toast.makeText(getContext(), "Null name :(", Toast.LENGTH_SHORT).show();
        }
        if (message != null) {
            recentMessage.setText(message.toString());
        }
        return row;
    }
    public void setRecentMessage(ChatMessage message) {
        this.message = message;
    }
}
