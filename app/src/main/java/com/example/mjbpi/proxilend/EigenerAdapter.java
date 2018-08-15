package com.example.mjbpi.proxilend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class EigenerAdapter extends ArrayAdapter<Offer> {

    ArrayList<Offer> offerArrayList;

    EigenerAdapter(@NonNull Context context, ArrayList<Offer> offerArrayList) {
        super(context, R.layout.custom_list_item, offerArrayList);

        this.offerArrayList = offerArrayList;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_list_item, parent, false);

        Offer simpleOffer = offerArrayList.get(position);
        String nameSimpleItem = simpleOffer.getName();

        TextView name = (TextView) customView.findViewById(R.id.name_entry);
        name.setText(nameSimpleItem);
        return customView;
    }
}
