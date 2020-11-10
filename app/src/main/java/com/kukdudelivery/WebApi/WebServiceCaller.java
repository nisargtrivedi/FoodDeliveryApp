package com.kukdudelivery.WebApi;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kukdudelivery.ApiController.CommonResponse;
import com.kukdudelivery.ApiController.DashboardResponse;
import com.kukdudelivery.ApiController.DateFilterResponse;
import com.kukdudelivery.ApiController.ForgotPasswordResponse;
import com.kukdudelivery.ApiController.LoginResponse;
import com.kukdudelivery.ApiController.OrderResponse;
import com.kukdudelivery.ApiController.PaymentOptionResponse;
import com.kukdudelivery.ApiController.ProfileDetailsResponse;
import com.kukdudelivery.ApiController.UpdateOrderResponse;
import com.kukdudelivery.Model.CheckSum;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


public class WebServiceCaller {
    private static ApiInterface ApiInterface, ApiInterfacePaytm, ApiInterfaceLoc,ApiInterfaceRazorPay;

    public static ApiInterface getClient() {
        if (ApiInterface == null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(WebUtility.BASE_URL)
                    .client(getRetroClient(true))
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
            Log.i("URL===", WebUtility.BASE_URL);
            ApiInterface = client.create(ApiInterface.class);
        }
        return ApiInterface;
    }

    public static ApiInterface getLocClient() {
        if (ApiInterfaceLoc == null) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(WebUtility.BASE_URL)
                    .client(getRetroClient(false))
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
            Log.i("URL===", WebUtility.BASE_URL);
            ApiInterfaceLoc = client.create(ApiInterface.class);
        }
        return ApiInterfaceLoc;
    }

    private static OkHttpClient getRetroClient(boolean hasLog) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                //.addHeader("Authorization", "Bearer "+com.kukdudelivery.BaseActivity.getInstance().txtToken.getText().toString())
                                //.addHeader("App-Key", "nE28E~]EeP.a")
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        builder.connectTimeout(1000, TimeUnit.SECONDS);
        builder.readTimeout(1000, TimeUnit.SECONDS);
        builder.writeTimeout(1000, TimeUnit.SECONDS);
        if (hasLog)
            builder.addInterceptor(logging);

        return builder.build();

    }

    public static ApiInterface getPaytmClient() {

        if (ApiInterfacePaytm == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    //.addHeader("Authorization", "Bearer "+com.kukdudelivery.BaseActivity.getInstance().txtToken.getText().toString())
                                    //.addHeader("App-Key", "nE28E~]EeP.a")
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    }).build();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(WebUtility.BASE_URL_PAYTM)
                    .client(okHttpClient.newBuilder()
                            .connectTimeout(1000, TimeUnit.SECONDS)
                            .readTimeout(1000, TimeUnit.SECONDS)
                            .writeTimeout(1000, TimeUnit.SECONDS)
                            .addInterceptor(logging).build())
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
            Log.i("URL===", WebUtility.BASE_URL_PAYTM);
            ApiInterfacePaytm = client.create(ApiInterface.class);
        }
        return ApiInterfacePaytm;
    }


    public static ApiInterface getRazorPayClient() {

        if (ApiInterfaceRazorPay == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request newRequest = chain.request().newBuilder()
                                    //.addHeader("Authorization", "Bearer "+com.kukdudelivery.BaseActivity.getInstance().txtToken.getText().toString())
                                    //.addHeader("App-Key", "nE28E~]EeP.a")
                                    .build();
                            return chain.proceed(newRequest);
                        }
                    }).build();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            Retrofit client = new Retrofit.Builder()
                    .baseUrl(WebUtility.RAZORPAY_URL)
                    .client(okHttpClient.newBuilder()
                            .connectTimeout(1000, TimeUnit.SECONDS)
                            .readTimeout(1000, TimeUnit.SECONDS)
                            .writeTimeout(1000, TimeUnit.SECONDS)
                            .addInterceptor(logging).build())
                    .addConverterFactory(GsonConverterFactory.create(gson)).build();
            Log.i("URL===", WebUtility.RAZORPAY_URL);
            ApiInterfaceRazorPay = client.create(ApiInterface.class);
        }
        return ApiInterfaceRazorPay;
    }




    public interface ApiInterface {

        @FormUrlEncoded
        @POST("Order/listPendingOrders")
        Call<OrderResponse> listOrders(@Field("user_id") String userID, @Field("page_id") String PageID);

        @FormUrlEncoded
        @POST("Order/updateOrderStatus")
        Call<UpdateOrderResponse> updateOrders(@Field("user_id") String userID, @Field("order_id") String OrderID, @Field("order_status") String OrderStatus);

        @FormUrlEncoded
        @POST("User/userLogin")
        Call<LoginResponse> Login(@Field("mobile") String Mobile, @Field("password") String Password, @Field("device_id") String DeviceID, @Field("device_type") String DeviceType, @Field("device_token") String DeviceToken);

        @FormUrlEncoded
        @POST("Order/listPastOrders")
        Call<OrderResponse> listPastOrders(@Field("user_id") String userID,
                                           @Field("search_date") String date,
                                           @Field("payment_type") String paymentType,
                                           @Field("search_text") String searchText,
                                           @Field("page_id") int PageID);

        @FormUrlEncoded
        @POST("User/resetPassword")
        Call<ForgotPasswordResponse> changePassword(@Field("mobile") String Mobile, @Field("newpassword") String NewPassword);

        @FormUrlEncoded
        @POST("User/updateUserProfile")
        Call<LoginResponse> updateProfile(@Field("name") String Name, @Field("email") String Email, @Field("user_id") String USERID);


        @FormUrlEncoded
        @POST("Order/verifyOTP")
        Call<OrderResponse> verifyOTP(@Field("user_id") String UserID, @Field("order_id") String OrderID, @Field("otp") String OTP);

        @FormUrlEncoded
        @POST("User/resendOTP")
        Call<ForgotPasswordResponse> resetPassword(@Field("mobile") String Mobile);


        @FormUrlEncoded
        @POST("User/userOTPLogin")
        Call<LoginResponse> otpForgotPassword(@Field("mobile") String Mobile, @Field("otp") String otp);


        @FormUrlEncoded
        @POST("Order/SearchOrder")
        Call<OrderResponse> search(@Field("keyword") String searchtext, @Field("user_id") String userid);


        @FormUrlEncoded
        @POST("User/addLocation")
        Call<ForgotPasswordResponse> updateLocation(@Field("user_id") String UserId, @Field("zipcode") String Zipcode, @Field("address") String Address, @Field("lat") String Latitude, @Field("lng") String Longitude);

        @FormUrlEncoded
        @POST("Order/getWeeklyOrderCount")
        Call<DashboardResponse> getDashboardData(@Field("user_id") String userID,
                                                 @Field("from_date") String fromDate,
                                                 @Field("to_date") String toDate);

        @FormUrlEncoded
        @POST("Order/getDashboard")
        Call<DashboardResponse> getDashboard(@Field("user_id") String userID, @Field("search_date") String searchDate);

        @GET("Order/listDateFilter")
        Call<DateFilterResponse> getDateFilter();

        @FormUrlEncoded
        @POST("Order/getDeliveryPaymentOption")
        Call<PaymentOptionResponse> getPaymentOption(@Field("user_id") String userID);

        @FormUrlEncoded
        @POST("Order/payCODorders")
        Call<CommonResponse> payCODOrder(@FieldMap HashMap<String, String> map);

        @FormUrlEncoded
        @POST("generateChecksum.php")
        Call<CheckSum> getChecksum(@Field("MID") String mId,
                                   @Field("ORDER_ID") String orderId,
                                   @Field("CUST_ID") String custId,
                                   @Field("CHANNEL_ID") String channelId,
                                   @Field("TXN_AMOUNT") String txnAmount,
                                   @Field("WEBSITE") String website,
                                   @Field("CALLBACK_URL") String callbackUrl,
                                   @Field("INDUSTRY_TYPE_ID") String industryTypeId);

        @FormUrlEncoded
        @POST("User/getDeliveryProfile")
        Call<ProfileDetailsResponse> getDeliveryProfile(@Field("user_id") String userID);


        @FormUrlEncoded
        @POST("v1/orders")
        Call<String> setRazorPayOrder(@Field("amount") double Amount,@Field("currency") String currency,@Field("receipt") String receipt,@Field("payment_capture") String payment_capture);

    }

}
