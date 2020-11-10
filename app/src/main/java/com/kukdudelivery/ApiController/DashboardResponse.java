package com.kukdudelivery.ApiController;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.DashboardInfo;
import com.kukdudelivery.Model.Order_tbl;

import java.util.ArrayList;

import static com.kukdudelivery.util.Utility.notNullEmpty;

public class DashboardResponse extends BaseResponse {

    @SerializedName("dashboard")
    @Expose
    public Dashboard dashboard;

    @Override
    public boolean isValid() {
        return ResponseCode == 1;
    }

    public class Dashboard {

        @SerializedName("total_delivered_orders")
        @Expose
        public String totalDeliveredOrders;
        @SerializedName("total_alloted_orders")
        @Expose
        public String totalAllotedOrders;
        @SerializedName("total_COD")
        @Expose
        public String totalCOD;
        @SerializedName("today_earning")
        @Expose
        public String todayEarning;
        @SerializedName("weekly_earning")
        @Expose
        public String weeklyEarning;
        @SerializedName("COD_orders")
        @Expose
        public ArrayList<DashboardInfo> codOrders;
        @SerializedName("weekly_orders")
        @Expose
        public ArrayList<DashboardInfo> weeklyOrders;
    }
}
