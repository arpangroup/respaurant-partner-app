package com.pureeats.restaurant.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.pureeats.restaurant.App;
import com.pureeats.restaurant.R;
import com.pureeats.restaurant.commons.Actions;
import com.pureeats.restaurant.commons.Constants;
import com.pureeats.restaurant.commons.UpdateHelper;
import com.pureeats.restaurant.databinding.ActivityMainBinding;
import com.pureeats.restaurant.models.response.Dashboard;
import com.pureeats.restaurant.receivers.ConnectivityReceiver;
import com.pureeats.restaurant.services.FetchOrderService;
import com.pureeats.restaurant.sharedpref.ServiceTracker;
import com.pureeats.restaurant.sharedpref.UserSession;
import com.pureeats.restaurant.viewmodels.OrderViewModel;
import com.pureeats.restaurant.views.auth.AuthActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, UpdateHelper.OnUpdateCheckListener{
    private final String TAG = this.getClass().getSimpleName();
    private static final long ORDER_REFRESH_INTERVAL = 1000 * 10;
    private FirebaseRemoteConfig mRemoteConfig;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 500;
    ActivityMainBinding mBinding;
    OrderViewModel orderViewModel;
    NavController navController;

    Dashboard mDashboard = null;


    private boolean isOnGoingOrder = false;
    Timer timer = new Timer();


    @Override
    protected void onStart() {
        super.onStart();
        //LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(MessagingService.MESSAGE_ORDER_STATUS));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        checkInternetConnection();
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();

        if(!UserSession.isLoggedIn()){
            Intent intent = new Intent(this, AuthActivity.class);
            finishAffinity();
            startActivity(intent);
            finish();
        }

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        //NavigationUI.setupActionBarWithNavController(this, navController);
        //startActivity(new Intent(this, AcceptOrderActivity.class));

        if (!Settings.canDrawOverlays(this)) {
            requestPermission();
        }
        //enableAutoStart();


        // Initially load all orders:
        orderViewModel.loadAllAcceptedOrders();

        orderViewModel.getAllAcceptedOrders().observe(this, orders -> {
            if(orders.size() > 0){
                isOnGoingOrder = true;
                checkRunningOrders();
            }else{
                isOnGoingOrder = false;
            }
        });

        // Check if service is running or not
        System.out.println("#################### FETCH_ORDER_SERVICE ######################");
        if(isFetchOrderServiceRunning(FetchOrderService.class)){
            System.out.println("Service is running in background");
            if(ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED){
                System.out.println("As service is already running so no action need to handle");
            }
        }else{
            System.out.println("Service is not running in background");
            if(ServiceTracker.getServiceState(this) == ServiceTracker.ServiceState.STARTED){
                Intent intent = new Intent(this, FetchOrderService.class);
                intent.setAction(Actions.START.name());
                System.out.println("Trying to run the service");
                ContextCompat.startForegroundService(this, intent);
            }
        }
        System.out.println("#############################################################");




        String deviceName = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;
        Log.d(TAG, "---------------------------------------\n");
        Log.d(TAG, "DEVICE_NAME: "+deviceName);
        Log.d(TAG, "DEVICE_MAN: "+deviceMan);
        Log.d(TAG, "DEVICE: "+ Build.DEVICE);
        Log.d(TAG, "BRAND: "+ Build.BRAND);

        Log.d(TAG, "HOST: "+ Build.HOST);
        Log.d(TAG, "ID: "+ Build.ID);
        Log.d(TAG, "PRODUCT: "+ Build.PRODUCT);
        Log.d(TAG, "SERIAL: "+ Build.SERIAL);
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "ANDROID_ID: "+ android_id);
        Log.d(TAG, "VERSION_CODENAME: "+ Build.VERSION.CODENAME);
        Log.d(TAG, "VERSION_INCREMENTAL: "+ Build.VERSION.INCREMENTAL);
        Log.d(TAG, "VERSION_RELEASE(user-visible-version): "+ Build.VERSION.RELEASE);
        Log.d(TAG, "SDK_VERSION: "+ Build.VERSION.SDK_INT);
        Log.d(TAG, "\n---------------------------------------");

//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
//        intent.putExtra("extra_pkgname", getPackageName());
//        startActivity(intent);
    }



    private void checkRunningOrders(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isOnGoingOrder){
                    orderViewModel.loadRunningOrderStatus();
                }else{
                    timer = null;
                }
            }
        }, 0, ORDER_REFRESH_INTERVAL);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        //register connection status listener
        App.getInstance().setConnectivityListener(this);


        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);
    }

    private void requestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + this.getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    //PermissionDenied();
                } else {
                    // Permission Granted-System will work
                }

            }
        }
    }

    private void enableAutoStart() {
        Log.d("APPLICATION_APP: ", "Inside enableAutoStart()....");
        for (Intent intent : App.POWERMANAGER_INTENTS)
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                // show dialog to ask user action
                new AlertDialog.Builder(this)
                        .setTitle("Enable AutoStart")
                        .setMessage("Please allow PureEats to always run in the background,else our services can't be accessed.")
                        .setPositiveButton("ALLOW", (dialogInterface, i) -> {
                            startActivity(intent);
                        })
                        .setNegativeButton("NO", null)
                        .create()
                        .show();
                break;
            }
    }

    public boolean isFetchOrderServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }


    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if(!isConnected){
            changeActivity();
        }
    }

    private void changeActivity() {
        Intent intent = new Intent(this, OfflineActivity.class);
        startActivity(intent);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected){
            changeActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.REQUEST_CALL_ACTIVITY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //makePhoneCall();
            }
        }
    }

    @Override
    public void onUpdateCheckListener(String urlApp) {
        Log.d(TAG, "Inside onUpdateCheckListener............................") ;
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please update to new new version to continue")
                .setPositiveButton("UPDATE", (dialogInterface, i) -> {
                    try {
                        String url = "market://details?id=\" + \"com.pureeats.restaurant";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }catch (ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlApp)));
                    }
                }).create();
//                .setNegativeButton("CANCEL", (dialogInterface, i) -> {
//                    dialogInterface.dismiss();
//                }).create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}