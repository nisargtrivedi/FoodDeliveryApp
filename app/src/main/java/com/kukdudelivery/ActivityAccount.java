package com.kukdudelivery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.kukdudelivery.ApiController.BaseResponse;
import com.kukdudelivery.ApiController.ForgotPasswordResponse;
import com.kukdudelivery.ApiController.LoginResponse;
import com.kukdudelivery.ApiController.ProfileDetailsResponse;
import com.kukdudelivery.util.EEditText;
import com.kukdudelivery.util.TTextView;
import com.kukdudelivery.util.Utility;
import com.kukdudelivery.WebApi.WebServiceCaller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityAccount extends BaseActivity {

    Activity mActivity;

    private void goTo(Class<?> cls) {
        if (cls == MainActivity.class)
            finishAffinity();
        else
            finish();
        startActivity(new Intent(mActivity, cls));
    }

    private void initBottom() {
        ((TextView) findViewById(R.id.tvAccount)).
                setTextColor(ContextCompat.getColor(mActivity, R.color.red_theme_color));

        findViewById(R.id.ic_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(MainActivity.class);
            }
        });
        findViewById(R.id.tvhome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(MainActivity.class);
            }
        });

        findViewById(R.id.ic_recent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(ActivityRecentOrder.class);
            }
        });
        findViewById(R.id.tvRecent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(ActivityRecentOrder.class);
            }
        });

        findViewById(R.id.ic_past).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(CompleteOrdersActivity.class);
            }
        });
        findViewById(R.id.tvDelivered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(CompleteOrdersActivity.class);
            }
        });
    }

    TTextView tvUserName, tvPhone, tvEmail, help, tvChangePassword, tvEdit, asap,tvDocs;
    TTextView tvDeliveryID,tvJoiningDate,tvAddress,tvCity,tvState,tvZIP,tvMobile,tvBloodGroup,tvAdharNo,tvVehicleType,tvVehicleNumber,tvLicenseExpiry,tvLicense;

    List<String> docArray=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        try {
            mActivity = this;
            initBottom();

            tvUserName = findViewById(R.id.tvUserName);
            tvPhone = findViewById(R.id.tvPhone);
            tvEmail = findViewById(R.id.tvEmail);
            tvChangePassword = findViewById(R.id.tvChangePassword);
            tvEdit = findViewById(R.id.tvEdit);
            help = findViewById(R.id.help);
            asap = findViewById(R.id.asap);

            tvDeliveryID = findViewById(R.id.tvDeliveryID);
            tvJoiningDate = findViewById(R.id.tvJoiningDate);
            tvAddress = findViewById(R.id.tvAddress);
            tvCity = findViewById(R.id.tvCity);
            tvState = findViewById(R.id.tvState);
            tvZIP = findViewById(R.id.tvZIP);
            tvMobile = findViewById(R.id.tvMobile);
            tvBloodGroup = findViewById(R.id.tvBloodGroup);
            tvAdharNo = findViewById(R.id.tvAdharNo);
            tvVehicleType = findViewById(R.id.tvVehicleType);
            tvVehicleNumber = findViewById(R.id.tvVehicleNumber);
            tvLicense=findViewById(R.id.tvLicense);
            tvLicenseExpiry=findViewById(R.id.tvLicenseExpiry);
            tvDocs=findViewById(R.id.tvDocs);

            if (appPreferences.getString("USERNAME") != null) {
                tvUserName.setText(appPreferences.getString("USERNAME"));
            }
            if (appPreferences.getString("MOBILE") != null) {
                tvPhone.setText(appPreferences.getString("MOBILE"));
            }
            if (appPreferences.getString("EMAIL") != null) {
                tvEmail.setText(appPreferences.getString("EMAIL"));
            }

            asap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            tvChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ChangePassword();
                    } catch (Exception ex) {
                        showInfoMsg(ex.toString());
                    }
                }
            });
            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EditProfile();
                    } catch (Exception ex) {
                        showInfoMsg(ex.toString());
                    }
                }
            });
            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appPreferences.set("USERID", "");
                    appPreferences.set("USERNAME", "");
                    appPreferences.set("MOBILE", "");
                    appPreferences.set("EMAIL", "");
                    appPreferences.set("USERTYPE", "");
                    appPreferences.set("ZIPCODE", "");
                    appPreferences.set("city", "");
                    appPreferences.set("area", "");
                    try {
                        if (locationProvider != null)
                            locationProvider.disconnect();
                    } catch (Exception ex) {
                        System.out.println("Logout =" + ex.toString());
                    } finally {
                        finishAffinity();
                        startActivity(new Intent(ActivityAccount.this, ActivityLogin.class));
                    }

                }
            });

            tvDocs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ActivityAccount.this,ActivityDocument.class)
                    .putStringArrayListExtra("docs", (ArrayList<String>) docArray)
                    );
                }
            });
        } catch (Exception ex) {
            System.out.println("Error-->" + ex.toString());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileAPICALL();
    }

    public void ChangePassword() {
        try {
            View view = getLayoutInflater().inflate(R.layout.activity_changepassword, null);
            final EEditText edtOldPassword = view.findViewById(R.id.edtOldPassword);
            final EEditText edtNewPassword = view.findViewById(R.id.edtNewPassword);
            TTextView btnUpdate = view.findViewById(R.id.btnUpdate);
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
            dialog.show();
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(edtOldPassword.getText().toString())) {
                        showErrorMsg("Enter Old Password");
                    } else if (TextUtils.isEmpty(edtNewPassword.getText().toString())) {
                        showErrorMsg("Enter New Password");
                    } else {
                        if (!edtNewPassword.getText().toString().equalsIgnoreCase(edtOldPassword.getText().toString())) {
                            showInfoMsg("New password and Retype password not match");
                        } else {
                            changePasswordAPICALL(edtNewPassword.getText().toString(), dialog);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("Change Password Catch -->" + ex.toString());
        }
    }

    private void changePasswordAPICALL(String newPassword, final AlertDialog dialog) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(ActivityAccount.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<ForgotPasswordResponse> responseCall = login.changePassword(appPreferences.getString("MOBILE"), newPassword);
                responseCall.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            BaseResponse loginResponse = response.body();
                            if (loginResponse.isValid()) {
                                showSuccessMsg(loginResponse.ResponseMsg);
                                dialog.dismiss();
                            } else {
                                showErrorMsg(loginResponse.ResponseMsg);
                            }
                        } else {
                            showInfoMsg(response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please check internet connection");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(ChangePasswordAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    public void EditProfile() {
        try {
            View view = getLayoutInflater().inflate(R.layout.activity_editprofile, null);
            final EEditText edtName = view.findViewById(R.id.edtName);
            final EEditText edtEmail = view.findViewById(R.id.edtEmail);
            final EEditText edtJoiningDate = view.findViewById(R.id.edtJoiningDate);
            final EEditText edtContactNo = view.findViewById(R.id.edtContactNo);
            final EEditText edtAdharNo = view.findViewById(R.id.edtAdharNo);
            final EEditText edtBloodGroup = view.findViewById(R.id.edtBloodGroup);
            final EEditText edtDrivingLicense = view.findViewById(R.id.edtDrivingLicense);

            final EEditText edtDrivingLicenseExpiry = view.findViewById(R.id.edtDrivingLicenseExpiry);
            final EEditText edtVehicleType = view.findViewById(R.id.edtVehicleType);
            final EEditText edtVehicleNumber = view.findViewById(R.id.edtVehicleNumber);


            final TTextView btnUpdate = view.findViewById(R.id.btnUpdate);
            final ProgressBar loading = view.findViewById(R.id.loading);

            edtName.setText(appPreferences.getString("USERNAME"));
            edtEmail.setText(appPreferences.getString("EMAIL"));

            edtJoiningDate.setText(tvJoiningDate.getText().toString());
            edtAdharNo.setText(tvAdharNo.getText().toString());
            edtBloodGroup.setText(tvBloodGroup.getText().toString());
            edtDrivingLicense.setText(tvLicense.getText().toString());
            edtContactNo.setText(tvMobile.getText().toString());
            edtDrivingLicenseExpiry.setText(tvLicenseExpiry.getText().toString());
            edtVehicleType.setText(tvVehicleType.getText().toString());
            edtVehicleNumber.setText(tvVehicleNumber.getText().toString());

            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();
            dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
            dialog.show();

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                        showErrorMsg("Enter Email");
                    } else if (TextUtils.isEmpty(edtName.getText().toString())) {
                        showErrorMsg("Enter Name");
                    } else {
                        if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches())
                            openAlert("Please Enter Proper Email Address");
                        else {
                            editProfileAPICALL(edtName.getText().toString(), edtEmail.getText().toString(), dialog, loading);
                        }

                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("Edit Profile --> " + ex.toString());
            sendMail(EditProfileAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

    private void editProfileAPICALL(final String email, final String phone, final AlertDialog alertDialog, final ProgressBar btn) {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(ActivityAccount.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<LoginResponse> responseCall = login.updateProfile(email, phone, appPreferences.getString("USERID"));
                responseCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            LoginResponse loginResponse = response.body();
                            btn.setVisibility(View.GONE);
                            if (loginResponse.isValid()) {
                                showSuccessMsg(loginResponse.ResponseMsg);
                                appPreferences.set("USERNAME", email);
                                appPreferences.set("EMAIL", phone);
                                tvEmail.setText(phone);
                                tvUserName.setText(email);
                            } else {
                                showErrorMsg(loginResponse.ResponseMsg);
                            }
                            alertDialog.dismiss();
                        } else {
                            showInfoMsg(response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("please check internet connection");
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(EditProfileAPIError + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }



    private void getProfileAPICALL() {
        try {
            if (!Utility.isNetworkAvailable(this)) {
                showInfoMsg("please check internet connection");
            } else {
                Utility.showProgress(ActivityAccount.this);
                WebServiceCaller.ApiInterface login = WebServiceCaller.getClient();
                Call<ProfileDetailsResponse> responseCall = login.getDeliveryProfile(appPreferences.getString("USERID"));
                responseCall.enqueue(new Callback<ProfileDetailsResponse>() {
                    @Override
                    public void onResponse(Call<ProfileDetailsResponse> call, Response<ProfileDetailsResponse> response) {
                        Utility.hideProgress();
                        if (response.isSuccessful()) {
                            if(response.body().isValid()){
                                if(response.body().profileModels!=null && response.body().profileModels.size()>0){
                                    tvDeliveryID.setText(response.body().profileModels.get(0).DeliverID+"");
                                    tvAddress.setText(response.body().profileModels.get(0).Address+"");
                                    tvCity.setText(response.body().profileModels.get(0).City+"");
                                    tvAdharNo.setText(response.body().profileModels.get(0).AdharNumber+"");
                                    tvBloodGroup.setText(response.body().profileModels.get(0).BloodGroup+"");
                                    tvJoiningDate.setText(response.body().profileModels.get(0).JoiningDate+"");
                                    tvLicense.setText(response.body().profileModels.get(0).DrivingLicense+"");
                                    tvLicenseExpiry.setText(response.body().profileModels.get(0).DrivingLicenseValidity+"");
                                    tvMobile.setText(response.body().profileModels.get(0).MobileNo+"");
                                    tvVehicleType.setText(response.body().profileModels.get(0).VehicleType+"");
                                    tvVehicleNumber.setText(response.body().profileModels.get(0).VehicleNumber+"");
                                    if(response.body().profileModels.get(0).Documents!=null && response.body().profileModels.get(0).Documents.size()>0){
                                        for (String str:response.body().profileModels.get(0).Documents) {
                                            docArray.add(str);
                                        }
                                    }
                                }
                            }else{
                                showInfoMsg(response.body().ResponseMsg+"");
                            }

                        } else {
                            showInfoMsg(response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<ProfileDetailsResponse> call, Throwable t) {
                        Utility.hideProgress();
                        showErrorMsg("Please check response on server side might be response of request is change.");
                        sendMail(DELIVERY_PROFILE_ERROR + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + t.toString());
                    }
                });

            }
        } catch (Exception ex) {
            showInfoMsg(ex.toString());
            sendMail(DELIVERY_PROFILE_ERROR + "User ID = " + appPreferences.getString("USERID") + "\n" + "Error = " + ex.toString());
        }
    }

}
