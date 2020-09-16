package com.example.mainactivity.views.menuitem;

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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.databinding.FragmentEditItemBinding;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.request.RequestToken;
import com.example.mainactivity.util.FileUtils;
import com.example.mainactivity.util.InputFilterMinMax;
import com.example.mainactivity.viewmodels.RestaurantViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

public class EditItemFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentEditItemBinding mBinding;
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
        mBinding = FragmentEditItemBinding.inflate(inflater, container, false);
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

        //mBinding.etMarkPrice.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "999")});
        //mBinding.etSellingPrice.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "999")});

        mBinding.toolbar.more.setVisibility(View.GONE);


        restaurantViewModel.getMenuItem().observe(getViewLifecycleOwner(), menuItem -> {
            ItemCategory category = restaurantViewModel.getSelectedCategory().getValue();
            mBinding.toolbar.title.setText(menuItem.getName());
            mBinding.etItemName.setText(menuItem.getName());
            mBinding.etDescription.setText(menuItem.getDesc());
            mBinding.etMarkPrice.setText(menuItem.getOldPrice());
            mBinding.etSellingPrice.setText(menuItem.getPrice());
            mBinding.etCategory.setText(category.getName());
            //mBinding.image.setImageResource(R.drawable.foodimg_1);
            Picasso.get().load(Constants.WEBSITE_URL + menuItem.getImage()).into(mBinding.image);

            mBinding.switchRecommended.setChecked(menuItem.getIsRecommended() == 1);
            mBinding.switchPopular.setChecked(menuItem.getIsPopular() == 1);
            mBinding.switchNew.setChecked(menuItem.getIsNew() == 1);
            mBinding.switchVeg.setChecked(menuItem.getIsVeg() == 1);
        });

        mBinding.btnChangeImage.setOnClickListener(view -> {
            verifyPermissions();
        });

        mBinding.btnUpdate.setOnClickListener(view -> {
            String itemName = String.valueOf(mBinding.etItemName.getText());
            String itemDescription = String.valueOf(mBinding.etDescription.getText());
            String markPrice = String.valueOf(mBinding.etMarkPrice.getText());
            String sellingPrice = String.valueOf(mBinding.etSellingPrice.getText());
            int isRecommended = mBinding.switchRecommended.isChecked() ? 1 : 0;
            int isPopular = mBinding.switchPopular.isChecked() ? 1 : 0;
            int isNew = mBinding.switchNew.isChecked() ? 1 : 0;
            int isVeg = mBinding.switchVeg.isChecked() ? 1 : 0;
        });

    }


    private boolean validateItemName(){
        String name = String.valueOf(mBinding.etItemName.getText()).trim();
        if(name.isEmpty()){
            mBinding.textInputItemNameLayout.setError("Field can't be empty");
            return false;
        }else if(name.length() < 3){
            mBinding.textInputItemNameLayout.setError("Item Name must be of atleast 3 characters");
            return false;
        }
        else {
            mBinding.textInputItemNameLayout.setError(null);
            //inputLayoutEmail.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateDescription(){
        String description = String.valueOf(mBinding.etDescription.getText()).trim();
        if(description.isEmpty()){
            mBinding.textInputDescriptionLayout.setError("Field can't be empty");
            return false;
        }else if(description.length() < 20){
            mBinding.textInputDescriptionLayout.setError("Description must be of atleast 2o characters");
            return false;
        }
        else {
            mBinding.textInputDescriptionLayout.setError(null);
            //inputLayoutEmail.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateMarkPrice(){
        String price = String.valueOf(mBinding.etMarkPrice.getText()).trim();
        if(price.isEmpty()){
            mBinding.textInputMarkPriceLayout.setError("Field can't be empty");
            return false;
        }else {
            mBinding.textInputMarkPriceLayout.setError(null);
            //inputLayoutEmail.setErrorEnabled(false);
            return true;
        }
    }

    private MultipartBody.Part getImage(Uri fileUri){
        RequestToken requestToken = new RequestToken();
        //RequestBody requestBodyUserId = RequestBody.create(MultipartBody.FORM, "1");
        RequestBody requestBodyUserId = RequestBody.create(MediaType.parse("multipart/form-data"), requestToken.getUserId()+"");
        //String name = et_name.getText().toString();
        //RequestBody requestBodyUserName = RequestBody.create(MediaType.parse("multipart/form-data"), name);

        MultipartBody.Part file = null;
        if(fileUri != null){
            File originalFile = new File(fileUri.getPath());
            System.out.println("OriginalFile:  "+originalFile);
            RequestBody filePart = RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    FileUtils.getFile(requireActivity(), fileUri)
            );
            file = MultipartBody.Part.createFormData("photo", "gggggggg.jpeg", filePart );
        }
        return file;


        /*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface api = retrofit.create(ApiInterface.class);
        Call<ApiResponse> call = api.editProfile(requestBodyUserId,requestBodyUserName, file);


        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please wait while we updating your profile");
        progressDialog.show();
        Log.d(TAG, "Inside editProfile() method");
        Log.d(TAG, call.request().url()+"");
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressDialog.dismiss();
                try{
                    ApiResponse apiResponse = response.body();
                    Log.d(TAG, "SUCCESS_RESPONSE: " + apiResponse);
                    //Toast.makeText(EditProfileActivity.this, "SERVER_RESPONSE: "+apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    User user = UserSession.getUserData();
                    user.setName(apiResponse.getName());
                    user.setPhoto(apiResponse.getPhoto());
                    UserSession.setUserData(user);

                    Picasso.get().load(Constants.WEBSITE_URL + apiResponse.getPhoto()).into(profile_image);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG,  "FAIL: ");

            }
        });

         */
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
                mBinding.image.setImageBitmap(mBitmap);
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