package com.kukdudelivery.ApiController;

import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.Usertbl;


public class ForgotPasswordResponse extends BaseResponse {


    @Override
    public boolean isValid() {
        if(ResponseCode==1)
            return true;
        else
            return false;
    }

}
