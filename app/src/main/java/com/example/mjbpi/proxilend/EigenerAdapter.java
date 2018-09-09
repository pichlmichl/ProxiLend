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

class EigenerAdapter extends ArrayAdapter<Entry> {


    public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

    private String mNameItem;
    private Map<String, User> mUserMap;

    public static final String OFFER_TYPE = "OFFER";
    public static final String REQUEST_TYPE = "REQUEST";

    private ArrayList<Entry> mEntryArrayList;

    EigenerAdapter(@NonNull Context context, ArrayList<Entry> entryArrayList, Map<String, User> userMap) {
        super(context, R.layout.custom_list_item, entryArrayList);

        mEntryArrayList = entryArrayList;
        mUserMap = userMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_list_item, parent, false);

        Entry entry = mEntryArrayList.get(position);
        String nameSimpleItem = entry.getName();
        String userNameItem = "Unknown...";
        User user = mUserMap.get(entry.getId());

        String type = entry.getType();

        if (user != null){
            userNameItem = user.getUserName();
        }

        // Dann wird das vorgesehene TextView mit dem Datum gesetzt
        TextView dateView = (TextView) customView.findViewById(R.id.date_text);
        dateView.setText(calculateDate(entry));

        TextView username = (TextView) customView.findViewById(R.id.user_name);
        username.setText(userNameItem);

        TextView distanceView = (TextView) customView.findViewById(R.id.distance_text);
        distanceView.setText("Entfernung: " + entry.getDistance() + "m");
        distanceView.setGravity(Gravity.CENTER);

        TextView name = (TextView) customView.findViewById(R.id.name_entry);
        name.setText(nameSimpleItem);
        name.setGravity(Gravity.CENTER);

        TextView typeView = (TextView) customView.findViewById(R.id.type);
        TextView typeArrow = (TextView) customView.findViewById(R.id.type_arrow);

        if (type != null){
            if (type.equals(OFFER_TYPE)){
                typeView.setText(R.string.offer_display);
                typeArrow.setText(R.string.offer_arrow);
                typeArrow.setTextColor(ContextCompat.getColor(getContext(), R.color.lightGreen));
            } else {
                typeView.setText(R.string.request_display);
                typeArrow.setText(R.string.request_arrow);
                typeArrow.setTextColor(ContextCompat.getColor(getContext(), R.color.lightRed));
            }
        } else {
            typeView.setText(R.string.error);
        }
        return customView;
    }

    public void setNameItem(String nameItem) {
        mNameItem = nameItem;
    }

    private String calculateDate(Entry entry) {
        // DieseMethode holt sich die aktuelle Zeit vom aktuellen Objekt und prüft
        // ob das Erstelldatum schon über 24 Stunden alt ist
        String date;
        if (System.currentTimeMillis() - entry.getCreationDate() < 1000 * 3600 * 24) {
            // wenn die Erstellmillisekunden jünger als ein Tag alt sind, wird die Zeit als Uhrzeit angezeigt
            date = RECENT_FORMAT.format(new Date(entry.getCreationDate()));
        } else {
            // Wenn die Erstellmillisekunden älter als ein Tag sind, wird die Zeit als Datum angezeigt
            date = OLD_FORMAT.format(new Date(entry.getCreationDate()));
        }
        return date;
    }

}
