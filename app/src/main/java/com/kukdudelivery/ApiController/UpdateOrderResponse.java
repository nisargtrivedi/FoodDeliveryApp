package com.kukdudelivery.ApiController;

public class UpdateOrderResponse extends BaseResponse{
    @Override
    public boolean isValid() {
        if(ResponseCode==0)
            return false;
        else
            return true;
    }
}
