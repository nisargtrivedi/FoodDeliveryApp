package com.kukdudelivery.Model;

import com.google.gson.annotations.SerializedName;
import com.mobandme.ada.Entity;

import java.io.Serializable;

public class Usertbl implements Serializable {

    @SerializedName("user_id")
    public String UserID;

    @SerializedName("name")
    public String UserName;

    @SerializedName("mobile")
    public String UserMobile;

    @SerializedName("email")
    public String UserEmail;

    @SerializedName("user_type")
    public String UserType;


}

//{
//        "user": {
//        "user_id": "68",
//        "name": "Nisarg",
//        "mobile": "9978538694",
//        "email": "nisarg@gmail.com",
//        "user_type": "C"
//        },
//        "ResponseCode": 1,
//        "ResponseMsg": "Congratulations! You have registered successfully.",
//        "Result": "True",
//        "ServerTime": "GMT"
//        }
