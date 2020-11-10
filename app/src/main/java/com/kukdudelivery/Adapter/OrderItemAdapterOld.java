package com.kukdudelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.kukdudelivery.Model.Product_tbl;
import com.kukdudelivery.R;
import com.kukdudelivery.util.TTextView;

import java.util.List;


public class OrderItemAdapterOld extends ArrayAdapter<Product_tbl> {

    List<Product_tbl> items;
    List<Product_tbl> fItems;
    private LayoutInflater inflater;
    Context context;
    String VendorName,orderTotal="0";


    static class ViewHolder{
        TTextView ItemName,VendorName,Weight,Qty,Price;
        RelativeLayout rl;
    }

    public OrderItemAdapterOld(Context context, List<Product_tbl> items, String name, String orderTotal) {
        super(context, R.layout.order_item_row);
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.fItems = items;
        this.context=context;
        VendorName=name;
        this.orderTotal=orderTotal;
    }

    @Override
    public int getCount() {
        return fItems.size();
    }

    @Override
    public Product_tbl getItem(int i) {
        return fItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.order_item_row,null);
            holder.ItemName=(TTextView) convertView.findViewById(R.id.ItemName);
            holder.VendorName=(TTextView) convertView.findViewById(R.id.VendorName);
            holder.Weight=(TTextView) convertView.findViewById(R.id.Weight);
            holder.Qty=(TTextView) convertView.findViewById(R.id.Qty);
            holder.Price=(TTextView) convertView.findViewById(R.id.Price);
            convertView.setTag(holder);


        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        final Product_tbl item=fItems.get(position);
        if(item!=null) {
            holder.ItemName.setText(item.product_name!=null && !item.product_name.isEmpty()?item.product_name:"");
            holder.VendorName.setText(VendorName);
            holder.Price.setText("₹ " + String.format("%.2f", Double.parseDouble(item.product_price)));
            holder.Qty.setText(item.quantity.isEmpty() ? "" : item.quantity);
            holder.Weight.setText(item.weight.isEmpty() ? "" : item.weight);
        }

        return convertView;
    }
}
