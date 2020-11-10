package com.kukdudelivery.Model;

import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.ApiController.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;

public class Order_tbl extends BaseResponse implements Serializable {

    @SerializedName("order_id")
    public String OrderID = "";

    @SerializedName("transaction_id")
    public String TransactionID = "";


    @SerializedName("order_comment")
    public String orderComment = "";

    @SerializedName("user_id")
    public String CustomerID = "";

    @SerializedName("customer_name")
    public String CustomerName = "";

    @SerializedName("order_date")
    public String OrderDate = "";

    @SerializedName("order_status")
    public String OrderStatus = "";

    @SerializedName("order_no")
    public String OrderNo = "";

    @SerializedName("otpcode")
    public String OTP = "";


    @SerializedName("verify_otp")
    public String verifyOTP = "";

    @SerializedName("customer_address")
    public String CustomerAddres = "";

    @SerializedName("customer_mobile")
    public String customerPhone = "";

    @SerializedName("vendor_address")
    public String VendorAddress = "";

    @SerializedName("map_link")
    public String MapLink = "";

    @SerializedName("delivery_time")
    public String DeliveryTime = "";

    @SerializedName("delivery_date")
    public String DeliveryDate = "";

    @SerializedName("item_total")
    public String ItemTotal = "";

    @SerializedName("order_total")
    public String OrderTotal = "";

    @SerializedName("payment_type")
    public String PaymentType = "";

    @SerializedName("payment_status")
    public String PaymentStatus = "";

    @SerializedName("delivery_payment_status")
    public String deliveryPaymentStatus = "";

    @SerializedName("delivery_payment_via")
    public String deliveryPaymentVia = "";

    @SerializedName("vendor_name")
    public String VendorName = "";

    @SerializedName("vendor_mobile")
    public String VendorMobile = "";

    @SerializedName("order_delivered_time")
    public String orderDeliveryTime = "";

    @SerializedName("order_approval_time")
    public String orderApprovalTime = "";

    @SerializedName("order_processing_time")
    public String orderProcessingTime = "";

    @SerializedName("order_cancellation_time")
    public String orderCancelTime = "";

    @SerializedName("products")
    public ArrayList<Product_tbl> productTbls;


    @SerializedName("shipping_mobile")
    public String shippingMobile = "";

    @SerializedName("shipping_address")
    public String shippingAddress = "";

    @SerializedName("shipping_area")
    public String shippingArea = "";

    @SerializedName("shipping_city")
    public String shippingCity = "";

    @SerializedName("shipping_zipcode")
    public String shippingZipcode = "";

    @SerializedName("shipping_country")
    public String shippingCountry = "";

    @SerializedName("wallet_money")
    public String walletMoney = "";

    @SerializedName("paid_amount")
    public String paidAmount = "";

    public String Address = shippingAddress + "," + shippingArea + "," + shippingZipcode + "," + shippingCity + "," + shippingCountry;

    @Override
    public boolean isValid() {
        return false;
    }
}
//
//{
//        "order_id": "15",
//        "user_id": "90",
//        "customer_name": "Sheetal",
//        "order_date": "2019-07-04 11:50:09",
//        "order_status": "Approved",
//        "order_no": "0407201915",
//        "otpcode": "3727",
//        "customer_address": "G 103 Richmond grand, Satellite, Ahmedabad, gujarat, INDIA , 395009",
//        "vendor_address": "Zoya Flats, Sonal Cinema Road, Near Torrent Power Vejalpur, Ahmedabad, Ahmedabad, GUJARAT , INDIA , 396008",
//        "map_link": "https://www.google.com/maps/dir/?api=1&origin=Zoya Flats, Sonal Cinema Road, Near Torrent Power Vejalpur, Ahmedabad, Ahmedabad, GUJARAT , INDIA , 396008&destination=G 103 Richmond grand, Satellite, Ahmedabad, gujarat, INDIA , 395009&dir_action=navigate",
//        "delivery_time": "5:00 pm - 8:00 pm",
//        "item_total": "520",
//        "extra_charges": "",
//        "packing_charges": "0",
//        "delivery_charges": "0",
//        "GST": "0",
//        "order_total": "520",
//        "payment_type": "cash",
//        "payment_status": "Pending",
//        "order_approval_time": "2019-07-04 15:21:37",
//        "vendor_name": "Ahmedabad Online Fresh Fish Shop",
//        "vendor_mobile": "99988888",
//        "transaction_id": "",
//        "products": [
//        {
//        "product_id": "13",
//        "product_price": "260",
//        "quantity": "2",
//        "product_total": "520",
//        "cuttype": "Thin Sliced - Without Head",
//        "product_name": "Chicken Curry Cut",
//        "regular_price": "260",
//        "sell_price": "0"
//        }
//        ]
//        }
