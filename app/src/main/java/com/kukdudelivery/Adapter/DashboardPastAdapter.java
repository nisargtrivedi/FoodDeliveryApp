package com.kukdudelivery.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kukdudelivery.Model.DashboardInfo;
import com.kukdudelivery.R;
import com.kukdudelivery.util.DateUtils;

import java.util.ArrayList;

import static com.kukdudelivery.util.DateUtils.DATE_FORMAT.DISPLAY_FORMAT;
import static com.kukdudelivery.util.DateUtils.DATE_FORMAT.SERVER_FORMAT;
import static com.kukdudelivery.util.DateUtils.DATE_FORMAT.SERVER_FORMAT_FULL;
import static com.kukdudelivery.util.Utility.getCurrencyAmount;
import static com.kukdudelivery.util.Utility.notNullEmpty;

/**
 * Created by andy on 9/01/18.
 */

public class DashboardPastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DashboardInfo> mArrayList;
    private Activity mActivity;

    public DashboardPastAdapter(Activity mActivity, ArrayList<DashboardInfo> mArrayList) {
        this.mActivity = mActivity;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_dashboard_past, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;

            DashboardInfo info = mArrayList.get(position);
            holder.txtOrderCount.setText(info.totalDeliveredOrders);
            holder.txtDate.setText(DateUtils.getFormattedDate(info.date, SERVER_FORMAT, DISPLAY_FORMAT));
            holder.txtAmount.setText(getCurrencyAmount(info.earning));
        }
    }

    @Override
    public int getItemCount() {
        return notNullEmpty(mArrayList) ? mArrayList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderCount, txtDate, txtAmount;

        MyViewHolder(final View itemView) {
            super(itemView);
            txtOrderCount = itemView.findViewById(R.id.txtOrderCount);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
        }
    }
}
