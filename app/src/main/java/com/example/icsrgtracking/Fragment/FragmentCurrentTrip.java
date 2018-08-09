package com.example.icsrgtracking.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.icsrgtracking.BuildConfig;
import com.example.icsrgtracking.Config;
import com.example.icsrgtracking.GPSTracker;
import com.example.icsrgtracking.HomePage;
import com.example.icsrgtracking.LocationMonitoringService;
import com.example.icsrgtracking.LocationTrackingActivity;
import com.example.icsrgtracking.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FragmentCurrentTrip extends Fragment  {
    GPSTracker gps;
    private static final String TAG = "HomeFrag";

    private static GoogleMapOptions options = new GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NORMAL)
            .compassEnabled(true)
            .rotateGesturesEnabled(true)
            .tiltGesturesEnabled(true)
            .zoomControlsEnabled(true)
            .scrollGesturesEnabled(true)
            .mapToolbarEnabled(true);
    protected LocationManager locationManager;
    private static final LocationRequest mLocationRequest = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private GoogleMap mMap;
    private static SupportMapFragment myMapFragment = SupportMapFragment.newInstance(options);
    private FragmentManager myFragmentManager;
    private LatLng current;
    private double dLatitude;
    private double dLongitude;
    private LatLng center;
    private LatLng newLatLng;
    private boolean isInit = true;
    private boolean isMarkerClicked;
    private LatLng latLngClickedMarker;
    private int selectedPosition = -1;


    public FragmentCurrentTrip() {
        // Required empty public constructor
    }

    private HashMap<String, Integer> markerMap;
    protected boolean hasLocationPermissions;
    private Bitmap mapPin;
    protected Resources r;
    protected float px;
    protected int width;
    protected int height;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_currenttrip, container, false);

        gps = new GPSTracker(getActivity());

        r = getResources();
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
        width = r.getDisplayMetrics().widthPixels;
        height = r.getDisplayMetrics().heightPixels;

        initMap(v);
        return v;
    }

    private void initMap(View rootView) {
        markerMap = new HashMap();
        mapPin = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        myFragmentManager = getChildFragmentManager();
        myMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.fragment_map);

        myMapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap mapTemp) {
                Log.i(TAG, "onMapReady: MAP IS LOADED");
                mMap = mapTemp;
                mMap.setPadding(0, 0/*(int) ((100 * px) + mActionBarHeight + getStatusBarHeight())*/, 0, (int) (140 * px));
                initMapOnLoad();
            }
        });

    }

    private void initMapOnLoad() {

            mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        MarkerOptions markerOptions = new MarkerOptions();
            dLatitude = gps.getLatitude();
            dLongitude = gps.getLongitude();
            newLatLng = new LatLng(dLatitude, dLongitude);
            if (dLatitude != 0.0 && dLongitude != 0.0)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 16));

        LatLng latLng = new LatLng(dLatitude, dLongitude);
        markerOptions.position(latLng);
        markerOptions.title("Name");
        mMap.clear();

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gaurd_24));
        markerOptions.getPosition();
        mMap.addMarker(markerOptions);
        center = mMap.getCameraPosition().target;

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {

                if (!isInit) {
                    center = mMap.getCameraPosition().target;
                    if (isMarkerClicked) {

                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
                                + "\n Center : " + center + "\n Marker : " + latLngClickedMarker);
                        if (("" + center.latitude).substring(0, 4).equals(("" + latLngClickedMarker.latitude).substring(0, 4))
                                && ("" + center.longitude).substring(0, 4).equals(("" + latLngClickedMarker.longitude).substring(0, 4))) {
                            isMarkerClicked = false;
                        }
                    } else {
                        isInit = false;
                    }
                }
            }

        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
                            + "\nMarker Clicked : Place : " + marker.getTitle()
                            + "\n\t Position : " + marker.getPosition());
                    latLngClickedMarker = marker.getPosition();
                    isMarkerClicked = true;


                return false;
            }
        });


    }
}



