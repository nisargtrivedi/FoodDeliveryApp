package com.kukdudelivery.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DashboardInfo implements Serializable {

    @SerializedName("order_no")
    @Expose
    public String orderNo;
    @SerializedName("order_id")
    @Expose
    public String orderId;
    @SerializedName("order_delivered_time")
    @Expose
    public String orderDeliveredTime;
    @SerializedName("order_total")
    @Expose
    public String orderTotal;


    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("total_delivered_orders")
    @Expose
    public String totalDeliveredOrders;
    @SerializedName("earning")
    @Expose
    public String earning;


    @SerializedName("from_date")
    @Expose
    public String fromDate;
    @SerializedName("to_date")
    @Expose
    public String toDate;
}