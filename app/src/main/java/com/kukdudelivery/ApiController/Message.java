package com.kukdudelivery.ApiController;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message implements Serializable {

    @SerializedName("error")
    public int Error;

    @SerializedName("message")
    public String Message_;

}
