package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.mainactivity.App;
import com.example.mainactivity.R;
import com.example.mainactivity.commons.OrderStatus;
import com.example.mainactivity.databinding.ActivityMainBinding;
import com.example.mainactivity.firebase.MessagingService;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.models.request.NewOrderRequest;
import com.example.mainactivity.models.response.Dashboard;
import com.example.mainactivity.services.NewOrderFetchService;
import com.example.mainactivity.util.CommonUtils;
import com.example.mainactivity.viewmodels.OrderViewModel;
import com.example.mainactivity.views.order.AcceptOrderActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private static final long ORDER_REFRESH_INTERVAL = 1000 * 10;
    private final String TAG = this.getClass().getSimpleName();
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
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderViewModel.init();

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



}