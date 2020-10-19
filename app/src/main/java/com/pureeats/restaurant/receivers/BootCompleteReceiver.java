package com.pureeats.restaurant.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.pureeats.restaurant.commons.Actions;
import com.pureeats.restaurant.services.FetchOrderService;
import com.pureeats.restaurant.sharedpref.ServiceTracker;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == Intent.ACTION_BOOT_COMPLETED && ServiceTracker.getServiceState(context) == ServiceTracker.ServiceState.STARTED){
            Intent endlessService = new Intent(context, FetchOrderService.class);
            endlessService.setAction(Actions.START.name());
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Log.d(TAG, "Starting the service in >=26 Mode from a BroadcastReceiver");
                context.startForegroundService(endlessService);
                return;
            }
            Log.d(TAG, "Starting the service in < 26 Mode from a BroadcastReceiver");
            context.startService(endlessService);
        }
    }
}
