package com.kukdudelivery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kukdudelivery.util.AppPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ActivitySplash extends BaseActivity {

    AppPreferences appPreferences;
    String currentVersion="0";
    String newVersion="0";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        appPreferences = new AppPreferences(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "2");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        if (ActivityCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivitySplash.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CALL_PHONE},101);
            try {
                System.out.println("USERID=========>"+appPreferences.getString("USERID"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(appPreferences.getString("USERID"))) {
                            finish();
                            startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                        } else {
                            finish();
                            startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                        }
                    }
                }, 5000);
            } catch (Exception ex) {
                System.out.println("Splash Error--->" + ex.toString());
            }
        }
        else {
            try {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(appPreferences.getString("USERID"))) {
                            finish();
                            startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                        } else {
                            finish();
                            startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                        }
                    }
                }, 5000);
            } catch (Exception ex) {
                System.out.println("Splash Error--->" + ex.toString());
            }
        }

        //new FetchAppVersionFromGooglePlayStore().execute();
    }

    class FetchAppVersionFromGooglePlayStore extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }

        protected void onPostExecute(String onlineVersion) {
            newVersion = onlineVersion;
            System.out.println("NEW VERSION ===>"+onlineVersion);
            Log.d("new Version", newVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (onlineVersion.equals(currentVersion)) {
                    if (ActivityCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ActivitySplash.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ActivitySplash.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CALL_PHONE},101);
                        try {
                            System.out.println("USERID=========>"+appPreferences.getString("USERID"));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(appPreferences.getString("USERID"))) {
                                        finish();
                                        startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                                    } else {
                                        finish();
                                        startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                                    }
                                }
                            }, 5000);
                        } catch (Exception ex) {
                            System.out.println("Splash Error--->" + ex.toString());
                        }
                    }
                    else {
                        try {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!TextUtils.isEmpty(appPreferences.getString("USERID"))) {
                                        finish();
                                        startActivity(new Intent(ActivitySplash.this, MainActivity.class));
                                    } else {
                                        finish();
                                        startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));
                                    }
                                }
                            }, 5000);
                        } catch (Exception ex) {
                            System.out.println("Splash Error--->" + ex.toString());
                        }
                    }

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(ActivitySplash.this).create();
                    alertDialog.setTitle("Update");
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    alertDialog.setMessage("New Update is available");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                            }
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }

            }
        }
    }
}
