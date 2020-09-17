package com.example.mainactivity.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mainactivity.R;
import com.example.mainactivity.adapters.MenuItemAdapter;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.databinding.FragmentEditCategoryBinding;
import com.example.mainactivity.databinding.FragmentEditRestaurantBinding;
import com.example.mainactivity.viewmodels.RestaurantViewModel;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class EditRestaurantFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentEditRestaurantBinding mBinding;
    RestaurantViewModel restaurantViewModel;
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

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Initialize RecyclerView
        //menuItemAdapter = new MenuItemAdapter(this);

        restaurantViewModel.getRestaurantDetails().observe(requireActivity(), restaurant -> {
            mBinding.toolbar.title.setText(restaurant.getName());

            // BusinessDetails:
            mBinding.businessDetails.etStoreName.setText(restaurant.getName());
            mBinding.businessDetails.etDescription.setText(restaurant.getName());
            mBinding.businessDetails.image.setImageResource(R.drawable.foodimg_1);
            Picasso.get().load(Constants.WEBSITE_URL + restaurant.getImage()).into(mBinding.businessDetails.image);
            mBinding.businessDetails.etAddress.setText(restaurant.getAddress());
            mBinding.businessDetails.etLatitude.setText(restaurant.getLatitude());
            mBinding.businessDetails.etLongitude.setText(restaurant.getLongitude());
            mBinding.businessDetails.etOpeningTime.setText(restaurant.getOpeningTime());
            mBinding.businessDetails.etClosingTime.setText(restaurant.getClosingTime());
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
            mBinding.businessExtra.etDeliveryCharge.setText(com.example.mainactivity.util.Constants.RUPEE_SYMBOL + restaurant.getDeliveryCharges());

            mBinding.businessExtra.etBaseDeliveryCharge.setText(restaurant.getBaseDeliveryCharge());
            mBinding.businessExtra.etBaseDeliveryDistance.setText(restaurant.getBaseDeliveryDistance()+"");
            mBinding.businessExtra.etExtraDeliveryCharge.setText(restaurant.getExtraDeliveryCharge());
            mBinding.businessExtra.etExtraDeliveryDistance.setText(restaurant.getExtraDeliveryDistance()+"");

        });


        mBinding.businessDetails.btnChangeImage.setOnClickListener(view -> {
            verifyPermissions();
        });

        mBinding.businessDetails.layoutAddress.setOnClickListener(view -> {
            Toast.makeText(requireActivity(), "CLICKED: ADDRESS", Toast.LENGTH_SHORT).show();
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