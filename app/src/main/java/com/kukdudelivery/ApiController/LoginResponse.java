package com.kukdudelivery.ApiController;

import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.Usertbl;


public class LoginResponse extends BaseResponse {


    @SerializedName("user")
    public Usertbl usertbl;

    @SerializedName("help")
    public String Help="";

    @Override
    public boolean isValid() {
        if(ResponseCode==1 && usertbl!=null)
            return true;
        else
            return false;
    }

}
