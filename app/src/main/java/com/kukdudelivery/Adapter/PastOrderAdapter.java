package com.kukdudelivery.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.R;
import com.kukdudelivery.util.DateUtils;

import java.util.ArrayList;

import static com.kukdudelivery.util.Utility.getCurrencyAmount;
import static com.kukdudelivery.util.Utility.notNullEmpty;
import static com.kukdudelivery.util.Utility.spannableText;

/**
 * Created by andy on 9/01/18.
 */

public class PastOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Order_tbl> mArrayList;
    private Activity mActivity;
    private final int ITEM_DATA = 1;
    private final int ITEM_PROGRESS = 2;
    private PaymentListener listener;

    public void setListener(PaymentListener listener) {
        this.listener = listener;
    }

    public interface PaymentListener {
        void onPayNow(Order_tbl orderTbl);
    }

    public PastOrderAdapter(Activity mActivity, ArrayList<Order_tbl> mArrayList) {
        this.mActivity = mActivity;
        this.mArrayList = mArrayList;
    }

    public void updateList(ArrayList<Order_tbl> mArrayList) {
        this.mArrayList = mArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       /* return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_past_order, parent, false));*/
        if (viewType == ITEM_DATA)
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_past_order, parent, false));
        else
            return new ProgressHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_progress, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return mArrayList.get(position) == null ? ITEM_PROGRESS : ITEM_DATA;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;

            final Order_tbl orderTbl = mArrayList.get(position);
            boolean isCOD = !orderTbl.PaymentType.equalsIgnoreCase("online");
            boolean isPending = orderTbl.deliveryPaymentStatus.equalsIgnoreCase("Pending");

            String status = isCOD ? "Cash Payment" : "PAID VIA " + orderTbl.PaymentType;
            holder.txtOrderStatus.setText(status);

            holder.btnPay.setVisibility(isCOD && isPending ? View.VISIBLE : View.GONE);
            holder.btnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPayNow(orderTbl);
                }
            });

            holder.txtPaidVia.setVisibility(isCOD && !isPending ? View.VISIBLE : View.GONE);
            holder.txtPaidVia.setText(String.format("Paid Via\n%s", orderTbl.deliveryPaymentVia));

            Log.e("IS_PAID_VIA::", String.format("ORDER_ID::%s, IS_COD::%s, IS_PENDING::%s", orderTbl.OrderID, isCOD, isPending));

            int color = isCOD && isPending ? R.color.red_theme_color : R.color.green;
            holder.txtOrderAmount.setTextColor(ContextCompat.getColor(mActivity, color));

            holder.txtDate.setText(DateUtils.getDateFromTime(orderTbl.OrderDate));
            holder.txtOrderNo.setText(String.format("#%s", orderTbl.OrderNo));
            holder.txtOrderAmount.setText(getCurrencyAmount(orderTbl.paidAmount));
            holder.txtVendorName.setText(orderTbl.VendorName);
            holder.txtArea.setText(orderTbl.shippingArea);

            String orderDate = String.format("%s(%s)", orderTbl.DeliveryTime, orderTbl.DeliveryDate);
            spannableText(mActivity, mActivity.getString(R.string.order_date), orderDate, holder.txtOrderDate);
            spannableText(mActivity, mActivity.getString(R.string.delivered_date), orderTbl.orderDeliveryTime, holder.txtDeliveryDate);
        }
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return notNullEmpty(mArrayList) ? Long.parseLong(mArrayList.get(position).OrderID) : position;
    }

    @Override
    public int getItemCount() {
        return notNullEmpty(mArrayList) ? mArrayList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderStatus, txtDate, txtOrderNo, txtOrderAmount, txtVendorName, txtOrderDate, txtDeliveryDate, txtArea,
                txtPaidVia;
        Button btnPay;

        MyViewHolder(final View itemView) {
            super(itemView);
            btnPay = itemView.findViewById(R.id.btnPay);
            txtPaidVia = itemView.findViewById(R.id.txtPaidVia);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtOrderNo = itemView.findViewById(R.id.txtOrderNo);
            txtOrderAmount = itemView.findViewById(R.id.txtOrderAmount);
            txtVendorName = itemView.findViewById(R.id.txtVendorName);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtDeliveryDate = itemView.findViewById(R.id.txtDeliveryDate);
            txtArea = itemView.findViewById(R.id.txtArea);
        }
    }
}
