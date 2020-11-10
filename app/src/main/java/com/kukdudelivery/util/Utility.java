package com.kukdudelivery.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import com.kukdudelivery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Utility {
    private static Dialog popupWindow;
    public static final int TEZ_REQUEST_CODE = 123;
    public static final int PAYMENT_REQUEST_CODE = 101;
    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";

    /**
     * Show toast.
     *
     * @param message the message
     * @param context the context
     */
    public static void showToast(String message, Context context) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, pxToDp(70, context));
        toast.show();
    }

    /**
     * Dp to px int.
     *
     * @param dp      the dp
     * @param context the context
     * @return the int
     */
    public static int dpToPx(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    /**
     * Px to dp int.
     *
     * @param px      the px
     * @param context the context
     * @return the int
     */
    public static int pxToDp(float px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    /**
     * Is network available boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static boolean method1(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static boolean method2(Context context) {
//   try{
////                InetAddress ipAddr = InetAddress.getByName("google.com");
////                //You can replace it with your name
////                return !ipAddr.equals("");
//       String command = "ping -c 1 google.com";
//       return (Runtime.getRuntime().exec (command).waitFor() == 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }


        return true;
    }

    /**
     * Gets file extension.
     *
     * @param s the s
     * @return the file extension
     */
    public static String getFileExtension(String s) {
        int start = s.lastIndexOf(".");
        return s.substring(start + 1, s.length());
    }

    /**
     * Gets app storage path.
     *
     * @param context the context
     * @param dir     the dir
     * @return the app storage path
     */
    public static String getAppStoragePath(Context context, String dir) {
        String path;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        } else {
            path = context.getFilesDir() + "/" + context.getString(R.string.app_name);
        }

        String directory = +dir.trim().length() > 0 ? (dir) : "";
        File file = new File(path + directory);
        if (!file.exists()) file.mkdirs();

        return file.getAbsolutePath();
    }

    /**
     * Gets app storage file.
     *
     * @param context the context
     * @param dir     the dir
     * @return the app storage file
     */
    public static File getAppStorageFile(Context context, String dir) {
        String path;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            path = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
        } else {
            path = context.getFilesDir() + "/" + context.getString(R.string.app_name);
        }
        String directory = +dir.trim().length() > 0 ? (dir) : "";
        File file = new File(path + directory);
        if (!file.exists()) file.mkdirs();
        return file;
    }

    /**
     * Is valid email boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidWebsite(String website) {
        if (Patterns.WEB_URL.matcher(website).matches() && (website.startsWith("http://www.") || website.startsWith("https://www."))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Convert time stamp to date string.
     *
     * @param timeStamp the time stamp
     * @param format    the format
     * @return the string
     */
    public static String convertTimeStampToDate(Long timeStamp, String format) {
        String date = "";
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(timeStamp);
//        cal.setTimeInMillis(timeStamp * 1000);
        date = DateFormat.format(format, cal).toString();
        return date;
    }


    public static long convertTimeStampToGMT(long timeStamp) {
        Utility.log("local", timeStamp + "");
        long timestampGMT = 0;
        Calendar cal = Calendar.getInstance(getTimeZone());
        cal.setTimeInMillis(timeStamp);
        timestampGMT = cal.getTimeInMillis();
        Utility.log("GMT", timestampGMT + "");
        return timestampGMT;
    }

    /**
     * Gets time zone.
     *
     * @return the time zone
     */
    public static TimeZone getTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("GMT");
        return tz;
    }

    /**
     * Image to body request body.
     *
     * @param text the text
     * @return the request body
     */
    public static RequestBody imageToBody(String text) {
        RequestBody requestBody;
        if (text != null && text.length() > 0) {
            MediaType MEDIA_TYPE = MediaType.parse("image/*");
            File file = new File(text);
            requestBody = RequestBody.create(MEDIA_TYPE, file);
        } else {
            requestBody = null;
        }
        return requestBody;
    }

    /**
     * Video to body request body.
     *
     * @param text the text
     * @return the request body
     */
    public static RequestBody videoToBody(String text) {
        RequestBody requestBody;
        if (text != null && text.length() > 0) {
            MediaType MEDIA_TYPE = MediaType.parse("video/*");
            File file = new File(text);
            requestBody = RequestBody.create(MEDIA_TYPE, file);
        } else {
            requestBody = null;
        }
        return requestBody;
    }

    /**
     * Text to body request body.
     *
     * @param text the text
     * @return the request body
     */
    public static RequestBody textToBody(String text) {
        RequestBody requestBody = null;
        if (text != null) {
            MediaType MEDIA_TYPE = MediaType.parse("text/plain");
            requestBody = RequestBody.create(MEDIA_TYPE, text);
        } else {
            requestBody = null;
        }
        return requestBody;
    }

    public static RequestBody audioToBody(String text) {
        RequestBody requestBody;
        if (text != null && text.length() > 0) {
            MediaType MEDIA_TYPE = MediaType.parse("audio/*");
            File file = new File(text);
            requestBody = RequestBody.create(MEDIA_TYPE, file);
        } else {
            requestBody = null;
        }
        return requestBody;
    }

    /**
     * Show progress.
     *
     * @param context the context
     */
    public static void showProgress(final Context context) {
        try {
            if (!((Activity) context).isFinishing()) {
                View layout = LayoutInflater.from(context).inflate(R.layout.layout_popup_loading, null);
                popupWindow = new Dialog(context, R.style.ProgressDialog);
                popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupWindow.setContentView(layout);
                popupWindow.setCancelable(false);

                if (!((Activity) context).isFinishing()) {
                    popupWindow.show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show progress with text text view.
     *
     * @param context the context
     * @param msg     the msg
     * @return the text view
     */
    public static TextView showProgressWithText(final Context context, String msg) {
        TextView txtProgressText = null;
        try {
            if (!((Activity) context).isFinishing()) {
                View layout = LayoutInflater.from(context).inflate(R.layout.layout_popup_loading, null);
                popupWindow = new Dialog(context, R.style.ProgressDialog);
                popupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupWindow.setContentView(layout);
                popupWindow.setCancelable(false);
                txtProgressText = (TextView) layout.findViewById(R.id.txtProgressText);
                txtProgressText.setVisibility(View.VISIBLE);
                txtProgressText.setText(msg);
                if (!((Activity) context).isFinishing()) {
                    popupWindow.show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtProgressText;
    }

    /**
     * Hide progress.
     */
    public static void hideProgress() {
        try {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets screen resolution width.
     *
     * @param context the context
     * @return the screen resolution width
     */
    public static int getScreenResolutionWidth(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        Log.e("screen width", screenWidth + "");
        //int screenHeight = displayMetrics.heightPixels;
        return screenWidth;
    }


    /**
     * Gets file name.
     *
     * @param s the s
     * @return the file name
     */
    public static String getFileName(String s) {
        if (s.length() > 0) {
            int start = s.lastIndexOf("/");
            return s.substring(start + 1, s.length());
        } else {
            return "";
        }

    }

    /**
     * Save image string.
     *
     * @param bitmap     the bitmap
     * @param context    the context
     * @param folderName the folder name
     * @param fileName   the file name
     * @return the string
     */
    public static String saveImage(Bitmap bitmap, Context context, String folderName, String fileName) {
        File outputFile = new File(Utility.getAppStorageFile(context, folderName), fileName);
        String path = "";
        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            path = outputFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return path;
    }


    /**
     * check and parse value to int
     *
     * @param a -value to be parsed
     * @return
     */
    public static int parseIntegerValue(String a) {
        int number = 0;
        try {
            number = Integer.parseInt(a);
        } catch (Exception e) {
            e.printStackTrace();
            number = 0;
        }
        return number;
    }

    /**
     * check and parse value to long
     *
     * @param a -value to be parsed
     * @return
     */
    public static long parseLongValue(String a) {
        long number = 0;
        try {
            number = Long.parseLong(a);
        } catch (Exception e) {
            e.printStackTrace();
            number = 0;
        }
        return number;
    }

    /**
     * check and parse value to double
     *
     * @param a -value to be parsed
     * @return
     */
    public static double parseDoubleValue(String a) {
        double number = 0;
        try {
            number = Double.parseDouble(a);
        } catch (Exception e) {
            e.printStackTrace();
            number = 0;
        }
        return number;
    }

    public static void log(String title, String message) {
        if (message != null) {
            Log.e(title + ">>", message);
        } else {
            Log.e(title + ">>", "null");
        }
    }

    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    public static void hideKeyBoard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view == null) {
            view = new View(context);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String convertToTwoDigit(int count) {
        return String.format(Locale.US, "%02d", count);
    }

    public static InputFilter USERNAME_FILTER = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals("_") && !Character.toString(source.charAt(i)).equals("-")) {
                    return "";
                }
            }
            return null;
        }
    };

//    private static DisplayImageOptions listImageOptions = null;
//
//    public static DisplayImageOptions getListImageOptions() {
//        if (listImageOptions == null) {
//            listImageOptions = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.drawable.ic_listingplaceholder)
//                    .showImageForEmptyUri(R.drawable.ic_listingplaceholder)
//                    .showImageOnFail(R.drawable.ic_listingplaceholder)
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .imageScaleType(ImageScaleType.EXACTLY)
//                    .considerExifParams(true)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//                    .build();
//        }
//        return listImageOptions;
//
//    }


    public static String convertArrayListToCommaSeparated(ArrayList<String> list) {
        String s = "";
        for (int i = 0; i < list.size(); i++) {
            if (s.length() == 0) {
                s = list.get(i);
            } else {
                s = s + "," + list.get(i);
            }
        }
        return s;
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), Base64.DEFAULT));
                AppPreferences appPreferences = new AppPreferences(context);
                appPreferences.set("HASH", key);
                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);

            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean notNullEmpty(String str) {
        return str != null && str.length() > 0;
    }

    public static boolean notNullEmpty(String... str) {
        return str != null && str.length > 0;
    }

    public static boolean notNullEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    public static boolean notNullEmpty(ArrayList<?> list) {
        return list != null && list.size() > 0;
    }

    public static void setSwipeColor(SwipeRefreshLayout swipeRefreshLayout) {
        int[] indicatorColorArr = {R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimary};
        swipeRefreshLayout.setColorSchemeResources(indicatorColorArr);
    }

    public static String getCurrencyAmount(double amount) {
        return String.format(Locale.US, "\u20B9 %.2f", amount);
    }

    public static String getRoundedCurrencyAmount(double amount) {
        return String.format(Locale.US, "\u20B9 %.2f", Double.parseDouble(Math.round(amount) + ""));
    }

    public static String getCurrencyAmount(String amount) {
        if (notNullEmpty(amount)) {
            return getCurrencyAmount(Double.parseDouble(amount));
        } else
            return getCurrencyAmount(0);
    }

    public static String getCurrencyAmount(String amount, boolean isNegative) {
        if (notNullEmpty(amount)) {
            String sign = isNegative ? "-" : "+";
            return String.format(Locale.US, "%s\u20B9 %.2f", sign, Double.valueOf(amount));
        } else
            return getCurrencyAmount(0);
    }

    public static String getAmount(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    public static void makeCall(Activity mActivity, String mPhn) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhn));
        mActivity.startActivity(intent);
    }

    public static void Alert(Context activity, String msg, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.app_name));
        builder.setIcon(activity.getDrawable(R.drawable.icon));
        builder.setMessage(msg);
        builder.setPositiveButton("Yes", listener);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void spannableText(Activity mActivity, String s1, String s2, TextView textView) {
        Spannable word = new SpannableString(s1 + ":");
        textView.setText(word);

        if (notNullEmpty(s2)) {
            Spannable wordTwo = new SpannableString(" " + s2);
            wordTwo.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity,
                    R.color.field_color)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordTwo.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.append(wordTwo);
        }
    }

    public static Uri getGPayUri(String amount, String orderId) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", "kukdu@icici")
                .appendQueryParameter("pn", "M H Enterprise")
                .appendQueryParameter("mc", "7299")
                .appendQueryParameter("url", "https://kukdu.in")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .appendQueryParameter("tr", orderId)// order id
                .appendQueryParameter("tn", "")
                .build();
    }

    public static void gotoGPay(Activity mActivity, Uri gPayUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(gPayUri);
        intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
        mActivity.startActivityForResult(intent, TEZ_REQUEST_CODE);
    }

    public static void addDivider(Activity mActivity, RecyclerView recyclerView) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }

}
