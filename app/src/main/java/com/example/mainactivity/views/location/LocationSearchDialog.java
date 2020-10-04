package com.example.mainactivity.views.location;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.commons.LocationSearchDialogListener;
import com.example.mainactivity.util.ScreenUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LocationSearchDialog extends BottomSheetDialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    private LocationSearchDialogListener mListener;

    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder mGeoCoder = null; //To get address from a location(Latlng)

    private Location mCurrentLocation = null;

    TextView txt_search;
    ImageButton btnClose;
    LinearLayout layout_current_location;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        final View rootView = View.inflate(getContext(), R.layout.layout_location_search, null);
        dialog.setContentView(rootView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)rootView.getParent());
        //bottomSheetBehavior.setPeekHeight(screenUtils.getHeight());
        bottomSheetBehavior.setDraggable(false);

        /*
        final View view = View.inflate(getContext(), R.layout.layout_location_search, null);
        dialog.setContentView(view);
        BottomSheetBehavior bottomSheetBehavior  =  BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(bottomSheetBehavior.STATE_EXPANDED == newState){
                    showView(appBarLayout, getActionBarSize());
                    hideView(linearLayout);
                }

                if(bottomSheetBehavior.STATE_COLLAPSED == newState){
                    hideView(appBarLayout);
                    showView(linearLayout, getActionBarSize());
                }

                if(bottomSheetBehavior.STATE_HIDDEN == newState){
                   dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_location_search, container, false);

        LinearLayout layoutSearch = rootView.findViewById(R.id.layout_search);
        ScreenUtils screenUtils = new ScreenUtils(requireActivity());
        layoutSearch.setMinimumHeight(screenUtils.getHeight());

        txt_search = rootView.findViewById(R.id.txt_search);
        btnClose = rootView.findViewById(R.id.btnClose);
        layout_current_location = rootView.findViewById(R.id.layout_current_location);
        initCurrentLocation();
        initGooglePlaceAutoComplete();
        initClicks();


        return rootView;
    }

    private void initClicks() {
        txt_search.setOnClickListener(view -> {
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY,
                    Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG))
                    .setCountries(Collections.singletonList("IN"))
                    .build(requireActivity());
            startActivityForResult(intent, Constants.REQUEST_GOOGLE_PLACE_AUTOCOMPLETE_SEARCH_ACTIVITY);
        });

        btnClose.setOnClickListener(view -> dismiss());

        layout_current_location.setOnClickListener(view -> {
            mGeoCoder = new Geocoder(requireActivity(), Locale.getDefault());
            try {
                List<Address> addressList = mGeoCoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                if(addressList != null){
                    Address addressObj = addressList.get(0);
                    LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    mListener.onCurrentLocationClick(latLng, addressObj);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initGooglePlaceAutoComplete() {
        Places.initialize(requireActivity(), Constants.GOOGLE_MAP_API_KEY);
    }
    private void initCurrentLocation() {
        Log.d(TAG, "All permissions available");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                mCurrentLocation = location;
            } else {
                Log.d(TAG, "Location is null");
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (LocationSearchDialogListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement LocationSearchDialogListener");
        }
    }



    private int getActionBarSize(){
        final TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(new int[]{
            android.R.attr.actionBarSize
        });
        return (int)typedArray.getDimension(0, 0);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == Constants.REQUEST_GOOGLE_PLACE_AUTOCOMPLETE_SEARCH_ACTIVITY){
            if(resultCode  == requireActivity().RESULT_OK){
                // Initialize places
                Place place = Autocomplete.getPlaceFromIntent(data);

                // set address as editText
                txt_search.setText(place.getAddress());

                //String localityName = String.format(place.getName());
                //LatLng latLng = place.getLatLng();
                mListener.onLocationSearchResult(place);

            }else if (resultCode  == AutocompleteActivity.RESULT_ERROR){
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }else if(resultCode == Activity.RESULT_CANCELED){
                // The user cancelled the operation.
            }
        }
    }
}
