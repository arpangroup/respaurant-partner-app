package com.example.mainactivity.commons;

import com.google.android.gms.maps.model.LatLng;

public class Constants {
    public static final int GPS_REQUEST_CODE = 100;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static final int PLAY_SERVICE_ERROR_CODE = 102;
    public static final int REQUEST_GOOGLE_PLACE_AUTOCOMPLETE_SEARCH_ACTIVITY = 103;
    public static final int REQUEST_CALL_ACTIVITY = 104;
    public static final int REQUEST_IMAGE = 105;
    public static final int REQUEST_IMAGE_CAPTURE = 106;
    public static final int REQUEST_IMAGE_EXTERNAL = 107;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 108;
    public static final int REQUEST_NO_INTERNET_ACTIVITY = 200;


    private static LatLng DEFAULT_LOCATION_KOLKATA = new LatLng(22.5714427, 88.3428709);
    private static LatLng DEFAULT_LOCATION_WESTBENGAL = new LatLng(22.9868, 87.8550);
    public static LatLng DEFAULT_LOCATION = DEFAULT_LOCATION_WESTBENGAL;
    public static final String WEBSITE_URL = "https://admin.pureeats.in/";
    public static final String ASSET_URL_DELIVERY_GUY = WEBSITE_URL + "assets/img/delivery/";
    public static final String GOOGLE_MAP_API_KEY = "AIzaSyDHOCpobHOC5KsYSoIMV5l7i6s-iQxCIXA";

    public static final int ORDER_ACCEPT_WAITING_TIME = 1000*60*5;//5-minute
//    public static final int ORDER_ACCEPT_WAITING_TIME = 1000*30;//30-sec
    public static final int ORDER_READY_WAITING_TIME = 1000*60*15;//15-min
    public static final int FOOD_PREPARE_TIME_MAX = 5; //5-min
    public static final int FOOD_PREPARE_TIME_MIN = 0;//0-min
}
