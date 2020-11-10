package com.kukdudelivery;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kukdudelivery.Adapter.OrderItemAdapter;
import com.kukdudelivery.ApiController.UpdateOrderResponse;
import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.WebApi.WebUtility;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.Utility.addDivider;
import static com.kukdudelivery.util.Utility.getCurrencyAmount;
import static com.kukdudelivery.util.Utility.notNullEmpty;

public class OrderDetails extends BaseActivity {

    TTextView tvAddress1, tvAddress2, tvItems, tvBack, btnreorder, btnDelivered, tvStatus, Price, phone, phone2;
    RecyclerView lvProducts;
    Order_tbl order_tbl;

    LinearLayout ll_sp_comments;
    TextView txt_sp_comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        tvAddress1 = findViewById(R.id.tvAddress1);
        tvAddress2 = findViewById(R.id.tvAddress2);
        tvItems = findViewById(R.id.tvItems);
        lvProducts = findViewById(R.id.lvProducts);
        tvBack = findViewById(R.id.tvBack);
        btnreorder = findViewById(R.id.btnreorder);
        btnDelivered = findViewById(R.id.btnDelivered);
        tvStatus = findViewById(R.id.tvStatus);
        Price = findViewById(R.id.Price);
        phone = findViewById(R.id.phone);
        phone2 = findViewById(R.id.phone2);

        ll_sp_comments = findViewById(R.id.ll_sp_comments);
        txt_sp_comments = findViewById(R.id.txt_sp_comments);

        order_tbl = (Order_tbl) getIntent().getSerializableExtra("order");

        tvAddress1.setText(order_tbl.VendorAddress);
        tvAddress2.setText(order_tbl.CustomerAddres);
        phone.setText(order_tbl.VendorMobile);

        if (notNullEmpty(order_tbl.shippingMobile))
            phone2.setText(String.format("%s,\n%s", order_tbl.customerPhone, order_tbl.shippingMobile));
        else
            phone2.setText(order_tbl.customerPhone);

        phone.setMovementMethod(LinkMovementMethod.getInstance());
        phone2.setMovementMethod(LinkMovementMethod.getInstance());

        //sendMail("SEND MAIL");
       /* phone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!order_tbl.customerPhone.isEmpty())
                    makePhoneCall(order_tbl.customerPhone);
            }
        });


        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!order_tbl.VendorMobile.isEmpty())
                    makePhoneCall(order_tbl.VendorMobile);
            }
        });*/
        if (order_tbl.PaymentType.equalsIgnoreCase("online"))
            tvStatus.setText("No payment needed");
        else
            tvStatus.setText(String.format("Collect amount from customer : %s", getCurrencyAmount(order_tbl.OrderTotal)));

        tvItems.setText(String.format(Locale.US, "#%s (%s|%d items ₹%s",
                order_tbl.OrderNo, order_tbl.OrderStatus, order_tbl.productTbls.size(), order_tbl.OrderTotal));

        Price.setText(String.format("Total Order Amount : %s", getCurrencyAmount(order_tbl.OrderTotal)));


        if (notNullEmpty(order_tbl.orderComment)) {
            ll_sp_comments.setVisibility(View.VISIBLE);
            txt_sp_comments.setText(order_tbl.orderComment);
        } else
            ll_sp_comments.setVisibility(View.GONE);

        addDivider(OrderDetails.this, lvProducts);
        lvProducts.setAdapter(new OrderItemAdapter(order_tbl.productTbls, order_tbl.VendorName));

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnreorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mapPath = order_tbl.MapLink;
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(mapPath));
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Utility.showToast("Map application is not installed", OrderDetails.this);
                    }
                } catch (Exception ex) {
                    Utility.showToast("Map application is not installed", OrderDetails.this);
                }
            }
        });


        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (order_tbl.PaymentType.equalsIgnoreCase("online")) {
                    if(order_tbl.PaymentStatus.equalsIgnoreCase("pending")){
                        try {
                            new AlertDialog.Builder(OrderDetails.this, R.style.YourAlertDialogTheme)
                                    .setTitle("Kukdu Delivery")
                                    .setMessage(Html.fromHtml("<font color='#123456'>Please collect the cash from customer and click ok to move forward</font>"))
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            if (!order_tbl.OrderStatus.equalsIgnoreCase("online")) {

                                                new AlertDialog.Builder(OrderDetails.this, R.style.YourAlertDialogTheme2)
                                                        .setTitle("Kukdu Delivery")
                                                        .setMessage("Please handover parcel to customer and complete your order delivery")
                                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                                APICALL();
                                                            }
                                                        })
                                                        .setNegativeButton(android.R.string.no, null).show();


                                            } else
                                                APICALL();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        } catch (Exception ex) {
                            ex.toString();
                        }

                    }
                    else{
                        new AlertDialog.Builder(OrderDetails.this, R.style.YourAlertDialogTheme2)
                                .setTitle("Kukdu Delivery")
                                .setMessage(Html.fromHtml("<font color='#FF7F27'>Please handover parcel to customer and complete your order delivery</font>"))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(order_tbl.TransactionID.startsWith("pay_") || order_tbl.TransactionID.startsWith("PAY_")){
                                                        PaymentCapture();
                                                        //Toast.makeText(OrderDetails.this,"CALLED CAPTURE",Toast.LENGTH_LONG).show();
                                                }else{
                                                    //Toast.makeText(OrderDetails.this,"CALLED ELSE",Toast.LENGTH_LONG).show();
                                                        APICALL();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();

                    }
                } else {

                    try {
                        new AlertDialog.Builder(OrderDetails.this, R.style.YourAlertDialogTheme)
                                .setTitle("Kukdu Delivery")
                                .setMessage(Html.fromHtml("<font color='#123456'>Please collect the cash from customer and click ok to move forward</font>"))
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        if (!order_tbl.OrderStatus.equalsIgnoreCase("online")) {

                                            new AlertDialog.Builder(OrderDetails.this, R.style.YourAlertDialogTheme2)
                                                    .setTitle("Kukdu Delivery")
                                                    .setMessage("Please handover parcel to customer and complete your order delivery")
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            APICALL();
                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.no, null).show();


                                        } else
                                            APICALL();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    } catch (Exception ex) {
                        ex.toString();
                    }

                }

            }
        });

    }

    private void APICALL() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(OrderDetails.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<UpdateOrderResponse> responseCall = login.updateOrders(appPreferences.getString("USERID"), order_tbl.OrderID, "Delivered");
                responseCall.enqueue(new Callback<UpdateOrderResponse>() {
                    @Override
                    public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            UpdateOrderResponse loginResponse = response.body();
                            if (loginResponse.isValid()) {
                                showSuccessMsg(loginResponse.ResponseMsg);
                                btnDelivered.setEnabled(false);
                                finishAffinity();
                                startActivity(new Intent(OrderDetails.this, CompleteOrdersActivity.class));
                            } else {
                                showErrorMsg(loginResponse.ResponseMsg);
                            }
                        } else {
                            showInfoMsg(response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please contact admin");
                    }
                });
            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
        }
    }
    RazorpayClient razorpayClient;

    private void PaymentCapture(){
        try {

            razorpayClient=new RazorpayClient(WebUtility.RAZORPAY_LIVE,WebUtility.RAZORPAY_LIVE_SECRET);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

//            HashMap<String,String> value= new HashMap<String, String>();
//            value.put("content-type","application/json");
//            razorpayClient.addHeaders(value);
//            JSONObject orderRequest = new JSONObject();
//            orderRequest.put("amount", 50000); // amount in the smallest currency unit
//            orderRequest.put("currency", "INR");
//            orderRequest.put("receipt", "order_rcptid_11");
//            orderRequest.put("payment_capture", false);
//
//            Order order = razorpayClient.Orders.create(orderRequest);
//
//
//
//
//            Log.i("ORDER ID======>",order.toString());
//            Log.i("ORDER NUMBER======>",order.get("id")+"");
//            OrderID=order.get("id")+"";

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject captureRequest = new JSONObject();
                        captureRequest.put("amount", (Integer.parseInt(order_tbl.paidAmount)*100));
                        captureRequest.put("currency", "INR");

                        //System.out.println("AMOUNT------------->"+(order_tbl.OrderTotal));
                        Payment payment = razorpayClient.Payments.capture(order_tbl.TransactionID, captureRequest);

                        if(payment!=null) {
                            if (payment.get("status") != null && Utility.notNullEmpty(payment.get("status").toString())) {
                                String status = payment.get("status").toString();
                                if (status.equalsIgnoreCase("captured")) {
                                    APICALL();
                                }
                            }
                        }else{
                            APICALL();
                        }
//                {"id":"pay_F9AptDNy6QbDZW","entity":"payment","amount":29100,"currency":"INR","status":"captured","order_id":null,"invoice_id":null,"international":false,"method":"card","amount_refunded":0,"refund_status":null,"captured":true,"description":"Food Order,546","card_id":"card_F9AptH2zZv40Th","card":{"id":"card_F9AptH2zZv40Th","entity":"card","name":"Test Service Center","last4":"1111","network":"Visa","type":"debit","issuer":null,"international":false,"emi":false},"bank":null,"wallet":null,"vpa":null,"email":"nisarg.trivedi1192@gmail.com","contact":"+919978538694","notes":[],"fee":582,"tax":0,"error_code":null,"error_description":null,"error_source":null,"error_step":null,"error_reason":null,"created_at":1593593922}

                        //Log.i("PAYMENT======>",payment.toString());
                    } catch (Exception e) {
                        // Handle Exception
                        System.out.println("ERROR---------->"+e.toString());
                        sendMail(e.toString());
                        APICALL();

                    }
                }
            });



//            I/ORDER ID======>: {"id":"order_F8on060xPLIFsG","entity":"order","amount":50000,"amount_paid":0,"amount_due":50000,"currency":"INR","receipt":"order_rcptid_11","offer_id":null,"status":"created","attempts":0,"notes":[],"created_at":1593516283}
//            order_F8on060xPLIFsG
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMsg(e.toString());
        }
    }
}
