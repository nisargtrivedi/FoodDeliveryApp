package com.kukdudelivery.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProfileModel implements Serializable {

    @SerializedName("delivery_id")
    public String DeliverID="";

    @SerializedName("address")
    public String Address="";

    @SerializedName("city")
    public String City="";

    @SerializedName("state")
    public String State="";

    @SerializedName("zipcode")
    public String ZIPCode="";

    @SerializedName("joining_date")
    public String JoiningDate="";

    @SerializedName("emergency_mobile")
    public String MobileNo="";

    @SerializedName("adhar_no")
    public String AdharNumber="";

    @SerializedName("blood_group")
    public String BloodGroup="";

    @SerializedName("driving_licence_no")
    public String DrivingLicense="";

    @SerializedName("driving_licence_validity")
    public String DrivingLicenseValidity="";

    @SerializedName("vehical_type")
    public String VehicleType="";

    @SerializedName("vehical_no")
    public String VehicleNumber="";

    @SerializedName("documents")
    public List<String> Documents;







}
