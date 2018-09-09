package com.example.mjbpi.proxilend;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class ChatAdapter extends ArrayAdapter<ChatMessage> {


    public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

    private Map<String, User> mUserMap;

    public static final String OFFER_TYPE = "OFFER";
    public static final String REQUEST_TYPE = "REQUEST";

    private ArrayList<ChatMessage> mChatArrayList;

    ChatAdapter(@NonNull Context context, ArrayList<ChatMessage> chatArrayList, Map<String, User> userMap) {
        super(context, R.layout.chat_message, chatArrayList);

        mChatArrayList = chatArrayList;
        mUserMap = userMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.chat_message, parent, false);

        ChatMessage message = mChatArrayList.get(position);
        String name = message.getName();
        String messageText = message.getMessage();
        User user = mUserMap.get(message.getId());


        // Dann wird das vorgesehene TextView mit dem Datum gesetzt
        TextView dateView = (TextView) customView.findViewById(R.id.date_text_chat);
        dateView.setText("(" + calculateDate(message) + ")" );

        TextView username = (TextView) customView.findViewById(R.id.user_name_chat);
        username.setText(name + ": ");

        TextView messageTextView = (TextView) customView.findViewById(R.id.text_message_chat);
        messageTextView.setText(messageText);


        return customView;
    }

    private String calculateDate(ChatMessage message) {
        // DieseMethode holt sich die aktuelle Zeit vom aktuellen Objekt und prüft
        // ob das Erstelldatum schon über 24 Stunden alt ist
        String date;
        if (System.currentTimeMillis() - message.getCreationDate() < 1000 * 3600 * 24) {
            // wenn die Erstellmillisekunden jünger als ein Tag alt sind, wird die Zeit als Uhrzeit angezeigt
            date = RECENT_FORMAT.format(new Date(message.getCreationDate()));
        } else {
            // Wenn die Erstellmillisekunden älter als ein Tag sind, wird die Zeit als Datum angezeigt
            date = OLD_FORMAT.format(new Date(message.getCreationDate()));
        }
        return date;
    }

}
