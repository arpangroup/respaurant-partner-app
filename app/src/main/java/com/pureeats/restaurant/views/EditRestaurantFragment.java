package com.pureeats.restaurant.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.commons.Constants;
import com.pureeats.restaurant.databinding.FragmentEditRestaurantBinding;
import com.pureeats.restaurant.util.FormatTime;
import com.pureeats.restaurant.util.InputFilterMinMax;
import com.pureeats.restaurant.viewmodels.AddressViewModel;
import com.pureeats.restaurant.viewmodels.RestaurantViewModel;
import com.pureeats.restaurant.views.location.LocationActivity;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class EditRestaurantFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private static final String MIN_DELIVERY_TIME = "1";
    private static final String MAX_DELIVERY_TIME = "59";
    private static final String MIN_DELIVERY_CHARGE = "0";
    private static final String MAX_DELIVERY_CHARGE = "1000";

    private FragmentEditRestaurantBinding mBinding;
    RestaurantViewModel restaurantViewModel;
    AddressViewModel addressViewModel;
    private NavController navController;

    private Bitmap mBitmap = null;
    private Uri mFilePath = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEditRestaurantBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        restaurantViewModel.init();
        addressViewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);
        addressViewModel.init(requireActivity());

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Initialize RecyclerView
        //menuItemAdapter = new MenuItemAdapter(this);
        mBinding.businessExtra.layoutCommission.setVisibility(View.GONE);
        mBinding.businessExtra.btnCommissionIncrease.setVisibility(View.VISIBLE);
        mBinding.businessExtra.btnCommissionDecrease.setVisibility(View.VISIBLE);
        mBinding.businessExtra.layoutFixDeliveryCharge.setVisibility(View.GONE);
        mBinding.businessExtra.textInputStoreChargeLayout.setVisibility(View.GONE);
        mBinding.btnRegister.setVisibility(View.GONE);
        mBinding.businessExtra.layoutDynamicDeliveryCharge.setVisibility(View.GONE);
        mBinding.businessDetails.country.setEnabled(false);
        mBinding.businessDetails.country.setClickable(false);

        mBinding.toolbar.back.setOnClickListener(view -> {
            navController.popBackStack();
        });

        // Get CurrentTime
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mBinding.businessDetails.btnOpenTime.setOnClickListener(view -> {
            /*
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (timePicker, selectedHour, selectedMinute) -> {
                mBinding.businessDetails.etOpeningTime.setText(CommonUtils.getFormattedTime(selectedHour, selectedMinute));
            }, hour, minute, false);
            timePickerDialog.setTitle("Select Opening Time");
            timePickerDialog.show();
             */
        });


        mBinding.businessDetails.btnCloseTime.setOnClickListener(view -> {
            /*
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireActivity(), (timePicker, selectedHour, selectedMinute) -> {
                mBinding.businessDetails.etOpeningTime.setText(CommonUtils.getFormattedTime(selectedHour, selectedMinute));
            }, hour, minute, false);
            timePickerDialog.setTitle("Select Closing Time");
            timePickerDialog.show();
             */
        });

        mBinding.businessDetails.btnChangeDeliveryTime.setOnClickListener(view -> {
            Log.d(TAG, "Clicked Change delivery time");
            final EditText input = new EditText(requireActivity());
            input.setFilters(new InputFilter[]{new InputFilterMinMax(MIN_DELIVERY_TIME, MAX_DELIVERY_TIME)});
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(24, 60, 24, 24);
            input.setLayoutParams(layoutParams);

            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
            alert.setView(input);
            alert.setTitle("Approx. Delivery Time(Minute)");
            alert.setCancelable(false);
            alert.setPositiveButton("SET",  (dialogInterface, i) -> {
                String value = "1";
                if(input.getText() != null){
                    value = input.getText().toString();
                    if(value.equals("")) value = "1";
                    mBinding.businessDetails.etDeliveryTime.setText(value +" min");
                }
            });
            alert.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        });

        restaurantViewModel.getRestaurantDetails().observe(requireActivity(), restaurant -> {
            mBinding.toolbar.title.setText(restaurant.getName());

            // BusinessDetails:
            mBinding.businessDetails.etStoreName.setText(restaurant.getName());
            mBinding.businessDetails.etDescription.setText(restaurant.getDescription());
            mBinding.businessDetails.image.setImageResource(R.drawable.foodimg_1);
            Picasso.get().load(Constants.WEBSITE_URL + restaurant.getImage()).into(mBinding.businessDetails.image);
            mBinding.businessDetails.etAddress.setText(restaurant.getAddress());
            mBinding.businessDetails.etLatitude.setText(restaurant.getLatitude());
            mBinding.businessDetails.etLongitude.setText(restaurant.getLongitude());
            mBinding.businessDetails.etOpeningTime.setText(FormatTime.formatTime(restaurant.getOpeningTime()));
            mBinding.businessDetails.etClosingTime.setText(FormatTime.formatTime(restaurant.getClosingTime()));
            mBinding.businessDetails.etContactNumber.setText(restaurant.getContactNumber());
            //mBinding.etPriceRange.setText(restaurant.getPriceRange());
            //mBinding.etLandmark.setText(restaurant.getLandmark());
            //mBinding.etPincode.setText(restaurant.getPinCode());
            mBinding.businessDetails.etDeliveryTime.setText(restaurant.getDeliveryTime() +" min");

            //BusinessExtras:
            mBinding.businessExtra.etCertificate.setText(restaurant.getCertificate());
            mBinding.businessExtra.etStoreCharge.setText(restaurant.getRestaurantCharges());
            mBinding.businessExtra.etMinOrderPrice.setText(restaurant.getMinOrderPrice());
            if(restaurant.getIsPureVeg() == 1)mBinding.businessExtra.isPureVeg.setChecked(true);
            mBinding.businessExtra.etCommissionRate.setText(restaurant.getCommissionRate() + "%");
            //if(restaurant.getDeliveryType() == 1)mBinding.etDeliveryType.setText("Delivery");
            //else if(restaurant.getDeliveryType() == 2)mBinding.etDeliveryType.setText("Self PickUp");
            //else mBinding.etDeliveryType.setText("Both Delivery & Self Pickup");
            mBinding.businessExtra.etDeliveryRadius.setText(restaurant.getDeliveryRadius() +" \nKM");
            mBinding.businessExtra.etDeliveryCharge.setText(com.pureeats.restaurant.util.Constants.RUPEE_SYMBOL + restaurant.getDeliveryCharges());

            mBinding.businessExtra.etBaseDeliveryCharge.setText(restaurant.getBaseDeliveryCharge());
            mBinding.businessExtra.etBaseDeliveryDistance.setText(restaurant.getBaseDeliveryDistance()+"");
            mBinding.businessExtra.etExtraDeliveryCharge.setText(restaurant.getExtraDeliveryCharge());
            mBinding.businessExtra.etExtraDeliveryDistance.setText(restaurant.getExtraDeliveryDistance()+"");

        });


        mBinding.businessExtra.btnCommissionDecrease.setOnClickListener(view -> {
            /*
            String prevStr = mBinding.businessExtra.etCommissionRate.getText().toString().replace("%", "");
            double prevVal  = Double.parseDouble(prevStr);
            prevVal -= 0.5;

//            if (prevVal == 1) mBinding.businessExtra.btnCommissionDecrease.setEnabled(false);
//            else mBinding.businessExtra.btnCommissionDecrease.setEnabled(true);
            if(prevVal < 1){
                //mBinding.businessExtra.etCommissionRate.setText("1%");
            }else{
                mBinding.businessExtra.etCommissionRate.setText(FormatPrice.formatDecimalPoint(prevVal) + "%");
            }
             */
        });

        mBinding.businessExtra.btnCommissionIncrease.setOnClickListener(view -> {
            /*
            String prevStr = mBinding.businessExtra.etCommissionRate.getText().toString().replace("%", "");
            double prevVal  = Double.parseDouble(prevStr);
            prevVal += 0.5;
//            if(prevVal == 99)mBinding.businessExtra.btnCommissionIncrease.setEnabled(false);
//            else mBinding.businessExtra.btnCommissionIncrease.setEnabled(true);
            if(prevVal > 99){
                //mBinding.businessExtra.etCommissionRate.setText("99%");
            }else{
                mBinding.businessExtra.etCommissionRate.setText(FormatPrice.formatDecimalPoint(prevVal) + "%");
            }
             */
        });

        mBinding.businessExtra.decreaseRadius.setOnClickListener(view -> {
            /*
            String prevStr = mBinding.businessExtra.etDeliveryRadius.getText().toString().replace("\nKM", "");
            double prevVal  = Double.parseDouble(prevStr);
            prevVal -= 0.5;

//            if (prevVal == 1) mBinding.businessExtra.btnCommissionDecrease.setEnabled(false);
//            else mBinding.businessExtra.btnCommissionDecrease.setEnabled(true);
            if(prevVal < 1){
                //mBinding.businessExtra.etCommissionRate.setText("1%");
            }else{
                mBinding.businessExtra.etDeliveryRadius.setText(FormatPrice.formatDecimalPoint(prevVal) + "\nKM");
            }
             */
        });
        mBinding.businessExtra.increaseRadius.setOnClickListener(view -> {
            /*
            String prevStr = mBinding.businessExtra.etDeliveryRadius.getText().toString().replace("\nKM", "");
            double prevVal  = Double.parseDouble(prevStr);
            prevVal += 0.5;
//            if(prevVal == 99)mBinding.businessExtra.btnCommissionIncrease.setEnabled(false);
//            else mBinding.businessExtra.btnCommissionIncrease.setEnabled(true);
            if(prevVal > 99){
                //mBinding.businessExtra.etCommissionRate.setText("99%");
            }else{
                mBinding.businessExtra.etDeliveryRadius.setText(FormatPrice.formatDecimalPoint(prevVal) + "\nKM");
            }
             */
        });

        mBinding.businessExtra.radiobuttonDelivery.setOnClickListener(view -> mBinding.businessExtra.radiobuttonBoth.setChecked(false));
        mBinding.businessExtra.radiobuttonSelfPickup.setOnClickListener(view -> mBinding.businessExtra.radiobuttonBoth.setChecked(false));
        mBinding.businessExtra.radiobuttonBoth.setOnCheckedChangeListener((compoundButton, b) -> {
            if(compoundButton.isChecked()){
                mBinding.businessExtra.radiobuttonDelivery.setChecked(false);
                mBinding.businessExtra.radiobuttonSelfPickup.setChecked(false);
            }
        });

        mBinding.businessDetails.btnChangeImage.setOnClickListener(view -> {
            verifyPermissions();
        });

        mBinding.businessDetails.layoutAddress.setOnClickListener(view -> {
            LatLng latLngRestaurant = null;
            try{
                double lat = Double.parseDouble(mBinding.businessDetails.etLatitude.getText().toString());
                double lng = Double.parseDouble(mBinding.businessDetails.etLongitude.getText().toString());
                latLngRestaurant = new LatLng(lat, lng);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(latLngRestaurant != null){
                addressViewModel.setCurrentLocation(latLngRestaurant);
                Log.d(TAG, "########################### RESTAURANT_LOCATION ####################");
                Log.d(TAG, "\nLATLNG: " + latLngRestaurant);
                Log.d(TAG, "\n##################################################################");
                Intent intent = new Intent(requireActivity(), LocationActivity.class);
                intent.putExtra(LocationActivity.INTENT_EXTRA_LATITUDE, latLngRestaurant.latitude);
                intent.putExtra(LocationActivity.INTENT_EXTRA_LONGITUDE, latLngRestaurant.longitude);
                requireActivity().startActivity(intent);
            }else{
                Toast.makeText(requireActivity(), "Invalid Location", Toast.LENGTH_SHORT).show();
            }
        });

        mBinding.businessExtra.btnChangeDeliveryCharge.setOnClickListener(view -> {
            final EditText input = new EditText(requireActivity());
            input.setFilters(new InputFilter[]{new InputFilterMinMax(MIN_DELIVERY_CHARGE, MAX_DELIVERY_CHARGE)});
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(24, 60, 24, 24);
            input.setLayoutParams(layoutParams);

            AlertDialog.Builder alert = new AlertDialog.Builder(requireActivity());
            alert.setView(input);
            alert.setTitle("Fix Delivery Charge");
            alert.setCancelable(false);
            alert.setPositiveButton("SET",  (dialogInterface, i) -> {
                String value = "1";
                if(input.getText() != null){
                    value = input.getText().toString();
                    if(value.equals("")) value = "1";
                    mBinding.businessExtra.etDeliveryCharge.setText(com.pureeats.restaurant.util.Constants.RUPEE_SYMBOL + value);
                }
            });
            alert.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        });
    }








    private void selectImage(){
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constants.REQUEST_IMAGE);
    }
    private void verifyPermissions(){
        Log.d(TAG, "Inside verifyPermissions().......");
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if(ContextCompat.checkSelfPermission(requireActivity(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED){

            selectImage();
        }else{
            ActivityCompat.requestPermissions(requireActivity(), permissions, Constants.MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            mFilePath = uri;
            Log.d(TAG, "URI: " + uri);
            //profile_image.invalidate();
            //profile_image.setImageResource(R.drawable.ic_star_gray);
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                mBinding.businessDetails.image.setImageBitmap(mBitmap);
//
//                File file = FileUtils.getFile(this, uri);// /storage/emulated/0/DCIM/Screenshots/Screenshot_2020-08-10-18-48-48-601_com.arpangroup.pureeats.jpg
//                Log.d(TAG, "FILE: "+file);
//                //Picasso.get().load(file).into(profile_image);
//                InputStream imageStream = getContentResolver().openInputStream(uri);
//                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                profile_image.setImageBitmap(selectedImage);
//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        verifyPermissions();
    }


}