package com.kukdudelivery.ApiController;


import android.widget.ArrayAdapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.DashboardInfo;
import com.kukdudelivery.Model.DateFilterInfo;

import java.util.ArrayList;

import static com.kukdudelivery.util.Utility.notNullEmpty;

public class DateFilterResponse extends BaseResponse {

    @SerializedName("datefilter")
    @Expose
    public ArrayList<DateFilterInfo> arrayList;

    @Override
    public boolean isValid() {
        return ResponseCode == 1 && notNullEmpty(arrayList);
    }
}
