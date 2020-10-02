package com.example.mainactivity.views.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.commons.LocationDetailsDialogListener;
import com.example.mainactivity.commons.LocationSearchDialogListener;
import com.example.mainactivity.databinding.ActivityLocationBinding;
import com.example.mainactivity.util.GpsUtils;
import com.example.mainactivity.viewmodels.AddressViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSearchDialogListener, LocationDetailsDialogListener {
    private final String TAG = this.getClass().getSimpleName();
    ActivityLocationBinding mBinding;
    AddressViewModel addressViewModel;
    private GoogleMap mMap;

    LocationSearchDialog locationSearchDialog;
    LocationDetailsDialog locationDetailsDialog;

    boolean mLocationEnabled = false;
    boolean mGpsEnabled = false;
    CombinePermissionLiveData combinePermissionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLocationBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        addressViewModel.init(this);
        mBinding.bgOverlay.setVisibility(View.GONE);

        //liveDataMerger.addSource(addressViewModel.isLocationEnabled(), value -> liveDataMerger.setValue(value));
        //liveDataMerger.addSource(addressViewModel.isGpsOn(), value -> liveDataMerger.setValue(value));
        combinePermissionLiveData = new CombinePermissionLiveData(addressViewModel.isLocationEnabled(), addressViewModel.isGpsOn());
        combinePermissionLiveData.observe(this, o -> {
           Pair<Object, Object> pair = (Pair<Object, Object>) o;
           if(pair.first != null && pair.second != null){
               Boolean isLocationEnable = (Boolean) pair.first;
               Boolean isGpsEnable = (Boolean) pair.second;
               mLocationEnabled = isLocationEnable;
               mGpsEnabled = isGpsEnable;
               Log.d(TAG, "-------------------------------------------");
               Log.d(TAG, "PERMISSION_STATUS_LOCATION: "+mLocationEnabled);
               Log.d(TAG, "PERMISSION_STATUS_GPS     : "+mGpsEnabled);
               if(isLocationEnable && isGpsEnable){
                   initGoogleMap();
               }else if(!isLocationEnable){//Request for location permission:
                   ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
               }else if (!isGpsEnable){
                   new GpsUtils(this).turnGPSOn(isGPSEnable -> addressViewModel.setGpsEnableStatus(isGPSEnable));
               }
               else{
                   //
               }
           }
        });

        /*
        addressViewModel.isLocationEnabled().observe(this, aBoolean -> {
            if(aBoolean){
                mLocationEnabled = aBoolean;
                initGoogleMap();
            }else{
                //Request for location permission:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
        addressViewModel.isGpsOn().observe(this, aBoolean -> {
            if(aBoolean){
                mGpsEnabled = aBoolean;
                initGoogleMap();
            }else{
                new GpsUtils(this).turnGPSOn(isGPSEnable -> addressViewModel.setGpsEnableStatus(isGPSEnable));
            }
        });
        */


        addressViewModel.getAddressFromCoordinate().observe(this, address->{
            mBinding.txtAddressHeader.setText(address);
        });

        mBinding.btnChangeAddress.setOnClickListener(view -> {
            locationSearchDialog = new LocationSearchDialog();
            locationSearchDialog.show(getSupportFragmentManager(), "LOCATION_SEARCH_DIALOG");
        });

        mBinding.btnConfirm.setOnClickListener(view -> {
            locationDetailsDialog = new LocationDetailsDialog();
            locationDetailsDialog.show(getSupportFragmentManager(), "LOCATION_DETAILS_DIALOG");
        });
    }

    private void initGoogleMap(){
        Log.d(TAG, "===================initGoogleMap=========================");
        if(mLocationEnabled && mGpsEnabled){
            Log.d(TAG, "All permissions are available to initiate the map");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
            if(mapFragment != null){
                mapFragment.getMapAsync(this);
                //LatLng currentLocation = addressViewModel.getCurrentLocation().getValue();
                //goToLocation(currentLocation);
            }
        }else{
            if(!mLocationEnabled)Log.d(TAG, "Location is not enabled");
            if(!mGpsEnabled)Log.d(TAG, "GPS is not enabled");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initGoogleMap();
    }

    private void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,     // fromXDelta
                0,       // toXDelta
                view.getHeight(), // fromYDelta
                0);      // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
    }
    private void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,    // fromXDelta
                0,      // toXDelta
                0,   // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        view.startAnimation(animate);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //https://stackoverflow.com/questions/14152594/google-map-api-v2-how-do-i-keep-a-marker-in-the-center-of-the-screen-while-use
        //https://stackoverflow.com/questions/36449891/how-to-make-a-custom-place-picker-for-android
        //https://stackoverflow.com/questions/36449891/how-to-make-a-custom-place-picker-for-android/54463362
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        LatLng currentLocation = addressViewModel.getCurrentLocation().getValue();
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        goToLocation(currentLocation);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                LatLng midLatLng = mMap.getCameraPosition().target;
                addressViewModel.convertCoordinateToAddress(midLatLng);
            }
        });

    }


    private void goToLocation(LatLng latLng) {
        if (mMap != null) {
            Log.i(TAG, "Inside goToLocation.........map is not null");

            //mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);
            mMap.animateCamera(cameraUpdate, 1000, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    //DrawMarker.getInstance(AddDeliveryLocationActivity.this).draw(mMap, latLng, R.drawable.ic_home_location, "Origin Location");
                }

                @Override
                public void onCancel() {

                }
            });
            addressViewModel.convertCoordinateToAddress(latLng);
        } else {
            Log.e(TAG, "NULL: GoogleMap Is null inside goToLocation() method");
        }
    }


    @Override
    public void onCurrentLocationClick(Location location, Address address) {
        Log.d(TAG, "Inside onCurrentLocationClick()....");
        if(location != null){
            if(locationSearchDialog != null)locationSearchDialog.dismiss();
            if(locationDetailsDialog != null)locationDetailsDialog.dismiss();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            Log.d(TAG, "ADDRESS: " + address);
            String fullAddress = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = address.getLocality();
            String state = address.getAdminArea();
            String country = address.getCountryName();
            String postalCode = address.getPostalCode();
            String knownName = address.getFeatureName(); // Only if available else return NULL

            String addressHeader = getAddressHeader(address);
            if(addressHeader == null) mBinding.txtAddressHeader.setVisibility(View.GONE);
            else mBinding.txtAddressHeader.setText(addressHeader);
            mBinding.txtAddress.setText(fullAddress);
        }
    }
    private String getAddressHeader(Address address){
        String addressHeader = null;
        if (address.getLocality() != null) addressHeader = address.getLocality();
        else if(address.getCountryName() != null) addressHeader = address.getCountryName();
        else if (address.getAdminArea() != null) addressHeader = address.getAdminArea();
        else if (address.getFeatureName() != null) addressHeader = address.getFeatureName();
        return addressHeader;
    }

    @Override
    public void onLocationSearchResult(Place place) {
        Log.d(TAG, "onLocationSearchResult");
        Log.d(TAG, "PLACE: " + place);
        if(locationSearchDialog != null)locationSearchDialog.dismiss();
        if(locationDetailsDialog != null)locationDetailsDialog.dismiss();
        mBinding.txtAddressHeader.setText(place.getName());
        mBinding.txtAddress.setText(place.getAddress());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
    }

    @Override
    public void onAddressSave() {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            addressViewModel.setLocationEnableStatus(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST_CODE) {
                addressViewModel.setGpsEnableStatus(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}