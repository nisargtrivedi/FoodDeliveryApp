package com.kukdudelivery.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kukdudelivery.Model.Product_tbl;
import com.kukdudelivery.R;
import com.kukdudelivery.util.TTextView;

import java.util.ArrayList;

import static com.kukdudelivery.util.Utility.getCurrencyAmount;
import static com.kukdudelivery.util.Utility.notNullEmpty;

/**
 * Created by andy on 9/01/18.
 */

public class OrderItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Product_tbl> mArrayList;
    private String VendorName;

    public OrderItemAdapter(ArrayList<Product_tbl> mArrayList, String name) {
        this.mArrayList = mArrayList;
        VendorName = name;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_re_order_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;

            Product_tbl item = mArrayList.get(position);
            if (item != null) {
                holder.ItemName.setText(item.product_name != null && !item.product_name.isEmpty() ? item.product_name : "");
                holder.VendorName.setText(VendorName);
                holder.Price.setText(getCurrencyAmount(item.product_price));
                holder.Qty.setText(item.quantity);
                holder.Weight.setText(item.weight);
            }

        }
    }

    @Override
    public int getItemCount() {
        return notNullEmpty(mArrayList) ? mArrayList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TTextView ItemName, VendorName, Weight, Qty, Price;

        MyViewHolder(final View itemView) {
            super(itemView);

            ItemName = itemView.findViewById(R.id.ItemName);
            VendorName = itemView.findViewById(R.id.VendorName);
            Weight = itemView.findViewById(R.id.Weight);
            Qty = itemView.findViewById(R.id.Qty);
            Price = itemView.findViewById(R.id.Price);
        }
    }
}