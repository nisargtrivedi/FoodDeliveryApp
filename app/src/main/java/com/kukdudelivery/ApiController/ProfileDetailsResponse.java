package com.kukdudelivery.ApiController;

import com.google.gson.annotations.SerializedName;
import com.kukdudelivery.Model.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailsResponse extends BaseResponse {

    @Override
    public boolean isValid() {
        return ResponseCode==1?true:false;
    }
    @SerializedName("delivery_profile")
    public List<ProfileModel> profileModels;
}
