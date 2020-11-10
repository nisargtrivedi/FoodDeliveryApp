package com.kukdudelivery.ApiController;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.DateFilterInfo;

import java.util.ArrayList;
import java.util.List;

import static com.kukdudelivery.util.Utility.notNullEmpty;

public class PaymentOptionResponse extends BaseResponse {

    @SerializedName("paymentoption")
    public List<String> list;

    @Override
    public boolean isValid() {
        return ResponseCode == 1 && notNullEmpty(list);
    }
}
