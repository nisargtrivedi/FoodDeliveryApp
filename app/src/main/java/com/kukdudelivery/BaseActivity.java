package com.kukdudelivery;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.kukdudelivery.ApiController.ForgotPasswordResponse;
import com.kukdudelivery.util.AppPreferences;
import com.kukdudelivery.util.LocationProvider;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;
import com.kukdudelivery.send_mail.GMailSender;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukdudelivery.util.Utility.makeCall;

public class BaseActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    AppPreferences appPreferences;
    LocationProvider locationProvider;

    public static String LocationAPIError = "Method Name = User/addLocation \n";
    public static String LoginAPIError = "Method Name = User/userLogin \n";
    public static String LoginOTPAPIError = "Method Name = User/resendOTP \n";
    public static String ChangePasswordAPIError = "Method Name = User/resetPassword \n";
    public static String EditProfileAPIError = "Method Name = User/updateUserProfile \n";
    public static String ForgotPasswordAPIError = "Method Name = User/resendOTP \n";
    public static String UpdateOrderAPIError = "Method Name = Order/updateOrderStatus \n";
    public static String SearchAPIError = "Method Name = Order/SearchOrder \n";
    public static String PasOrderAPIError = "Method Name = Order/listPastOrders \n";

    public static String DELIVERY_PROFILE_ERROR = "Method Name = user/getDeliveryProfile \n";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = new AppPreferences(this);
        locationProvider = new LocationProvider(this, this);
        locationProvider.connect();
    }

    public void openAlert(String msg) {

        Toasty.normal(this, msg, Toast.LENGTH_SHORT).show();

    }

    public void showSuccessMsg(String msg) {
        Toasty.success(this, msg, Toast.LENGTH_SHORT, true).show();
    }

    public void showErrorMsg(String msg) {
        Toasty.error(this, msg, Toast.LENGTH_SHORT, true).show();
    }

    public void showInfoMsg(String msg) {
        Toasty.info(this, msg, Toast.LENGTH_SHORT, true).show();
    }

    public void showWarningMsg(String msg) {
        Toasty.warning(this, msg, Toast.LENGTH_SHORT, true).show();
    }

    public void showNormalMsg(String msg) {
        Toasty.normal(this, msg, Toast.LENGTH_SHORT).show();
    }


    public void makePhoneCall(String no) {
       /* try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + no));
            startActivity(intent);
        } catch (Exception ex) {
            sendMail("Make Phone Call Permission Error \n " +
                    "User ID = " + appPreferences.getString("USERID") +
                    "\n" + "Error = " + ex.toString());
        }*/
        makeCall(this, no);
    }


    private void APICALL(String lat, String longi) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showToast("please check internet connection", getApplicationContext());
            } else {
                WebServiceCaller.ApiInterface login = WebServiceCaller.getLocClient();
                Call<ForgotPasswordResponse> responseCall = login.updateLocation(appPreferences.getString("USERID"), appPreferences.getString("ZIPCODE"), appPreferences.getString("city") + appPreferences.getString("area"), lat, longi);
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        try {
                            System.out.println(response.body().ResponseMsg + "");
                        } catch (Exception ex) {
                            sendMail(LocationAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
                        }


                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    }
                });

            }
        } catch (Exception ex) {
            sendMail(LocationAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    public void sendMail(final String msg) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender(
                            "kukdudev@gmail.com",
                            "Kukdu@2020");
                    //sender.addAttachment(Environment.getExternalStorageDirectory().getPath()+"/image.jpg");
                    sender.sendMail("Delivery App Report",
                            msg,
                            "kukdudev@gmail.com",
                            "nisarg.trivedi786@gmail.com");
                } catch (Exception e) {
                    System.out.println("EMAIL ERROR" + e.toString());
                    //Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                }
            }

        }).start();
    }

    @Override
    public void handleNewLocation(Location location) {
        APICALL(location.getLatitude() + "", location.getLongitude() + "");
    }
}
