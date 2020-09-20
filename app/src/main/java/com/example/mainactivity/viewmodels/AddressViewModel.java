package com.example.mainactivity.viewmodels;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.repositories.AddressRepository;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.LOCATION_SERVICE;

public class AddressViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private AddressRepository addressRepository;
    private MutableLiveData<LatLng> mutableLatLng = new MutableLiveData<>(Constants.DEFAULT_LOCATION);

    private Context mContext;
    private MutableLiveData<Boolean> mutableIsLocationEnable;
    private MutableLiveData<Boolean> mutableIsGpsEnable;

    public void init(Context context){
        mContext  = context;
//        if (mItems != null){
//            return;
//        }
        addressRepository = AddressRepository.getInstance();
    }


    public LiveData<Boolean> getIsLoading(){
        LiveData<Boolean> isLoading=addressRepository.getIsLoading();
        return isLoading;
    }

    public LiveData<LatLng> getCurrentLocation(){
        return mutableLatLng;
    }
    public void setCurrentLocation(LatLng latLng){
        mutableLatLng.setValue(latLng);
    }

    public void convertCoordinateToAddress(LatLng latLng){
        addressRepository.convertCoordinateToAddress(latLng);
    }
    public LiveData<String> getAddressFromCoordinate(){
        return addressRepository.getAddressFromCoordinate();
    }


    public LiveData<Boolean> isLocationEnabled(){
        if(mutableIsLocationEnable == null){
            mutableIsLocationEnable = new MutableLiveData<>(false);
            boolean result = isLocationPermissionGranted();
            mutableIsLocationEnable.setValue(result);
        }
        return mutableIsLocationEnable;
    }
    public LiveData<Boolean> isGpsOn(){
        if(mutableIsGpsEnable == null){
            mutableIsGpsEnable = new MutableLiveData<>(false);
            boolean result = isGPSEnabled();
            mutableIsGpsEnable.setValue(result);
        }
        return mutableIsGpsEnable;
    }
    public void setLocationEnableStatus(boolean isLocationEnabled){
        if(mutableIsLocationEnable == null){
            mutableIsLocationEnable  =  new MutableLiveData<>(false);
        }
        mutableIsLocationEnable.setValue(isLocationEnabled);

    }
    public void setGpsEnableStatus(boolean isGpsEnabled){
        if(mutableIsGpsEnable == null){
            mutableIsGpsEnable  =  new MutableLiveData<>(false);
        }
        mutableIsGpsEnable.setValue(isGpsEnabled);

    }



    private boolean isLocationPermissionGranted(){
        if(ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
    private boolean isGPSEnabled(){
        final LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }
        return true;
    }
    private boolean isGooglePlayServiceOk(){
        Log.d(TAG, "Inside isGooglePlayServiceOk(): Checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isGooglePlayServiceOk(): GooglePlay Services is working");
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occurred but we can resolve it
            Log.d(TAG, "isGooglePlayServiceOk(): an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) mContext, available, Constants.PLAY_SERVICE_ERROR_CODE);
            dialog.show();
        }else{
            Toast.makeText(mContext, "Play services are required by this application", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
