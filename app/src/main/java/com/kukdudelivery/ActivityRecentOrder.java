package com.kukdudelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kukdudelivery.Adapter.RecentOrderAdapter;
import com.kukdudelivery.ApiController.OrderResponse;
import com.kukdudelivery.ApiController.UpdateOrderResponse;
import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRecentOrder extends BaseActivity implements RecentOrderAdapter.ItemClickListener {

    RecyclerView RvOrder;
    TTextView tvTotalOrder, tvUserName, noorder, ordermsg;

    ArrayList<Order_tbl> orderTbls = new ArrayList<>();
    RecentOrderAdapter adapter;
    Activity mActivity;

    public static int page = 1;
    public static int TotalPage = 0;

    private void goTo(Class<?> cls) {
        if (cls == MainActivity.class)
            finishAffinity();
        else
            finish();

        startActivity(new Intent(mActivity, cls));
    }

    private void initBottom() {
        ((TextView) findViewById(R.id.tvRecent)).
                setTextColor(ContextCompat.getColor(mActivity, R.color.red_theme_color));

        findViewById(R.id.ic_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(MainActivity.class);
            }
        });
        findViewById(R.id.tvhome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(MainActivity.class);
            }
        });

        findViewById(R.id.ic_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(ActivityAccount.class);
            }
        });
        findViewById(R.id.tvAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(ActivityAccount.class);
            }
        });

        findViewById(R.id.ic_past).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(CompleteOrdersActivity.class);
            }
        });
        findViewById(R.id.tvDelivered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(CompleteOrdersActivity.class);
            }
        });

        findViewById(R.id.explore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, ActivitySearch.class));
            }
        });
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall(appPreferences.getString("HELP"));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        mActivity = this;
        try {
            initBottom();
            RvOrder = findViewById(R.id.RvORders);
            tvTotalOrder = findViewById(R.id.tvTotalOrder);
            tvUserName = findViewById(R.id.tvUserName);
            noorder = findViewById(R.id.noorder);
            ordermsg = findViewById(R.id.ordermsg);


            adapter = new RecentOrderAdapter(this, orderTbls);
            RvOrder.setLayoutManager(new LinearLayoutManager(this));
            RvOrder.setAdapter(adapter);
            adapter.setItemClickListener(this);
            ordermsg.setText("Recent Order");


            tvUserName.setText(String.format("Welcome %s", appPreferences.getString("USERNAME")));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        APICALL();
    }

    @Override
    protected void onStart() {
        super.onStart();
        page = 1;
        TotalPage = 0;

    }

    @Override
    protected void onStop() {
        super.onStop();
        page = 1;
        TotalPage = 0;

    }


    private void APICALL() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(ActivityRecentOrder.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<OrderResponse> responseCall = login.listOrders(appPreferences.getString("USERID"), "1");
                responseCall.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            //System.out.println("ORDERS--->"+response.body().toString());
                            OrderResponse loginResponse = response.body();
                            if (loginResponse.isValid()) {
                                orderTbls.clear();
                                orderTbls.addAll(loginResponse.orderTbls);
                                adapter.notifyDataSetChanged();
                                tvTotalOrder.setText(orderTbls.size() + " Orders");

                            } else {
                                showErrorMsg(loginResponse.ResponseMsg);
                                if (loginResponse.ResponseMsg.equalsIgnoreCase("Provided user_id is wrong. It is not valid user for this request. Please contact admin or provide valid user_id")) {
                                    appPreferences.set("USERID", "");
                                    appPreferences.set("USERNAME", "");
                                    appPreferences.set("MOBILE", "");
                                    appPreferences.set("EMAIL", "");
                                    appPreferences.set("USERTYPE", "");
                                    appPreferences.set("ZIPCODE", "");
                                    appPreferences.set("city", "");
                                    appPreferences.set("area", "");
                                    finishAffinity();
                                    startActivity(new Intent(ActivityRecentOrder.this, ActivityLogin.class));
                                }
                            }
                            if (orderTbls.size() > 0) {
                                noorder.setVisibility(View.INVISIBLE);
                                RvOrder.setVisibility(View.VISIBLE);
                            } else {
                                noorder.setVisibility(View.VISIBLE);
                                RvOrder.setVisibility(View.INVISIBLE);
                                tvTotalOrder.setText(orderTbls.size() + " Orders");
                            }
                        } else {
                            showInfoMsg(response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please contact admin");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void pickOrderAPICALL(String oid) {
        try {
            if (!Utility.isNetworkAvailable(ActivityRecentOrder.this)) {
                Utility.showToast("please check internet connection", ActivityRecentOrder.this);
            } else {
                Utility.showProgress(ActivityRecentOrder.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<UpdateOrderResponse> responseCall = login.updateOrders(appPreferences.getString("USERID"), oid, "Processing");
                responseCall.enqueue(new Callback<UpdateOrderResponse>() {
                    @Override
                    public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            UpdateOrderResponse loginResponse = response.body();
                            if (loginResponse.isValid()) {
                                Utility.showToast(loginResponse.ResponseMsg, ActivityRecentOrder.this);
                                APICALL();
                            } else {
                                Utility.showToast(loginResponse.ResponseMsg, ActivityRecentOrder.this);
                            }
                        } else {
                            Utility.showToast(response.message(), ActivityRecentOrder.this);
                        }

                    }

                    @Override
                    public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showToast("please contact admin", ActivityRecentOrder.this);
                    }
                });
            }
        } catch (Exception ex) {
            Utility.showToast(ex.toString(), ActivityRecentOrder.this);
        }
    }

    @Override
    public void onOrderPick(Order_tbl orderTbl) {
        pickOrderAPICALL(orderTbl.OrderID);
    }

    @Override
    public void onVerifyOtp(String otp, Order_tbl orderTbl) {
        invokeVerifyOtp(otp, orderTbl);
    }

    private void invokeVerifyOtp(String otp, final Order_tbl order_tbl) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Toasty.info(this, "please check internet connection").show();
            } else {
                Utility.showProgress(this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<OrderResponse> responseCall = login.verifyOTP(appPreferences.getString("USERID"), order_tbl.OrderID, otp);
                responseCall.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            OrderResponse memberResponse = response.body();
                            if (memberResponse.isValid()) {
                                showInfoMsg(memberResponse.ResponseMsg);
                                startActivity(new Intent(mActivity, OrderDetails.class).putExtra("order", order_tbl));
                            } else {
                                showInfoMsg(memberResponse.ResponseMsg);
                            }
                        } else {
                            showInfoMsg("OTP not verify");
                        }

                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showInfoMsg("Please contact admin");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
        }
    }
}
