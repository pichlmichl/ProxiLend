package com.example.mjbpi.proxilend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

class EigenerAdapter extends ArrayAdapter<Offer> {


    public static final SimpleDateFormat RECENT_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat OLD_FORMAT = new SimpleDateFormat("dd. MMMM");

    private String mNameItem;
    private Map<String, User> mUserMap;

    ArrayList<Offer> mOfferArrayList;

    EigenerAdapter(@NonNull Context context, ArrayList<Offer> offerArrayList, Map<String, User> userMap) {
        super(context, R.layout.custom_list_item, offerArrayList);

        mOfferArrayList = offerArrayList;
        mUserMap = userMap;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_list_item, parent, false);

        Offer offer = mOfferArrayList.get(position);
        String nameSimpleItem = offer.getName();
        String userNameItem = "Unknown...";
        User user = mUserMap.get(offer.getId());
        if (user != null){
            userNameItem = user.getUserName();
        }

        // Dann wird das vorgesehene TextView mit dem Datum gesetzt
        TextView dateView = (TextView) customView.findViewById(R.id.date_text);
        dateView.setText(calculateDate(offer));

        TextView username = (TextView) customView.findViewById(R.id.user_name);
        username.setText(userNameItem);

        TextView name = (TextView) customView.findViewById(R.id.name_entry);
        name.setText(nameSimpleItem);
        return customView;
    }

    public void setNameItem(String nameItem) {
        mNameItem = nameItem;
    }

    private String calculateDate(Offer offer) {
        // DieseMethode holt sich die aktuelle Zeit vom aktuellen Objekt und prüft
        // ob das Erstelldatum schon über 24 Stunden alt ist
        String date;
        if (System.currentTimeMillis() - offer.getCreationDate() < 1000 * 3600 * 24) {
            // wenn die Erstellmillisekunden jünger als ein Tag alt sind, wird die Zeit als Uhrzeit angezeigt
            date = RECENT_FORMAT.format(new Date(offer.getCreationDate()));
        } else {
            // Wenn die Erstellmillisekunden älter als ein Tag sind, wird die Zeit als Datum angezeigt
            date = OLD_FORMAT.format(new Date(offer.getCreationDate()));
        }
        return date;
    }

}
