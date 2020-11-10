package com.kukdudelivery.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = LocationProvider.class.getSimpleName();
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    int REQUEST_CHECK_SETTINGS = 100;

    private LocationCallback mLocationCallback;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public Timer timer;
    private TimerTask timerTask;
    private long LOCATION_UPDATE_TIME=15000;

    private Location googlelocation;
    AppPreferences appPreferences;
    Context context;
    public LocationProvider(Context context, LocationCallback callback) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        appPreferences=new AppPreferences(context);
        this.context=context;

        mLocationCallback = callback;

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5*1000)       // 10 seconds, in milliseconds
                .setFastestInterval(10*1000); // 1 second, in milliseconds

        mContext = context;
        //connect();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void removeLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
    public static String formatDateToString(Date date, String format,
                                            String timeZone) {
        // null check
        if (date == null) return null;
        // create SimpleDateFormat object with input format
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // default system timezone if passed null or empty
        if (timeZone == null || "".equalsIgnoreCase(timeZone.trim())) {
            timeZone = Calendar.getInstance().getTimeZone().getID();
        }
        // set timezone to SimpleDateFormat
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        // return Date in required format with timezone as String
        return sdf.format(date);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "LocationData services connected.");

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            final Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


            if (location == null) {
            } else {
                timer = new Timer();

                if(timerTask!=null){
                    timerTask.cancel();
                }

                timerTask = new TimerTask() {
                    int i=0;
                    @Override
                    public void run() {
                        i++;
                        Log.i("COUNTER METHOD---------", i+"");
                        if(i==1) {
                            if(googlelocation!=null) {
                                //getAllData();
                                mLocationCallback.handleNewLocation(googlelocation);
                                Log.i("Update Details---------", googlelocation.getLatitude() + "----" + googlelocation.getLongitude() + "");
                                //sendNotification(123456);
                            }else{
                                //getAllData();
                                //sendNotification(123456);
                            }
                            i=0;
                        }
                    }
                };
                timer.schedule(timerTask, 0, LOCATION_UPDATE_TIME);
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                Activity activity = (Activity) mContext;
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with theConnection Fail error.
             */
            Log.i(TAG, "LocationData services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        googlelocation=location;
        mLocationCallback.handleNewLocation(location);
        if(location!=null) {
            //Toast.makeText(context,location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_LONG).show();
            Log.i("CHANGED LATITUDE--->", location.getLatitude() + "");
            Log.i("CHANGED LONGITUDE--->", location.getLongitude() + "");
        }

    }


    public interface LocationCallback {
        void handleNewLocation(Location location);
    }



}