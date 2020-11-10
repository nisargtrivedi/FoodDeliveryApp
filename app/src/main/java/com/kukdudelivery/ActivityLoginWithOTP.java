package com.kukdudelivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.kukdudelivery.ApiController.ForgotPasswordResponse;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLoginWithOTP extends BaseActivity {

    EEditText txtPhone;
    TTextView btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_otp);

        txtPhone=findViewById(R.id.txtPhone);
        btnGo=findViewById(R.id.btnGo);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty(txtPhone.getText().toString())) {
                        openAlert("Please enter mobile number");
                    } else {
                        resetOTPAPI();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    sendMail(LoginOTPAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
                }
            }
        });
    }
    private void resetOTPAPI(){
        try{
            if(!Utility.isNetworkAvailable(this)){
                showInfoMsg("please check internet connection");
            }else{
                Utility.showProgress(ActivityLoginWithOTP.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<ForgotPasswordResponse> responseCall=login.resetPassword(txtPhone.getText().toString().trim());
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        Utility.hideProgress();
                        if(response.isSuccessful()){
                           if(response.body().ResponseCode==1){
                               finish();startActivity(new Intent(ActivityLoginWithOTP.this,ActivityOTP.class).putExtra("mobile",txtPhone.getText().toString()).putExtra("PAGE","LOGIN"));
                           }else{
                               showInfoMsg(response.body().ResponseMsg);
                           }
                        }else{
                            showInfoMsg(response.message());
                        }

                    }
                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please contact admin");
                    }
                });

            }
        }catch (Exception ex){
            showInfoMsg(ex.toString());
            sendMail(LoginOTPAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
        }
    }
}
