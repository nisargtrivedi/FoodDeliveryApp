package com.kukdudelivery.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.kukdudelivery.ApiController.OrderResponse;
import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.OrderDetails;
import com.kukdudelivery.PickOrderInterface;
import com.kukdudelivery.R;
import com.kukdudelivery.util.AppPreferences;
import com.kukdudelivery.util.DateUtils;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.Utility.makeCall;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    public List<Order_tbl> list;
    Activity context;
    public String name;
    AppPreferences appPreferences;
    PickOrderInterface pickOrderInterface;

    public OrderAdapter(Activity context, List<Order_tbl> list) {
        this.context = context;
        this.list = list;
        appPreferences = new AppPreferences(context);
    }


    public void pickOrder(PickOrderInterface listener) {
        pickOrderInterface = listener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public EEditText edtOtp;
        public TTextView OrderStatus, OrderDate, OrderNo, OrderAmount, VendorName, VendorAddress, VendorMobile, DeliverySlot, btnVerify, pickOrder, tvOtp, Status;
        public RelativeLayout menu;

        public ViewHolder(View view) {
            super(view);
            menu = view.findViewById(R.id.menu);
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

            edtOtp = view.findViewById(R.id.edtOtp);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.row_order, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // setFadeAnimation(holder.itemView);

        final Order_tbl task = list.get(position);

        if (task != null) {
            holder.OrderStatus.setText(task.PaymentType.equalsIgnoreCase("online") ? "PAID VIA " + task.PaymentType : "Cash Payment");
            holder.OrderDate.setText(DateUtils.getDateFromTime(task.OrderDate));
            holder.OrderNo.setText("#" + task.OrderNo);
            holder.OrderAmount.setText("₹ " + String.format("%.2f", Double.parseDouble(task.OrderTotal)));
            holder.VendorName.setText(task.VendorName);
            holder.VendorMobile.setText(task.VendorMobile);

            holder.DeliverySlot.setText(String.format("%s(%s)", task.DeliveryTime, task.DeliveryDate));
            holder.Status.setText(task.OrderStatus);

            holder.VendorMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makePhoneCall(task.VendorMobile);
                }
            });

            holder.pickOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        new AlertDialog.Builder(context)
                                .setTitle("Kukdu Delivery")
                                .setMessage("Do you really want to pick order?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        pickOrderInterface.OnPickOrder(task);
                                        //APICALL(holder.btnVerify, holder.edtOtp, task.OrderID,holder.pickOrder);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }

                }
            });

            holder.btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (task.OTP.equalsIgnoreCase(holder.edtOtp.getText().toString().trim())) {
                        invokeVerifyOtp(task.OrderID, appPreferences.getString("USERID"), holder.edtOtp.getText().toString(), task);
                    } else {
                        Toasty.info(context, "OTP not match").show();
                    }
                }
            });

            Log.i("PAGE NAME---->", context.getClass().getName());
            if (context.getClass().getName().equalsIgnoreCase("com.kukdudelivery.MainActivity")) {
                holder.VendorAddress.setText(task.VendorAddress);
                if (task.OrderStatus.equalsIgnoreCase("Processing")) {
                    holder.pickOrder.setVisibility(View.INVISIBLE);
                    holder.tvOtp.setVisibility(View.VISIBLE);
                    holder.btnVerify.setVisibility(View.VISIBLE);
                    holder.edtOtp.setVisibility(View.VISIBLE);
                    if (!task.verifyOTP.equalsIgnoreCase("0")) {
                        holder.tvOtp.setVisibility(View.INVISIBLE);
                        holder.btnVerify.setVisibility(View.INVISIBLE);
                        holder.edtOtp.setVisibility(View.INVISIBLE);
                        holder.pickOrder.setVisibility(View.INVISIBLE);
                        holder.menu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, OrderDetails.class).putExtra("order", task));
                            }
                        });

                    } else {
                        holder.tvOtp.setVisibility(View.VISIBLE);
                        holder.btnVerify.setVisibility(View.VISIBLE);
                        holder.edtOtp.setVisibility(View.VISIBLE);
                        holder.pickOrder.setVisibility(View.INVISIBLE);
                    }
                    // holder.DeliverySlot.setText(task.orderProcessingTime+"(Order Processing)");
                } else if (task.OrderStatus.equalsIgnoreCase("Delivered")) {
                    //holder.DeliverySlot.setText(task.orderDeliveryTime +"(Order Delivered)");
                    holder.tvOtp.setVisibility(View.INVISIBLE);
                } else if (task.OrderStatus.equalsIgnoreCase("Approved")) {
                    // holder.DeliverySlot.setText(task.orderApprovalTime +"(Order Approved)");
                    holder.tvOtp.setVisibility(View.INVISIBLE);
                } else {
                    holder.pickOrder.setVisibility(View.VISIBLE);
                    holder.tvOtp.setVisibility(View.INVISIBLE);
                    holder.btnVerify.setVisibility(View.INVISIBLE);
                    holder.edtOtp.setVisibility(View.INVISIBLE);
                }
            } else if (context.getClass().getName().equalsIgnoreCase("com.kukdudelivery.ActivitySearch")) {
                holder.VendorAddress.setText(task.VendorAddress);
                if (task.OrderStatus.equalsIgnoreCase("Processing")) {
                    holder.pickOrder.setVisibility(View.INVISIBLE);
                    holder.tvOtp.setVisibility(View.VISIBLE);
                    holder.btnVerify.setVisibility(View.VISIBLE);
                    holder.edtOtp.setVisibility(View.VISIBLE);
                    if (!task.verifyOTP.equalsIgnoreCase("0")) {
                        holder.tvOtp.setVisibility(View.INVISIBLE);
                        holder.btnVerify.setVisibility(View.INVISIBLE);
                        holder.edtOtp.setVisibility(View.INVISIBLE);
                        holder.pickOrder.setVisibility(View.INVISIBLE);
                        holder.menu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context, OrderDetails.class).putExtra("order", task));
                            }
                        });

                    } else {
                        holder.tvOtp.setVisibility(View.VISIBLE);
                        holder.btnVerify.setVisibility(View.VISIBLE);
                        holder.edtOtp.setVisibility(View.VISIBLE);
                        holder.pickOrder.setVisibility(View.INVISIBLE);
                    }
                    // holder.DeliverySlot.setText(task.orderProcessingTime+"(Order Processing)");
                } else if (task.OrderStatus.equalsIgnoreCase("Delivered")) {
                    //holder.DeliverySlot.setText(task.orderDeliveryTime +"(Order Delivered)");
                    holder.tvOtp.setVisibility(View.INVISIBLE);
                } else if (task.OrderStatus.equalsIgnoreCase("Approved")) {
                    // holder.DeliverySlot.setText(task.orderApprovalTime +"(Order Approved)");
                    holder.tvOtp.setVisibility(View.INVISIBLE);
                } else {
                    holder.pickOrder.setVisibility(View.VISIBLE);
                    holder.tvOtp.setVisibility(View.INVISIBLE);
                    holder.btnVerify.setVisibility(View.INVISIBLE);
                    holder.edtOtp.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.VendorAddress.setText(task.VendorAddress);
                holder.OrderStatus.setText(task.PaymentType.equalsIgnoreCase("online") ? "PAID VIA " + task.PaymentType : "Cash Payment");
                holder.DeliverySlot.setText(task.orderDeliveryTime + "(Order Delivered)");
                holder.btnVerify.setVisibility(View.GONE);
                holder.pickOrder.setVisibility(View.GONE);
                holder.tvOtp.setVisibility(View.GONE);
                //holder.VendorAddress.setText(task.shippingAddress+","+task.shippingArea+","+task.shippingZipcode+","+task.shippingCity+","+task.shippingCountry);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }


    private void invokeVerifyOtp(String order_id, String userid, String otp, final Order_tbl order_tbl) {
        try {
            if (!Utility.isNetworkAvailable(context)) {
                Toasty.info(context, "please check internet connection").show();
            } else {
                Utility.showProgress(context);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<OrderResponse> responseCall = login.verifyOTP(userid, order_id, otp);
                responseCall.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            OrderResponse memberResponse = response.body();
                            if (memberResponse.isValid()) {
                                Toasty.info(context, memberResponse.ResponseMsg).show();
                                context.startActivity(new Intent(context, OrderDetails.class).putExtra("order", order_tbl));
                            } else {
                                Toasty.info(context, memberResponse.ResponseMsg).show();
                            }
                        } else {
                            Toasty.info(context, "OTP not verify").show();
                        }

                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Toasty.info(context, "please contact admin").show();
                    }
                });

            }
        } catch (Exception ex) {
            Toasty.info(context, ex.toString());
        }
    }


    private void makePhoneCall(String no) {
        makeCall(context, no);
    }

}
