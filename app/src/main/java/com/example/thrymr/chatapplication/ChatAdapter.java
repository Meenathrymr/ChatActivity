package com.example.thrymr.chatapplication;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import models.ChatMessage;

/**
 * Created by thrymr on 18/5/16.
 */
public class ChatAdapter extends BaseAdapter {


    private Context mContext;
    private List<ChatMessage> mMessages;
    private LinearLayout singleMessageContainer;

    public ChatAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public ChatAdapter(Context context, List<ChatMessage> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = (ChatMessage) this.getItem(position);
        Log.d("ChatAdapter", "message" + message);

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_single_item, parent, false);
            holder.message = (TextView) convertView.findViewById(R.id.singleMessage);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.message.setText(message.getMessage());

        singleMessageContainer = (LinearLayout) convertView.findViewById(R.id.singleMessageContainer);
        singleMessageContainer.setGravity(message.isMine() ? Gravity.LEFT : Gravity.RIGHT);

        return convertView;
    }

    private static class ViewHolder {
        TextView message;
    }

    @Override
    public long getItemId(int position) {
        //Unimplemented, because we aren't using Sqlite.
        return position;
    }

    void setNotify(List<ChatMessage> newMesageList) {
        mMessages = newMesageList;
        notifyDataSetChanged();
    }

}
