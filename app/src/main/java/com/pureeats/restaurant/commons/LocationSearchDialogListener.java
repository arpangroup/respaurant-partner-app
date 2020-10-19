package com.pureeats.restaurant.commons;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

public interface LocationSearchDialogListener {
    public void onCurrentLocationClick(LatLng latLng, Address address);
    public void onLocationSearchResult(Place place);
}
