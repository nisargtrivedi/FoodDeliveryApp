package com.kukdudelivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.kukdudelivery.ApiController.LoginResponse;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends BaseActivity {

    EEditText Mobile,Password;
    TTextView Login,btnForgotPassword,btnLoginwithOTP;

    //9925834234

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            Login = findViewById(R.id.Login);
            Mobile = findViewById(R.id.Mobile);
            Password = findViewById(R.id.Password);
            btnForgotPassword=findViewById(R.id.btnForgotPassword);
            btnLoginwithOTP=findViewById(R.id.btnLoginwithOTP);

            Login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(Mobile.getText().toString().trim())) {
                        showErrorMsg("please enter mobile number");
                    } else if (TextUtils.isEmpty(Password.getText().toString().trim())) {
                        showErrorMsg("enter password");
                    } else
                        APICALL();
                }
            });
            btnLoginwithOTP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();startActivity(new Intent(ActivityLogin.this,ActivityLoginWithOTP.class));
                }
            });

            btnForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();startActivity(new Intent(ActivityLogin.this,ActivityForgotPassword.class));
                }
            });
        }catch (Exception ex){
            System.out.println("Login Error--->"+ex.toString());
        }
    }
    private void APICALL(){
        try{
            if(!Utility.isNetworkAvailable(this)){
                showInfoMsg("please check internet connection");
            }else{
                Utility.showProgress(ActivityLogin.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<LoginResponse> responseCall=login.Login(Mobile.getText().toString().trim(),Password.getText().toString().trim(),"123","android",appPreferences.getString("DEVICE_KEY"));
                responseCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        Utility.hideProgress();
                        if(response.isSuccessful()){
                            LoginResponse memberResponse=response.body();
                            if(memberResponse.isValid()){
                                appPreferences.set("USERID",memberResponse.usertbl.UserID);
                                appPreferences.set("USERNAME",memberResponse.usertbl.UserName);
                                appPreferences.set("MOBILE",memberResponse.usertbl.UserMobile);
                                appPreferences.set("EMAIL",memberResponse.usertbl.UserEmail);
                                appPreferences.set("USERTYPE",memberResponse.usertbl.UserType);
                                appPreferences.set("HELP",memberResponse.Help);

                                finish();
                                startActivity(new Intent(ActivityLogin.this,MainActivity.class));
                            }else{
                                showErrorMsg(memberResponse.ResponseMsg);
                            }
                        }else{
                            showInfoMsg(response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please contact admin");
                        sendMail(LoginAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+t.toString());
                    }
                });

            }
        }catch (Exception ex){
            showInfoMsg(ex.toString());
            sendMail(LoginAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
        }
    }
}
