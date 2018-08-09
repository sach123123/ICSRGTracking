package com.example.icsrgtracking.Tracking;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.icsrgtracking.Config;
import com.example.icsrgtracking.HomePage;
import com.example.icsrgtracking.database.LocContentProvider;
import com.example.icsrgtracking.database.LocTable;

import org.json.JSONObject;

public class UpdateLocation extends Service implements 
	LocationListener{
	
  private Looper mServiceLooper;
  private ServiceHandler mServiceHandler;
  private final String DEBUG_TAG = "UpdateLocation::Service";
  private LocationManager mgr;
  private String best;
 
  
  // Handler that receives messages from the thread
  private final class ServiceHandler extends Handler {
      public ServiceHandler(Looper looper) {
          super(looper);
      }
      
      @Override
      public void handleMessage(Message msg) {
          Location location = mgr.getLastKnownLocation(best);
          mServiceHandler.post(new MakeToast(trackLocation(location)));
          // Stop the service using the startId, so that we don't stop
          // the service in the middle of handling another job
          stopSelf(msg.arg1);
      }
  }

  @Override
  public void onCreate() {
  	super.onCreate();
	  if (Build.VERSION.SDK_INT >= 26) {
		  String CHANNEL_ID = "my_channel_01";
		  NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
				  "title",
				  NotificationManager.IMPORTANCE_DEFAULT);

		  ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

		  Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
				  .setContentTitle("")
				  .setContentText("").build();

		  startForeground(1, notification);
	  }
    // Start up the thread running the service.  Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block.  We also make it
    // background priority so CPU-intensive work will not disrupt our UI.
    HandlerThread thread = new HandlerThread("ServiceStartArguments",
    		android.os.Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();
    Log.d(DEBUG_TAG, ">>>onCreate()");
    // Get the HandlerThread's Looper and use it for our Handler 
    mServiceLooper = thread.getLooper();
    mServiceHandler = new ServiceHandler(mServiceLooper);
	mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    best = mgr.getBestProvider(criteria, true);
    mgr.requestLocationUpdates(best, 5000, 1, this);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
//      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

      // For each start request, send a message to start a job and deliver the
      // start ID so we know which request we're stopping when we finish the job
      Message msg = mServiceHandler.obtainMessage();
      msg.arg1 = startId;
      mServiceHandler.sendMessage(msg);
      Log.d(DEBUG_TAG, ">>>onStartCommand()");
      // If we get killed, after returning from here, restart
      return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
      // We don't provide binding, so return null
      return null;
  }
  
  @Override
  public void onDestroy() {
//    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show(); 
	  Log.d(DEBUG_TAG, ">>>onDestroy()");
  }

  //obtain current location, insert into database and make toast notification on screen
  private String trackLocation(Location location) {
	  double longitude;
	  double latitude;
	  String time;
	  String result = "Location currently unavailable.";
		
	  // Insert a new record into the Events data source.
	  // You would do something similar for delete and update. 
	  if (location != null)
	  {
  		longitude = location.getLongitude();
  		latitude = location.getLatitude();
  		time = parseTime(location.getTime());
  		ContentValues values = new ContentValues(); 
  		values.put(LocTable.COLUMN_TIME, time);
  		values.put(LocTable.COLUMN_LATITUDE, latitude);    		
  		values.put(LocTable.COLUMN_LONGITUDE, longitude);
  		getContentResolver().insert(LocContentProvider.CONTENT_URI, values);
  		result = "Location: " + Double.toString(longitude)+", "+Double.toString(latitude);
  		UpdateLocationSr(Double.toString(latitude),Double.toString(longitude));
	  }
		return result;
  }
	
	private String parseTime(long t) {
		DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		df.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		String gmtTime = df.format(t);
		return gmtTime;
	}
	
  private class MakeToast implements Runnable {
		String txt;
		
		public MakeToast(String text){
		    txt = text;
		}
		public void run(){

		     Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
		     Log.d("Location",txt);
		}
  }
  
	@Override
	public void onLocationChanged(Location location) {
//		mHandler.post(new MakeToast(trackLocation(location)));
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		Log.w(DEBUG_TAG, ">>>provider disabled: " + provider);
	}


	@Override
	public void onProviderEnabled(String provider) {
		Log.w(DEBUG_TAG, ">>>provider enabled: " + provider);
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.w(DEBUG_TAG, ">>>provider status changed: " + provider);
	}


	public  void UpdateLocationSr(final String latitute, final String longitute) {
		final StringRequest stringRequest = new StringRequest(Request.Method.POST,
				Config.UpdateLocationURL,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String result) {
						//loadingDialog.dismiss();
						try {
							if (null != result) {
								JSONObject obj = new JSONObject(result);
								String Status = obj.getString("status").toString();
								// JSONObject user	Data = obj.getJSONObject("data");
								Log.d("Service Called>>>>>>>>>>>>>>>>>>>>>",""+Status);
								if (Status.equals("true")) {

									Toast.makeText(getBaseContext(),"Updated", Toast.LENGTH_SHORT).show();

								} else {
									//loadingDialog.dismiss();
									Toast.makeText(getBaseContext(),"Invalid    Details", Toast.LENGTH_SHORT).show();
								}

							}

						} catch (Exception e) {
							e.printStackTrace();
							// loadingDialog.dismiss();

						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// loadingDialog.dismiss();
						//Toast.makeText(MainActivity.getActivity(), error.toString(), Toast.LENGTH_LONG).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("lat", latitute);
				params.put("long", longitute);
				params.put("user_id", "1");

				return params;
			}
		};
		RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		stringRequest.setShouldCache(true);
		requestQueue.add(stringRequest);
	}

}