package com.kukdudelivery.util;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kukdudelivery.ApiController.ForgotPasswordResponse;
import com.kukdudelivery.WebApi.WebServiceCaller;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = AppService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    GoogleApiClient mGoogleApiClient;
    ThreadDemo td;
    AppPreferences appPreferences;


    public class LocationServiceBinder extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(12345678, getNotification());
        } else {
            startForeground(12345678, getNotification());
        }

        td = new ThreadDemo(60000);
        td.start();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appPreferences = new AppPreferences(this);
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.d(TAG, "Connected to Google API");
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "== Error On onConnected() Permission not grantd");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        System.out.println("CALLED TIME Method ============>");
        if (location != null) {
            Log.d(TAG, "== location != null");
            int speed = (int) ((location.getSpeed() * 3600) / 1000);
            int mspeed = (int) (speed / 1.609344);
            double s = ((location.getSpeed() * 3600) / 1000) / 1.609344;
            if (mspeed > 0) {
                if (mspeed > 5) {
                    Toast.makeText(this, "you are going to fast", Toast.LENGTH_LONG).show();
                }
            }
            sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), s + "");
        }

    }


    double oldLat = 0;
    double oldLng = 0;
    double curTime = 0;

    private void sendMessageToUI(String lat, String lng, String speed) {
        changeLocation(lat, lng);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }


    private class ThreadDemo extends Thread {
        long time = 0l;

        public ThreadDemo(long l) {
            this.time = l;
        }

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(time);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.getMessage();
                    return;
                } catch (Exception e) {
                    e.getMessage();
                    return;
                }
            }
        }
    }

    private Notification getNotification() {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "sv_channel1",
                    "sv_channel1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), "sv_channel1");
        } else {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            builder = new Notification.Builder(getApplicationContext());
        }


        return builder.build();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getServiceLocation();
            Log.e("Driver SERVICE", "<><><><>><><><<>>");
        }
    };

    public void getServiceLocation() {
        try {
            if (mGoogleApiClient == null) {
                try {
                    mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isGooglePlayServicesAvailable()) {
            createLocationRequest();
        } else {
            Log.e(TAG, "Not Available ...............................");
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability status = GoogleApiAvailability.getInstance();
        int available = status.isGooglePlayServicesAvailable(getApplicationContext());
        if (available != ConnectionResult.SUCCESS) {
            if (status.isUserResolvableError(available)) {
                status.getErrorDialog((Activity) getApplicationContext(), available, 2404).show();
            } else {
                status.getErrorDialog((Activity) getApplicationContext(), available, 0).show();
            }
            return false;
        }
        return true;
    }

    public void createLocationRequest() {
        Log.e("Creating ", "...Location Request");
        try {
            if (mLocationRequest == null) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(5000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

            } else {
                Log.e("Creating ", "...mLocationRequest NOT NULL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeLocation(String lat, String lng) {
        Log.e("changeLocation", "changeLocation>>>>>>>>>>  true ");
        try {
            if (!Utility.isNetworkAvailable(this)) {
                Utility.showToast("please check internet connection", getApplicationContext());
            } else {
                WebServiceCaller.ApiInterface login = WebServiceCaller.getLocClient();
                Call<ForgotPasswordResponse> responseCall = login.updateLocation(appPreferences.getString("USERID"), appPreferences.getString("ZIPCODE"), appPreferences.getString("city") + appPreferences.getString("area"), lat, lng);
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        try {
                            System.out.println(response.body().ResponseMsg + "");
                        } catch (Exception ex) {

                        }
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    }
                });

            }
        } catch (Exception ex) {
        }

    }

}
