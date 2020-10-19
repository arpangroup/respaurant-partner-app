package com.pureeats.restaurant.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pureeats.restaurant.api.ApiInterface;
import com.pureeats.restaurant.api.ApiService;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository {
    private final String TAG = this.getClass().getSimpleName();
    private static AddressRepository addressRepository;
    private MutableLiveData<Boolean> isLoading=new MutableLiveData<>();
    private MutableLiveData<String> mutableConvertedAddressFromCoordinate = new MutableLiveData<>(null);

    public static AddressRepository getInstance(){
        if (addressRepository == null){
            addressRepository = new AddressRepository();
        }
        return addressRepository;
    }

    public LiveData<Boolean> getIsLoading(){
        return isLoading;
    }

    public void convertCoordinateToAddress(LatLng latLng){
        convertCoOrdinateToAddressApi(latLng.latitude, latLng.longitude);
    }
    public LiveData<String> getAddressFromCoordinate(){
        return mutableConvertedAddressFromCoordinate;
    }

    private void convertCoOrdinateToAddressApi(double lat, double lng) {
        Log.d(TAG, "Inside convertCoOrdinateToAddress()....");
        Log.d(TAG, "REQUEST: \nLATITUDE: "+lat+"\nLONGITUDE: "+lng);
        ApiInterface apiInterface = ApiService.getApiService();
        isLoading.setValue(true);
        apiInterface.coordinateToAddress(String.valueOf(lat), String.valueOf(lng)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String address = response.body();
                    mutableConvertedAddressFromCoordinate.setValue(address);
                } else {
                    Log.e(TAG, "RESPONSE_UNSUCCESS: convertCoOrdinateToAddress() api return unsuccessful response");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isLoading.setValue(false);
            }
        });
    }



}
