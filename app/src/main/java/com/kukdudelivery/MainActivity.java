package com.kukdudelivery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kukdudelivery.Adapter.DashboardAdapter;
import com.kukdudelivery.Adapter.DashboardPastAdapter;
import com.kukdudelivery.Adapter.SpinnerAdapter;
import com.kukdudelivery.ApiController.DashboardResponse;
import com.kukdudelivery.ApiController.DateFilterResponse;
import com.kukdudelivery.Model.DateFilterInfo;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.Utility.TEZ_REQUEST_CODE;
import static com.kukdudelivery.util.Utility.getCurrencyAmount;
import static com.kukdudelivery.util.Utility.gotoGPay;
import static com.kukdudelivery.util.Utility.hideProgress;
import static com.kukdudelivery.util.Utility.notNullEmpty;

public class MainActivity extends BaseActivity {

    TTextView tvUserName, noorder;
    RecyclerView rvPast, rvCOD;
    Activity mActivity;
    TextView txt_assigned_order,
            txt_delivered_order,
            txt_cod_amount,
            txt_todays_earning,
            txt_weekly_earning;
    Spinner spDateFilter;

    DashboardAdapter mAdapter;
    DashboardPastAdapter pastAdapter;

    private void goTo(Class<?> cls) {
        startActivity(new Intent(mActivity, cls));
    }

    private void initBottom() {
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

        findViewById(R.id.ic_recent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(ActivityRecentOrder.class);
            }
        });

        findViewById(R.id.tvRecent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(ActivityRecentOrder.class);
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

        ((TextView) findViewById(R.id.tvhome)).
                setTextColor(ContextCompat.getColor(mActivity, R.color.red_theme_color));

        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall(appPreferences.getString("HELP"));
//                gotoGPay(mActivity);
            }
        });

        findViewById(R.id.llCod).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, CompleteOrdersActivity.class).putExtra("IS_COD", true));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TEZ_REQUEST_CODE) {
            // Process based on the data in response.
            Log.e("TEZ_REQUEST_CODE::", "RESULT::" + data.getStringExtra("Status"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        try {
            initBottom();
            tvUserName = findViewById(R.id.tvUserName);
            noorder = findViewById(R.id.noorder);
            rvPast = findViewById(R.id.rv_past);
            rvCOD = findViewById(R.id.rv_cod);

            txt_assigned_order = findViewById(R.id.txt_assigned_order);
            txt_delivered_order = findViewById(R.id.txt_delivered_order);
            txt_cod_amount = findViewById(R.id.txt_cod_amount);
            txt_todays_earning = findViewById(R.id.txt_todays_earning);
            txt_weekly_earning = findViewById(R.id.txt_weekly_earning);

            spDateFilter = findViewById(R.id.spDateFilter);
            initData();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initData() {
        tvUserName.setText(String.format("Welcome %s", appPreferences.getString("USERNAME")));

        rvCOD.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvPast.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDateFilter();
    }

    private void getDashboardData(String searchDate) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(this);

                WebServiceCaller.ApiInterface client = WebServiceCaller.getClient();
                Call<DashboardResponse> responseCall = client.getDashboard(appPreferences.getString("USERID"), searchDate);
                responseCall.enqueue(new Callback<DashboardResponse>() {
                    @Override
                    public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful() && response.body() != null) {
                            DashboardResponse dashboardResponse = response.body();
                            if (dashboardResponse.isValid()) {
                                DashboardResponse.Dashboard dashboard = dashboardResponse.dashboard;

                                txt_cod_amount.setText(getCurrencyAmount(dashboard.totalCOD));
                                txt_todays_earning.setText(getCurrencyAmount(dashboard.todayEarning));
                                txt_weekly_earning.setText(getCurrencyAmount(dashboard.weeklyEarning));

                                txt_assigned_order.setText(dashboard.totalAllotedOrders);
                                txt_delivered_order.setText(dashboard.totalDeliveredOrders);

                               /* if (notNullEmpty(dashboard.codOrders)) {
                                    mAdapter = new DashboardAdapter(mActivity, dashboard.codOrders);
                                    rvCOD.setAdapter(mAdapter);
                                }*/

                                if (notNullEmpty(dashboard.weeklyOrders)) {
                                    rvPast.setVisibility(View.VISIBLE);
                                    pastAdapter = new DashboardPastAdapter(mActivity, dashboard.weeklyOrders);
                                    rvPast.setAdapter(pastAdapter);
                                } else {
                                    rvPast.setVisibility(View.GONE);
                                }
                            } else {
                                showInfoMsg(dashboardResponse.ResponseMsg);
                            }
                        } else {
                            showInfoMsg(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<DashboardResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please contact admin");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(PasOrderAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    private void getDateFilter() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(this);

                WebServiceCaller.ApiInterface client = WebServiceCaller.getClient();
                Call<DateFilterResponse> responseCall = client.getDateFilter();
                responseCall.enqueue(new Callback<DateFilterResponse>() {
                    @Override
                    public void onResponse(Call<DateFilterResponse> call, Response<DateFilterResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful() && response.body() != null) {
                            DateFilterResponse dashboardResponse = response.body();
                            if (dashboardResponse.isValid()) {
                                if (notNullEmpty(dashboardResponse.arrayList)) {
//                                    getDashboardData(dashboardResponse.arrayList.get(0).searchDate);
//                                    dashboardResponse.arrayList.add(0, new DateFilterInfo("Select Delivery Time"));
                                    spDateFilter.setAdapter(new SpinnerAdapter(mActivity, R.layout.row, dashboardResponse.arrayList));
                                    spDateFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            DateFilterInfo filterInfo = (DateFilterInfo) spDateFilter.getSelectedItem();
                                            getDashboardData(filterInfo.searchDate);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                    spDateFilter.setSelection(0);
                                }
                            } else {
                                hideProgress();
                                showInfoMsg(dashboardResponse.ResponseMsg);
                            }
                        } else {
                            hideProgress();
                            showInfoMsg(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<DateFilterResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please contact admin");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(PasOrderAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }
}
