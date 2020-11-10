package com.kukdudelivery.ApiController;


import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.Order_tbl;

import java.util.ArrayList;

import static com.kukdudelivery.util.Utility.notNullEmpty;

public class OrderResponse extends BaseResponse {

    @SerializedName("orders")
    public ArrayList<Order_tbl> orderTbls;

    @Override
    public boolean isValid() {
        return ResponseCode == 1 && notNullEmpty(orderTbls);
    }
}
