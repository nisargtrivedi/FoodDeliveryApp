package com.kukdudelivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.kukdudelivery.ApiController.LoginResponse;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityOTP extends BaseActivity {

    TTextView Verify;
    EEditText Ed1,Ed2,Ed3,Ed4;
    TTextView PhoneTv;
    TTextView btnResendOTP;

    String no;
    String phone;

    String PageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Verify=findViewById(R.id.Verify);
        Ed1=findViewById(R.id.Ed1);
        Ed2=findViewById(R.id.Ed2);
        Ed3=findViewById(R.id.Ed3);
        Ed4=findViewById(R.id.Ed4);

        PhoneTv=findViewById(R.id.PhoneTv);
        btnResendOTP=findViewById(R.id.btnResendOTP);
        init();

        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verify();
            }
        });

    }
    private void init(){
        phone=getIntent().getStringExtra("mobile");
        PageName=getIntent().getStringExtra("PAGE");
        PhoneTv.setText(phone);

        Ed1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    Ed2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Ed2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    Ed3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Ed3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    Ed4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void Verify(){

        if(TextUtils.isEmpty(Ed1.getText().toString().trim())){
            openAlert("Enter Proper OTP");
        }
        else if(TextUtils.isEmpty(Ed2.getText().toString().trim())){
            openAlert("Enter Proper OTP");
        }
        else if(TextUtils.isEmpty(Ed3.getText().toString().trim())){
            openAlert("Enter Proper OTP");
        }
        else if(TextUtils.isEmpty(Ed4.getText().toString().trim())){
            openAlert("Enter Proper OTP");
        }else{
            no=Ed1.getText().toString()+Ed2.getText().toString()+Ed3.getText().toString()+Ed4.getText().toString();
            APICALL();
        }
    }

    private void APICALL(){
        try{
            if(!Utility.isNetworkAvailable(this)){
                showInfoMsg("please check internet connection");
            }else{
                Utility.showProgress(ActivityOTP.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<LoginResponse> responseCall=login.otpForgotPassword(phone,no);
                responseCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        Utility.hideProgress();
                        if(response.isSuccessful()){
                            LoginResponse memberResponse=response.body();
                            if(memberResponse.isValid()){
                                appPreferences.set("MOBILE",memberResponse.usertbl.UserMobile);

                                if(PageName.equalsIgnoreCase("FORGOT")){
                                    startActivity(new Intent(ActivityOTP.this,ActivityChangePassword.class));
                                }else {
                                    appPreferences.set("USERID",memberResponse.usertbl.UserID);
                                    appPreferences.set("USERNAME",memberResponse.usertbl.UserName);
                                    appPreferences.set("EMAIL",memberResponse.usertbl.UserEmail);
                                    appPreferences.set("USERTYPE",memberResponse.usertbl.UserType);
                                    startActivity(new Intent(ActivityOTP.this, MainActivity.class));
                                }

                                finish();

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
                    }
                });

            }
        }catch (Exception ex){
            showInfoMsg(ex.toString());
        }
    }
}
