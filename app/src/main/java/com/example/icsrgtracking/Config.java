package com.example.icsrgtracking;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Config {

    public static final String LoginURL ="http://techiephi.website/webservices/login.php";
    public static final String SignUpURL ="http://techiephi.website/webservices/registration.php";
    public static final String UpdateLocationURL ="http://techiephi.website/webservices/user_location.php";

    public static String UserId;
    public static String ReferalCode;
    public static String UniqueCode;
    public static String UserName;
    public static String UserEmail;
    public static String MobileNumber;
    public static String Address;
    public static String City;
    public static String Pincode;
    public static String UserImage;
    public static String BankName;
    public static String BankAcNumber;
    public static String BankBranch;
    public static String BankIFSC;
    public static String OtherDetails;
    public static String UserImagePath;
    public static String ReferredBy;
}
