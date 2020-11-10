package com.kukdudelivery.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.OrderDetails;
import com.kukdudelivery.R;
import com.kukdudelivery.util.DateUtils;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;

import org.w3c.dom.Text;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.kukdudelivery.util.Utility.getCurrencyAmount;
import static com.kukdudelivery.util.Utility.makeCall;
import static com.kukdudelivery.util.Utility.notNullEmpty;
import static com.kukdudelivery.util.Utility.spannableText;

/**
 * Created by andy on 9/01/18.
 */

public class RecentOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Order_tbl> mArrayList;
    private Activity mActivity;
    private ItemClickListener itemClickListener;

    public interface OrderStatus {
        String PROCESSING = "processing";
        String DELIVERED = "delivered";
        String APPROVED = "approved";
//        String PROCESSING = "processing";
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onOrderPick(Order_tbl orderTbl);

        void onVerifyOtp(String otp, Order_tbl orderTbl);
    }

    public RecentOrderAdapter(Activity mActivity, ArrayList<Order_tbl> mArrayList) {
        this.mActivity = mActivity;
        this.mArrayList = mArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MyViewHolder) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
            final Order_tbl task = mArrayList.get(position);
            if (task != null) {
                holder.OrderStatus.setText(task.PaymentType.equalsIgnoreCase("online") ?
                        "PAID VIA " + task.PaymentType : "Cash Payment");
                holder.OrderDate.setText(DateUtils.getDateFromTime(task.OrderDate));
                holder.OrderNo.setText(String.format("#%s", task.OrderNo));
                holder.OrderAmount.setText(getCurrencyAmount(task.paidAmount));
                holder.VendorName.setText(task.VendorName);
                holder.VendorAddress.setText(task.VendorAddress);
                holder.VendorMobile.setText(task.VendorMobile);
                holder.DeliverySlot.setText(String.format("%s(%s)", task.DeliveryTime, task.DeliveryDate));
                holder.Status.setText(task.OrderStatus);
                holder.VendorMobile.setMovementMethod(LinkMovementMethod.getInstance());
                holder.txtArea.setText(task.shippingArea);

                holder.pickOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.Alert(mActivity, "Do you really want to pick order?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                itemClickListener.onOrderPick(task);
                            }
                        });
                    }
                });

                holder.btnVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (task.OTP.equalsIgnoreCase(holder.edtOtp.getText().toString().trim())) {
                            itemClickListener.onVerifyOtp(holder.edtOtp.getText().toString(), task);
                        } else {
                            Toasty.info(mActivity, "OTP not match").show();
                        }
                    }
                });

                final boolean hasDetails = task.OrderStatus.equalsIgnoreCase(OrderStatus.PROCESSING) && !task.verifyOTP.equals("0");
                holder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hasDetails)
                            mActivity.startActivity(new Intent(mActivity, OrderDetails.class)
                                    .putExtra("order", task));
                    }
                });

                switch (task.OrderStatus.toLowerCase()) {
                    case OrderStatus.PROCESSING:
                        boolean hasNoOtp = !task.verifyOTP.equalsIgnoreCase("0");
                        showOTP(hasNoOtp, holder);
                        holder.pickOrder.setVisibility(hasNoOtp ? View.GONE : View.INVISIBLE);
                        break;
                    case OrderStatus.DELIVERED:
                        holder.pickOrder.setVisibility(View.GONE);
                        showOTP(true, holder);
                    case OrderStatus.APPROVED:
                        holder.pickOrder.setVisibility(View.VISIBLE);
                        showOTP(true, holder);
                        break;
                    default:
                        showOTP(true, holder);
                        holder.pickOrder.setVisibility(View.VISIBLE);
                        break;
                }

            }
        }
    }

    private void showOTP(boolean hasNoOtp, MyViewHolder holder) {
        holder.tvOtp.setVisibility(hasNoOtp ? View.GONE : View.VISIBLE);
        holder.btnVerify.setVisibility(hasNoOtp ? View.GONE : View.VISIBLE);
        holder.edtOtp.setVisibility(hasNoOtp ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return notNullEmpty(mArrayList) ? mArrayList.size() : 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        EEditText edtOtp;
        TTextView OrderStatus, OrderDate, OrderNo, OrderAmount, VendorName, VendorAddress, VendorMobile,
                DeliverySlot, btnVerify, pickOrder, tvOtp, Status;
        RelativeLayout menu;
        TextView txtArea;

        MyViewHolder(final View view) {
            super(view);
            OrderStatus = view.findViewById(R.id.OrderStatus);
            OrderDate = view.findViewById(R.id.OrderDate);
            OrderNo = view.findViewById(R.id.OrderNo);
            OrderAmount = view.findViewById(R.id.OrderAmount);
            VendorName = view.findViewById(R.id.VendorName);
            VendorAddress = view.findViewById(R.id.VendorAddress);
            VendorName = view.findViewById(R.id.VendorName);

            VendorMobile = view.findViewById(R.id.VendorMobile);
            DeliverySlot = view.findViewById(R.id.DeliverySlot);
            btnVerify = view.findViewById(R.id.btnVerify);
            pickOrder = view.findViewById(R.id.pickOrder);
            tvOtp = view.findViewById(R.id.tvOtp);
            Status = view.findViewById(R.id.Status);
            txtArea = itemView.findViewById(R.id.txtArea);

            edtOtp = view.findViewById(R.id.edtOtp);
            menu = view.findViewById(R.id.menu);
        }
    }
}
