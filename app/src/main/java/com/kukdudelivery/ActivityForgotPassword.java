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

public class ActivityForgotPassword extends BaseActivity {

    TTextView loginlink,btnReset;
    EEditText txtPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        loginlink=findViewById(R.id.loginlink);
        btnReset=findViewById(R.id.btnReset);
        txtPhone=findViewById(R.id.txtPhone);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    btnReset();
                }catch (Exception ex){

                }
            }
        });

        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ActivityForgotPassword.this,ActivityLogin.class));
            }
        });

    }

    private void btnReset(){
        try {
            if (TextUtils.isEmpty(txtPhone.getText().toString())) {
                openAlert("Please enter mobile number");
            } else {
                APICALL();
            }
        }catch (Exception ex){
            sendMail(ForgotPasswordAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
        }
    }

    private void APICALL(){
        try{
            if(!Utility.isNetworkAvailable(this)){
                showInfoMsg("please check internet connection");
            }else{
                Utility.showProgress(ActivityForgotPassword.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<ForgotPasswordResponse> responseCall=login.resetPassword(txtPhone.getText().toString().trim());
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        Utility.hideProgress();
                        if(response.isSuccessful()){
                                if(response.body().isValid()) {
                                    showErrorMsg(response.body().ResponseMsg);
                                    finish();
                                    startActivity(new Intent(ActivityForgotPassword.this, ActivityOTP.class).putExtra("mobile", txtPhone.getText().toString()).putExtra("PAGE", "FORGOT"));
                                }else
                                    showErrorMsg(response.body().ResponseMsg);


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
            sendMail(ForgotPasswordAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
        }
    }
}
