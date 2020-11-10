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

public class ActivityChangePassword extends BaseActivity{

    EEditText edtOldPassword,edtNewPassword;
    TTextView btnUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        btnUpdate=findViewById(R.id.btnUpdate);

        edtOldPassword=findViewById(R.id.edtOldPassword);
        edtNewPassword=findViewById(R.id.edtNewPassword);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate();
            }
        });

    }

    public void btnUpdate(){
        try {
            if (TextUtils.isEmpty(edtOldPassword.getText().toString().trim())) {
                showErrorMsg("Please type new password");
            } else if (TextUtils.isEmpty(edtNewPassword.getText().toString().trim())) {
                showErrorMsg("Please Retype new password");
            } else {
                if (edtNewPassword.getText().toString().trim().equalsIgnoreCase(edtOldPassword.getText().toString().trim()))
                    APICALL(edtNewPassword.getText().toString().trim());
                else
                    showErrorMsg("New password and Retype password not match");
            }
        }catch (Exception ex){
            sendMail(ChangePasswordAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
        }
    }

    private void APICALL(String newPassword){
        try{
            if(!Utility.isNetworkAvailable(this)){
                showInfoMsg("please check internet connection");
            }else{
                Utility.showProgress(ActivityChangePassword.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<ForgotPasswordResponse> responseCall=login.changePassword(appPreferences.getString("MOBILE"),newPassword);
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        Utility.hideProgress();
                        if(response.isSuccessful()){
                            if(response.body().isValid()) {
                                showErrorMsg(response.body().ResponseMsg);
                                appPreferences.set("USERID","");
                                appPreferences.set("USERNAME","");
                                appPreferences.set("MOBILE","");
                                appPreferences.set("EMAIL","");
                                appPreferences.set("USERTYPE","");
                                finish();startActivity(new Intent(ActivityChangePassword.this,ActivityLogin.class));
                            }
                            else
                                showErrorMsg(response.body().ResponseMsg);


                        }else{
                            showInfoMsg(response.message());
                        }

                    }
                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg(t.getMessage().toString());
                        sendMail(ChangePasswordAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+t.toString());
                    }
                });

            }
        }catch (Exception ex){
            showInfoMsg(ex.toString());
            sendMail(ChangePasswordAPIError+"User ID = "+appPreferences.getString("USERID")+"\n"+"Error = "+ex.toString());
        }
    }
}
