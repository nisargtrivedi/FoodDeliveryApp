package com.kukdudelivery;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kukdudelivery.Adapter.OrderAdapter;
import com.kukdudelivery.Adapter.PastOrderAdapter;
import com.kukdudelivery.Adapter.SpinnerAdapter;
import com.kukdudelivery.ApiController.OrderResponse;
import com.kukdudelivery.Model.DateFilterInfo;
import com.kukdudelivery.Model.Order_tbl;
import com.kukdudelivery.util.DateUtils;
import com.kukdudelivery.util.RecyclerViewScrollListener;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.DateUtils.DATE_FORMAT.DISPLAY_FORMAT;
import static com.kukdudelivery.util.DateUtils.DATE_FORMAT.SERVER_FORMAT;
import static com.kukdudelivery.util.Utility.PAYMENT_REQUEST_CODE;
import static com.kukdudelivery.util.Utility.notNullEmpty;

public class CompleteOrdersActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, PastOrderAdapter.PaymentListener {

    RecyclerView RvOrder;
    LinearLayout llDateFilter, llOrderSearch;
    TTextView tvTotalOrder, tvUserName, noorder, ordermsg;
    TextView txtDate, txtClear;
    Spinner spPaymentType;
    EditText edtSearch;

    ArrayList<Order_tbl> orderTbls = new ArrayList<>();
    PastOrderAdapter adapter;

    DatePickerDialog dateDialog;
    Activity mActivity;
    String selectedDate = "", serverSelectedDate = "", paymentType = "", searchText = "";

    private RecyclerViewScrollListener scrollListener;
    private boolean isPaginate = false;
    private int page = 1;

    private void goTo(Class<?> cls) {
        if (cls == MainActivity.class)
            finishAffinity();
        else
            finish();
        startActivity(new Intent(mActivity, cls));
    }

    private void initBottom() {
        findViewById(R.id.explore).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tvDelivered)).
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
            txtDate = findViewById(R.id.txtDate);
            txtClear = findViewById(R.id.txtClear);
            llDateFilter = findViewById(R.id.llDateFilter);
            llOrderSearch = findViewById(R.id.llOrderSearch);
            spPaymentType = findViewById(R.id.spPaymentType);
            edtSearch = findViewById(R.id.edt_search);

            llDateFilter.setVisibility(View.VISIBLE);
            llOrderSearch.setVisibility(View.VISIBLE);

            txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCalender();
                }
            });
            txtClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (notNullEmpty(selectedDate) || notNullEmpty(searchText)) {
                        selectedDate = "";
                        serverSelectedDate = "";
                        txtDate.setText("");
                        edtSearch.setText("");
                        searchText = edtSearch.getText().toString();
                        page = 1;
                        orderTbls = new ArrayList<>();
                        APICALL(true);
                    }
                }
            });


            edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH) {
                        callSearchApi();
                        return true;
                    }
                    return false;
                }
            });

            findViewById(R.id.txtGo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callSearchApi();
                }
            });

            scrollListener = new RecyclerViewScrollListener() {
                @Override
                public void onEndOfScrollReached(RecyclerView recyclerView) {
                    Log.e("MY_WALLET::", "onEndOfScrollReached");
                    if (!isPaginate) {
                        isPaginate = true;
                        page++;
                        orderTbls.add(null);
                        adapter.notifyItemInserted(orderTbls.size());
                        APICALL(false);
                    }
                }
            };
            RvOrder.addOnScrollListener(scrollListener);

            ordermsg.setText("Past Order");
           /* adapter = new PastOrderAdapter(this, orderTbls);
            RvOrder.setLayoutManager(new LinearLayoutManager(this));
            RvOrder.setAdapter(adapter);*/

            ArrayList<DateFilterInfo> list = new ArrayList<>();
            list.add(new DateFilterInfo("All"));
            list.add(new DateFilterInfo("Cash"));
            list.add(new DateFilterInfo("Online"));

            spPaymentType.setAdapter(new SpinnerAdapter(mActivity, R.layout.row, list));
            spPaymentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0)
                        paymentType = "";
                    else {
                        DateFilterInfo filterInfo = (DateFilterInfo) spPaymentType.getSelectedItem();
                        paymentType = filterInfo.display;
                    }

                    try {
                        page = 1;
                        orderTbls = new ArrayList<>();
                        APICALL(true);
                    } catch (Exception ex) {
                        sendMail(PasOrderAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            Bundle bundle = getIntent().getExtras();
            spPaymentType.setSelection((bundle != null && bundle.containsKey("IS_COD")) ? 1 : 0);

            tvUserName.setText(String.format("Welcome %s", appPreferences.getString("USERNAME")));

        } catch (Exception ex) {
            System.out.println("Complete Order Error-->" + ex.toString());
            sendMail("Complete Order Error--> \n " + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    private void callSearchApi() {
        searchText = edtSearch.getText().toString();
        page = 1;
        orderTbls = new ArrayList<>();
        APICALL(true);
    }

    private void removeLoader(boolean isError) {
        if (isError && page > 1) {
            page--;
            isPaginate = false;
        }

        if (notNullEmpty(orderTbls)) {
            int pos = orderTbls.size() - 1;
            if (orderTbls.get(pos) == null) {
                orderTbls.remove(pos);
                adapter.notifyItemRemoved(pos);
            }
        }
    }

    private void APICALL(boolean showProgress) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                if (showProgress)
                    Utility.showProgress(CompleteOrdersActivity.this);

                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<OrderResponse> responseCall = login.listPastOrders(appPreferences.getString("USERID"),
                        serverSelectedDate, paymentType.toLowerCase(), searchText, page);
                responseCall.enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        Utility.hideProgress();

                        if (response.isSuccessful()) {
                            removeLoader(false);
                            OrderResponse loginResponse = response.body();
                            if (loginResponse.isValid()) {
                                if (notNullEmpty(orderTbls)) {
                                    orderTbls.addAll(loginResponse.orderTbls);
                                    adapter.updateList(orderTbls);
                                } else {
                                    RvOrder.setVisibility(View.VISIBLE);
                                    noorder.setVisibility(View.INVISIBLE);
                                    orderTbls = loginResponse.orderTbls;
                                    adapter = new PastOrderAdapter(mActivity, orderTbls);
                                    adapter.setListener(CompleteOrdersActivity.this);
                                    RvOrder.setAdapter(adapter);
                                }

                                if (page < loginResponse.totalPages)
                                    scrollListener.enableScrollListener();
                                else
                                    scrollListener.disableScrollListener();

                            } else {
                                scrollListener.disableScrollListener();
                                if (page == 1) {
                                    orderTbls = new ArrayList<>();
                                    RvOrder.setVisibility(View.GONE);
                                    noorder.setVisibility(View.VISIBLE);
                                }
                                showInfoMsg(loginResponse.ResponseMsg);
                            }
                        } else {
                            removeLoader(true);
                            scrollListener.disableScrollListener();
                            if (page == 1) {
                                orderTbls = new ArrayList<>();
                                RvOrder.setVisibility(View.GONE);
                                noorder.setVisibility(View.VISIBLE);
                            }
                            showInfoMsg(response.message());
                        }
                        isPaginate = false;
                        updateCount();
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        Utility.hideProgress();
                        removeLoader(true);
                        scrollListener.disableScrollListener();
                        if (page == 1) {
                            orderTbls = new ArrayList<>();
                            RvOrder.setVisibility(View.GONE);
                            noorder.setVisibility(View.VISIBLE);
                        }
                        isPaginate = false;
                        updateCount();
                        showErrorMsg("please contact admin");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(PasOrderAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    private void updateCount() {
        int size = notNullEmpty(orderTbls) ? orderTbls.size() : 0;
        tvTotalOrder.setText(String.format(Locale.US, "%d Orders", size));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void openCalender() {
        Calendar now = Calendar.getInstance();
        if (notNullEmpty(selectedDate))
            now.setTime(DateUtils.getFormattedDate(selectedDate, DISPLAY_FORMAT));

        dateDialog = new DatePickerDialog(mActivity, R.style.DialogTheme, this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );

        dateDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dateDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        selectedDate = DateUtils.getFormattedDate(cal, DISPLAY_FORMAT);
        Log.e("DATE::", selectedDate);
        txtDate.setText(selectedDate);
        if (notNullEmpty(selectedDate)) {
            serverSelectedDate = DateUtils.getFormattedDate(selectedDate, DISPLAY_FORMAT, SERVER_FORMAT);
            page = 1;
            orderTbls = new ArrayList<>();
            APICALL(true);
        }
    }

    @Override
    public void onPayNow(Order_tbl orderTbl) {
        startActivityForResult(new Intent(mActivity, PaymentActivity.class)
                .putExtra("ORDER_ID", orderTbl.OrderID)
                .putExtra("total", orderTbl.paidAmount), PAYMENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            page = 1;
            orderTbls = new ArrayList<>();
            APICALL(true);
        }
    }
}
