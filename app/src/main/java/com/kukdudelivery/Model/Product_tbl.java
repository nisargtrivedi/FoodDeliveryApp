package com.kukdudelivery.Model;

import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.ApiController.BaseResponse;

import java.io.Serializable;

public class Product_tbl implements Serializable {

    @SerializedName("product_id")
    public String product_id;

    @SerializedName("product_price")
    public String product_price;

    @SerializedName("quantity")
    public String quantity;

    @SerializedName("product_total")
    public String product_total;

    @SerializedName("cuttype")
    public String cuttype;

    @SerializedName("product_name")
    public String product_name;

    @SerializedName("regular_price")
    public String regular_price;

    @SerializedName("sell_price")
    public String sell_price;

    @SerializedName("weight")
    public String weight;
}

//"product_id": "13",
//        "product_price": "260",
//        "quantity": "2",
//        "product_total": "520",
//        "cuttype": "Thin Sliced - Without Head",
//        "product_name": "Chicken Curry Cut",
//        "regular_price": "260",
//        "sell_price": "0"