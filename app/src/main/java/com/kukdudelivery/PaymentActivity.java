package com.kukdudelivery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kukdudelivery.ApiController.CommonResponse;
import com.kukdudelivery.ApiController.PaymentOptionResponse;
import com.kukdudelivery.Model.CheckSum;
import com.kukdudelivery.Model.Paytm;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.kukdudelivery.WebApi.WebUtility;
import com.kukdudelivery.util.Utility;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.razorpay.Checkout;
import com.razorpay.Payment;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.Utility.TEZ_REQUEST_CODE;
import static com.kukdudelivery.util.Utility.getAmount;
import static com.kukdudelivery.util.Utility.getGPayUri;
import static com.kukdudelivery.util.Utility.hideProgress;

public class PaymentActivity extends BaseActivity implements PaymentResultListener, View.OnClickListener {
    Activity mActivity;
    TextView tvPaytm, tvGPay, tvRazorPayy, tvBack;
    ProgressBar loading;

    double Total = 0;
    String OrderID = "";
    Bundle mBundle;

    JSONObject object = null;
    Uri gPayUri = null;
    Checkout checkout = new Checkout();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mActivity = this;
        initView();
    }

    private void initView() {
        tvPaytm = findViewById(R.id.tvPaytm);
        tvGPay = findViewById(R.id.tvGPay);
        tvRazorPayy = findViewById(R.id.tvRazorPayy);
        tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(this);
        tvGPay.setOnClickListener(this);
        tvPaytm.setOnClickListener(this);
        tvRazorPayy.setOnClickListener(this);

        loading = findViewById(R.id.loading);

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            OrderID = mBundle.getString("ORDER_ID");

            if (OrderID == null || OrderID.equals(""))
                OrderID = android.text.format.DateFormat.format("yyyyMMddHHmmss", new Date()).toString();

            Total = Double.parseDouble(getIntent().getStringExtra("total"));

            fetchPaymentStatus();

            //checkout.setKeyID("rzp_live_N2bFpPqVkW3q3y");
            Checkout.preload(PaymentActivity.this);

        }

    }

    private void fetchPaymentStatus() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(this);

                WebServiceCaller.ApiInterface client = WebServiceCaller.getClient();
                Call<PaymentOptionResponse> responseCall = client.getPaymentOption(appPreferences.getString("USERID"));
                responseCall.enqueue(new Callback<PaymentOptionResponse>() {
                    @Override
                    public void onResponse(Call<PaymentOptionResponse> call, Response<PaymentOptionResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful() && response.body() != null) {
                            PaymentOptionResponse optionResponse = response.body();
                            if (optionResponse.isValid()) {
                                tvGPay.setVisibility(optionResponse.list.contains("GooglePay") ? View.VISIBLE : View.GONE);
                                tvPaytm.setVisibility(optionResponse.list.contains("Paytm") ? View.VISIBLE : View.GONE);
                                tvRazorPayy.setVisibility(optionResponse.list.contains("Razorpay") ? View.VISIBLE : View.GONE);
                            } else {
                                hideProgress();
                                showInfoMsg(optionResponse.ResponseMsg);
                            }
                        } else {
                            hideProgress();
                            showInfoMsg(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentOptionResponse> call, Throwable t) {
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




    private HashMap<String, String> getCommonParam() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", appPreferences.getString("USERID"));
        params.put("order_id", OrderID);
        params.put("paid_amount", getAmount(Total));
        params.put("payment_type", "online");

        return params;
    }

    private void manageLoader(boolean isEnable) {
        tvPaytm.setEnabled(isEnable);
        tvRazorPayy.setEnabled(isEnable);
        tvGPay.setEnabled(isEnable);

        loading.setVisibility(isEnable ? View.GONE : View.VISIBLE);
    }

    private void requestGPay() {
        // Disables the button to prevent multiple clicks.
        loading.setVisibility(View.VISIBLE);
        tvGPay.setEnabled(false);
        gPayUri = getGPayUri(getAmount(Total), OrderID);
        Utility.gotoGPay(this, gPayUri);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("RAZOR PAY BACK====>" + data.getExtras());

        if (requestCode == TEZ_REQUEST_CODE) {
            manageLoader(true);

            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String response = bundle.toString();
                Log.e("TEZZ::", response);
                String status = bundle.getString("Status", "");
                String txnID = bundle.getString("txnId", "");

                if (status.equalsIgnoreCase("success")) {
                    HashMap<String, String> params = getCommonParam();
                    params.put("payment_gateway_request", gPayUri.toString());
                    params.put("payment_gateway_response", response);
                    params.put("payment_via", "G-Pay");
                    params.put("transaction_id", txnID);
                    params.put("payment_status", "paid");

                    payCODorder(params);
                }
            }


        }
    }

    private void checkOutRazorPay() {
        try {

            checkout.setImage(R.drawable.logoapp_);
            object = new JSONObject();
            object.put("description", "Food Order," + OrderID);
            object.put("currency", "INR");
            long amt = Math.round(Total * 100);
            object.put("amount", amt);
            checkout.open(mActivity, object);

        } catch (JSONException ex) {
            ex.printStackTrace();
            manageLoader(true);
            showErrorMsg(ex.toString());
        }
       /* try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", 50000); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_11");
            orderRequest.put("payment_capture", true);
            Order order = razorpay.Orders.create(orderRequest);
        } catch (RazorpayException e) {  // Handle Exception
            System.out.println(e.getMessage());
        }*/
    }

    @Override
    public void onPaymentSuccess(String s) {
        System.out.println("RAZOR PAY DATA=====" + s);
        manageLoader(true);
        HashMap<String, String> params = getCommonParam();
        params.put("payment_gateway_request", object.toString());
        params.put("payment_gateway_response", s);
        params.put("payment_via", getString(R.string.razor_pay).replaceAll("\\s+", ""));
        params.put("transaction_id", s);
        params.put("payment_status", "paid");

        //payCODorder(params);
        PaymentCapture((int)Total,s,params);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.i("PAYMENT ERROR RAZOR PAY", i + "=>" + s);
        manageLoader(true);

    }

    private void generateCheckSum() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(this);

                final Paytm paytm = new Paytm(
                        WebUtility.PAYTM_MERCHANT_ID,
                        WebUtility.CHANNEL_ID,
                        Total + "",
                        WebUtility.WEBSITE,
                        WebUtility.CALLBACK_URL + OrderID,
                        WebUtility.INDUSTRY_TYPE_ID
                );

                WebServiceCaller.ApiInterface client = WebServiceCaller.getPaytmClient();
                Call<CheckSum> responseCall = client.getChecksum(paytm.getmId(),
                        OrderID,
                        appPreferences.getString("USERID"),
                        paytm.getChannelId(),
                        paytm.getTxnAmount(),
                        paytm.getWebsite(),
                        paytm.getCallBackUrl(),
                        paytm.getIndustryTypeId());
                responseCall.enqueue(new Callback<CheckSum>() {
                    @Override
                    public void onResponse(Call<CheckSum> call, Response<CheckSum> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful() && response.body() != null) {
                            System.out.println("Error---->" + response.body().toString());
                            System.out.println("CHECKSUM----->" + response.body().getChecksumHash());
                            try {
                                if (Utility.isNetworkAvailable(mActivity))
                                    initializePaytmPayment(response.body().getChecksumHash(), paytm);
                                else
                                    showInfoMsg("please check internet connection");
                            } catch (Exception ex) {
                                showInfoMsg("please check internet connection");
                            }
                        } else {
                            hideProgress();
                            manageLoader(true);
                            showInfoMsg(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckSum> call, Throwable t) {
                        Utility.hideProgress();
                        manageLoader(true);
                        showErrorMsg("please contact admin");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(PasOrderAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    private void initializePaytmPayment(String checksumHash, Paytm paytm) {

        try {
            //getting paytm service
            PaytmPGService Service = PaytmPGService.getProductionService();

            //use this when using for production
            //PaytmPGService Service = PaytmPGService.getProductionService();

            //creating a hashmap and adding all the values required
            final HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("MID", WebUtility.PAYTM_MERCHANT_ID);
            paramMap.put("ORDER_ID", OrderID);
            paramMap.put("CUST_ID", appPreferences.getString("USERID"));
            paramMap.put("CHANNEL_ID", paytm.getChannelId());
            paramMap.put("TXN_AMOUNT", Total + "");
            paramMap.put("WEBSITE", paytm.getWebsite());
            paramMap.put("CALLBACK_URL", paytm.getCallBackUrl());
            paramMap.put("CHECKSUMHASH", checksumHash);
            paramMap.put("INDUSTRY_TYPE_ID", paytm.getIndustryTypeId());

            Log.e("PAYTM_PARAMETER::", paramMap.toString());

//        paramMap.put("MID", "jnPbDy42525351382302");
//        paramMap.put("ORDER_ID", "order1234erte45667038");
//        paramMap.put("CUST_ID", "cus1232567");
//        paramMap.put("CHANNEL_ID", "WAP");
//        paramMap.put("TXN_AMOUNT", "1.00");
//        paramMap.put("WEBSITE", "DEFAULT");
//        paramMap.put("CALLBACK_URL","https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=order1234erte45667038");
//        paramMap.put("CHECKSUMHASH", "KkVa6SndAxZJiR8OD2PFXIV42IgM3XFu9NKAq+XhMjTskHmTmh8Kut5MP01ib5VsNg/BzCFgz/nVC4i3A+183mUBN9cOXK7nqm8R2RXaf/g=");
//        paramMap.put("INDUSTRY_TYPE_ID", "Retail");


            //creating a paytm order object using the hashmap
            PaytmOrder order = new PaytmOrder(paramMap);

            //intializing the paytm service
            Service.initialize(order, null);

            //finally starting the payment transaction
            Service.startPaymentTransaction(this, true, true,
                    new PaytmPaymentTransactionCallback() {
                        @Override
                        public void onTransactionResponse(Bundle inResponse) {
                            System.out.println("Success-->" + inResponse.toString());

                            //String str="Bundle[{STATUS=TXN_SUCCESS,
                            // CHECKSUMHASH=lUf5SKbEMnNlQcqPf09r3Y3JzeVF7Zf5niIWx4ciNQdE/EnrsRul2u3zG+4bKtZY7b4Awmp1G4yF5hlmviC8Z+USi+nGH7QHXhsSBwYK6x8=,
                            // BANKNAME=WALLET, ORDERID=20190812162302, TXNAMOUNT=2.00, TXNDATE=2019-08-12 16:23:41.0, MID=jnPbDy42525351382302,
                            // TXNID=20190812111212800110168634882512434, RESPCODE=01, PAYMENTMODE=PPI, BANKTXNID=119417780579, CURRENCY=INR,
                            // GATEWAYNAME=WALLET, RESPMSG=Txn Success}]";
                            String paytmStatus = inResponse.getString("STATUS");
                            String txnId = inResponse.getString("ORDERID");
                            System.out.println("STATUS----------->" + paytmStatus);
                            if (inResponse.getString("STATUS").equalsIgnoreCase("TXN_SUCCESS")) {

                                manageLoader(true);
                                HashMap<String, String> params = getCommonParam();
                                params.put("payment_gateway_request ", paramMap.toString());
                                params.put("payment_gateway_response ", inResponse.toString());
                                params.put("payment_via ", "Paytm");
                                params.put("transaction_id ", txnId);
                                params.put("payment_status ", "paid");

                                payCODorder(params);
                            } else {
                                manageLoader(true);
                                showInfoMsg(inResponse.getString("RESPMSG"));

                            }
                        }

                        @Override
                        public void networkNotAvailable() {
                            showInfoMsg("No Network Available");
                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                            try {
                                showInfoMsg(inErrorMessage);
                                manageLoader(true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {

                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            try {
                                showInfoMsg("Back Press Transaction Cancel");
                                manageLoader(true);
                            } catch (Exception ex) {

                            }
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                            try {
                                showInfoMsg(inErrorMessage);
                                manageLoader(true);
                            } catch (Exception ex) {

                            }
                        }
                    });
        } catch (Exception ex) {
            manageLoader(true);
        }
    }

    private void payCODorder(HashMap<String, String> params) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(this);
                Log.e("COD_REQUEST::", params.toString());
                WebServiceCaller.ApiInterface client = WebServiceCaller.getClient();
                Call<CommonResponse> responseCall = client.payCODOrder(params);
                responseCall.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful() && response.body() != null) {
                            CommonResponse optionResponse = response.body();
                            if (optionResponse.isValid()) {
                                showSuccessMsg(optionResponse.ResponseMsg);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(RESULT_OK, new Intent());
                                        PaymentActivity.super.onBackPressed();
                                    }
                                }, 500);
                            } else {
                                showInfoMsg(optionResponse.ResponseMsg);
                            }
                        } else {
                            showInfoMsg(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvBack:
                onBackPressed();
                break;
            case R.id.tvGPay:
                manageLoader(false);
                requestGPay();
                break;
            case R.id.tvPaytm:
                manageLoader(false);
                generateCheckSum();
                break;
            case R.id.tvRazorPayy:
                manageLoader(false);
                checkOutRazorPay();
                break;

        }
    }


    RazorpayClient razorpayClient;
    private void PaymentCapture(int total,String tid,HashMap<String, String> params){
        try {

            razorpayClient=new RazorpayClient(WebUtility.RAZORPAY_LIVE,WebUtility.RAZORPAY_LIVE_SECRET);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                JSONObject captureRequest = new JSONObject();
                captureRequest.put("amount", (total*100));
                captureRequest.put("currency", "INR");

                //System.out.println("AMOUNT------------->"+(order_tbl.OrderTotal));
                Payment payment = razorpayClient.Payments.capture(tid, captureRequest);

                if(payment!=null){
                    if(payment.get("status")!=null && Utility.notNullEmpty(payment.get("status").toString())){
                        String status=payment.get("status").toString();
                        if(status.equalsIgnoreCase("captured")){
                            payCODorder(params);
                        }
                    }
                }
//                {"id":"pay_F9AptDNy6QbDZW","entity":"payment","amount":29100,"currency":"INR","status":"captured","order_id":null,"invoice_id":null,"international":false,"method":"card","amount_refunded":0,"refund_status":null,"captured":true,"description":"Food Order,546","card_id":"card_F9AptH2zZv40Th","card":{"id":"card_F9AptH2zZv40Th","entity":"card","name":"Test Service Center","last4":"1111","network":"Visa","type":"debit","issuer":null,"international":false,"emi":false},"bank":null,"wallet":null,"vpa":null,"email":"nisarg.trivedi1192@gmail.com","contact":"+919978538694","notes":[],"fee":582,"tax":0,"error_code":null,"error_description":null,"error_source":null,"error_step":null,"error_reason":null,"created_at":1593593922}

                //Log.i("PAYMENT======>",payment.toString());
            } catch (RazorpayException e) {
                // Handle Exception
                System.out.println(e.getMessage());
            }

//            I/ORDER ID======>: {"id":"order_F8on060xPLIFsG","entity":"order","amount":50000,"amount_paid":0,"amount_due":50000,"currency":"INR","receipt":"order_rcptid_11","offer_id":null,"status":"created","attempts":0,"notes":[],"created_at":1593516283}
//            order_F8on060xPLIFsG
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
