package com.kukdudelivery.ApiController;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable {


    @SerializedName("totalRecords")
    public int totalRecords;

    @SerializedName("totalPages")
    public int totalPages;

    @SerializedName("ResponseCode")
    public int ResponseCode;

    @SerializedName("ResponseMsg")
    public String ResponseMsg;

    public abstract boolean isValid();
}
//"totalRecords": 2,
//        "totalPages": 1,
//        "ResponseCode": 1,
//        "ResponseMsg": "Pending Order list.",
//        "Result": "True",
//        "ServerTime": "IST"