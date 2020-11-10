package com.kukdudelivery.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kukdudelivery.Model.DateFilterInfo;
import com.kukdudelivery.R;

import java.util.ArrayList;


public class SpinnerAdapter extends ArrayAdapter<DateFilterInfo> {

    private Context context;
    private ArrayList<DateFilterInfo> arrayList;

    public SpinnerAdapter(@NonNull Context context, int resource, ArrayList<DateFilterInfo> arrayList) {
        super(context, resource, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View recycle, @NonNull ViewGroup container) {

        TextView textView = (TextView) super.getView(position, recycle, container);
       /* if (position == 0)
            textView.setTextColor(ContextCompat.getColor(context, R.color.hint_color));
        else
            textView.setTextColor(Color.BLACK);*/

        DateFilterInfo beanSpinner = arrayList.get(position);
        textView.setText(beanSpinner.display);
        textView.setTag(beanSpinner.searchDate);

        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        /*if (position == 0)
            textView.setTextColor(ContextCompat.getColor(context, R.color.hint_color));
        else
            textView.setTextColor(Color.BLACK);*/

        int padding = 15;
        textView.setPadding(padding, padding, padding, padding);


        DateFilterInfo beanSpinner = arrayList.get(position);
        textView.setText(beanSpinner.display);
        textView.setTag(beanSpinner.searchDate);
        return textView;
    }

    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return /*position > 0*/true;
    }
}
