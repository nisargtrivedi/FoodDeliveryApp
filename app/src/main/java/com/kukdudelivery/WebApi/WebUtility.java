package com.kukdudelivery.WebApi;


public class WebUtility {

//    private static String BASE = "http://216.10.242.66/~kukdu6of/";

    public static String RAZORPAY_LIVE="rzp_live_xpo8h8JawdRo5A";
    public static String RAZORPAY_LIVE_SECRET="R6HtiOWt6CSlmvlCRPmJVnbZ";

    private static String BASE = "http://kukdu.in/";

    private static String DEV_VERSION = "dev";
    private static String LIVE_VERSION = "admin";

    //Live
//    public static String BASE_URL = BASE + LIVE_VERSION + "/WebAPI/";
//    public static String BASE_URL_PAYTM = BASE + LIVE_VERSION + "/Paytm_App_Checksum/";

    //Dev
    public static String BASE_URL = BASE + DEV_VERSION + "/WebAPI/";
    public static String BASE_URL_PAYTM = BASE + DEV_VERSION + "/Paytm_App_Checksum/";

    public static String RAZORPAY_URL = "https://api.razorpay.com/";



    //region paytm live credentials
    public final static String PAYTM_MERCHANT_ID = "jnPbDy42525351382302"; // live key
    public static final String WEBSITE = "DEFAULT";
    //endregion

    //region paytm test credentials
//    public final static String PAYTM_MERCHANT_ID = "FfONrz89818241564610"; // new test key
//    public static final String WEBSITE = "WEBSTAGING";
    //endregion

    public static final String CHANNEL_ID = "WAP"; //Paytm Channel Id, got it in paytm credentials
    public static final String INDUSTRY_TYPE_ID = "Retail"; //Paytm industry type got it in paytm credential

//    public final static String PAYTM_MERCHANT_ID = "vOxwxQ62678858982352"; //test key

    public static final String CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";


}
