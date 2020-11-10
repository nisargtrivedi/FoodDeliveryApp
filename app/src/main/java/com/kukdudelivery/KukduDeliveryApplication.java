package com.kukdudelivery;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.kukdudelivery.util.AppService;


public class KukduDeliveryApplication extends Application  {

    private static KukduDeliveryApplication mainApplication;
    public AppService gpsService;
    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;

    }


    public static synchronized KukduDeliveryApplication getInstance() {
        return mainApplication;
    }
    public ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();
            System.out.println("SERVICE CLASS NAME ===="+name);
            if (name.endsWith("AppService")) {
                gpsService = ((AppService.LocationServiceBinder) service).getService();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            System.out.println("DISCONNECT SERVICE CLASS NAME ===="+className.getClassName().toString());
            if (className.getClassName().equals("AppService")) {
                gpsService = null;
            }
        }
    };

    public void startServiceMethod(){
        final Intent i = new Intent(this, AppService.class);
        startService(i);
//        this.getApplication().startForegroundService(intent);
        bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopServiceMethod(){
        final Intent i = new Intent(this, AppService.class);
        stopService(i);
//        this.getApplication().startForegroundService(intent);
        unbindService(serviceConnection);
    }

}
