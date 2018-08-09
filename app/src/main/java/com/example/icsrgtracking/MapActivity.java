package com.example.icsrgtracking;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.icsrgtracking.Tracking.AlarmReceiver;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.HashMap;

import static com.example.icsrgtracking.database.LocDatabaseHelper.DEBUG_TAG;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap googleMap;
    GPSTracker gps;
    Geocoder geocoder;

    ImageView im_logout;
    UserSessionManager userSessionManager;
    String str_uid = "", strUserName = "";
    int mapLoction=1;

    private static final String TAG = LocationTrackingActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean mAlreadyStartedService = false;
    private TextView mMsgView;

    private PendingIntent tracking;
    private AlarmManager alarms;

    private long UPDATE_INTERVAL = 500;
    private int START_DELAY = 3;

    String strLat="",strLong="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        gps = new GPSTracker(MapActivity.this);

        userSessionManager = new UserSessionManager(MapActivity.this);
        if (userSessionManager.signIn())
            finish();
        final HashMap<String, String> user = userSessionManager.getUserDetails();
        str_uid = user.get(userSessionManager.KEY_USER_ID);
        strUserName = user.get(userSessionManager.KEY_UserName);
        String strName = user.get(userSessionManager.KEY_FIRST_NAME);
        String strEmail = user.get(userSessionManager.KEY__EMAIL);



        try {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        setRecurringAlarm(getBaseContext());


        mMsgView = (TextView) findViewById(R.id.msgView);
        mMsgView.setText("Tracking Started");
        im_logout=(ImageView)findViewById(R.id.im_logout);
        im_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMsgView.setText("Stop tracking");
                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                tracking = PendingIntent.getBroadcast(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarms = (AlarmManager) MapActivity.this.getSystemService(Context.ALARM_SERVICE);
                alarms.cancel(tracking);
                Log.d(DEBUG_TAG, ">>>Stop tracking()");
                userSessionManager.logoutUser();
            }
        });


        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(15000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (mapLoction==1) {
                                    mapLoction=0;
                                    MapFragment mapFragment = (MapFragment) getFragmentManager()
                                            .findFragmentById(R.id.map);

                                    mapFragment.getMapAsync(MapActivity.this);
                                }



                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();


    }
    private void setRecurringAlarm(Context context) {
        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        // add 5 minutes to the calendar object
        cal.add(Calendar.SECOND, START_DELAY);
        Intent intent = new Intent(context, AlarmReceiver.class);
        tracking = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), UPDATE_INTERVAL, tracking);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        MarkerOptions markerOptions = new MarkerOptions();

        double latitude = gps.getLatitude();
        double longitude = gps.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        markerOptions.position(latLng);
        markerOptions.title(strUserName);
        googleMap.clear();

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gaurd_24));
        markerOptions.getPosition();
        googleMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude,
                        longitude)).zoom(15).build();


        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


}


