package com.example.icsrgtracking.Tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Recurring alarm; requesting location tracking.");
        // start the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent tracking = new Intent(context, UpdateLocation.class);
            context.startForegroundService(tracking);
        } else {
            Intent tracking = new Intent(context, UpdateLocation.class);
            context.startService(tracking);
        }
    }
}