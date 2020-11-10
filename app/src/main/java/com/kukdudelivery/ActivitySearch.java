package com.kukdudelivery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.kukdudelivery.Adapter.PastOrderAdapter;
import com.kukdudelivery.Adapter.RecentOrderAdapter;
import com.kukdudelivery.ApiController.OrderResponse;
import com.kukdudelivery.ApiController.UpdateOrderResponse;
import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearch extends BaseActivity implements RecentOrderAdapter.ItemClickListener {

    RecyclerView RvOrder;
    TTextView noorder, help;
    ImageView imgSearch;

    ArrayList<Order_tbl> orderTbls = new ArrayList<>();
    RecentOrderAdapter adapter;
    EEditText txtSearch;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mActivity = this;
        try {
            RvOrder = findViewById(R.id.RvORders);
            noorder = findViewById(R.id.noorder);
            help = findViewById(R.id.help);
            txtSearch = findViewById(R.id.txtSearch);
            imgSearch = findViewById(R.id.imgSearch);

            adapter = new RecentOrderAdapter(this, orderTbls);
            adapter.setItemClickListener(this);
            RvOrder.setLayoutManager(new LinearLayoutManager(this));
            RvOrder.setAdapter(adapter);


            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    //makePhoneCall(appPreferences.getString("PHONE"));
                }
            });

            txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        if (txtSearch.getText().toString().length() > 0)
                            APICALL();
                        return true;
                    }
                    return false;
                }
            });

            imgSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (txtSearch.getText().toString().length() > 0)
                        APICALL();
                }
            });

           /* txtSearch.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (txtSearch.getRight() - txtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (txtSearch.getText().toString().length() > 0)
                                APICALL();
                            return true;
                        }
                    }
                    return false;
                }
            });
*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //APICALL();
    }

    private void APICALL() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(ActivitySearch.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<OrderResponse> responseCall = login.search(txtSearch.getText().toString().trim(),
                        appPreferences.getString("USERID"));
                responseCall.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        Utility.hideProgress();
                        try {


                            if (response.isSuccessful()) {
                                OrderResponse loginResponse = response.body();
                                if (loginResponse.isValid()) {
                                    orderTbls.clear();
                                    orderTbls.addAll(loginResponse.orderTbls);
                                    adapter.notifyDataSetChanged();


                                } else {
                                    showErrorMsg(loginResponse.ResponseMsg);
                                }
                                if (orderTbls.size() > 0) {
                                    noorder.setVisibility(View.INVISIBLE);
                                    RvOrder.setVisibility(View.VISIBLE);
                                } else {
                                    noorder.setVisibility(View.VISIBLE);
                                    RvOrder.setVisibility(View.INVISIBLE);

                                }
                            } else {
                                showInfoMsg(response.message());
                            }
                        } catch (Exception ex) {

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
            sendMail(SearchAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    private void pickOrderAPICALL(String oid) {
        try {
            if (!Utility.isNetworkAvailable(ActivitySearch.this)) {
                Utility.showToast("please check internet connection", ActivitySearch.this);
            } else {
                Utility.showProgress(ActivitySearch.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<UpdateOrderResponse> responseCall = login.updateOrders(appPreferences.getString("USERID"), oid, "Processing");
                responseCall.enqueue(new Callback<UpdateOrderResponse>() {
                    @Override
                    public void onResponse(Call<UpdateOrderResponse> call, Response<UpdateOrderResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            UpdateOrderResponse loginResponse = response.body();
                            if (loginResponse.isValid()) {
                                Utility.showToast(loginResponse.ResponseMsg, ActivitySearch.this);
                                APICALL();
                            } else {
                                Utility.showToast(loginResponse.ResponseMsg, ActivitySearch.this);
                            }
                        } else {
                            Utility.showToast(response.message(), ActivitySearch.this);
                        }

                    }

                    @Override
                    public void onFailure(Call<UpdateOrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        Utility.showToast("please contact admin", ActivitySearch.this);
                    }
                });
            }
        } catch (Exception ex) {
            Utility.showToast(ex.toString(), ActivitySearch.this);
            sendMail(UpdateOrderAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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
