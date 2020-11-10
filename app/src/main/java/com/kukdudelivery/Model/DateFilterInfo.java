package com.kukdudelivery.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DateFilterInfo  implements Serializable {
    @SerializedName("display")
    @Expose
    public String display;
    @SerializedName("search_date")
    @Expose
    public String searchDate;

    public DateFilterInfo(String display) {
        this.display = display;
    }
}
