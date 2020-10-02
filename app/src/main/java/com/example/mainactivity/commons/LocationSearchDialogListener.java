package com.example.mainactivity.commons;

import android.location.Address;
import android.location.Location;

import com.google.android.libraries.places.api.model.Place;

public interface LocationSearchDialogListener {
    public void onCurrentLocationClick(Location location, Address address);
    public void onLocationSearchResult(Place place);
}
