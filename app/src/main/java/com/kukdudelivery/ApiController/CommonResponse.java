package com.kukdudelivery.ApiController;


public class CommonResponse extends BaseResponse {

    @Override
    public boolean isValid() {
        return ResponseCode == 1;
    }
}
