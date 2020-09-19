package com.example.mainactivity.views.location;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.LocationDetailsDialogListener;
import com.example.mainactivity.commons.LocationSearchDialogListener;
import com.example.mainactivity.databinding.ActivityAcceptOrderBinding;
import com.example.mainactivity.databinding.ActivityLocationBinding;

public class LocationActivity extends AppCompatActivity implements LocationSearchDialogListener, LocationDetailsDialogListener {
    ActivityLocationBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLocationBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

//        mBinding.layoutSearch.setVisibility(View.GONE);
        mBinding.bgOverlay.setVisibility(View.GONE);

        mBinding.btnChangeAddress.setOnClickListener(view -> {
            LocationSearchDialog dialog = new LocationSearchDialog();
            dialog.show(getSupportFragmentManager(), "LOCATION_SEARCH_DIALOG");
        });

        mBinding.btnConfirm.setOnClickListener(view -> {
            LocationDetailsDialog dialog = new LocationDetailsDialog();
            dialog.show(getSupportFragmentManager(), "LOCATION_DETAILS_DIALOG");
        });



        /*
        mBinding.btnCloseDialog.setOnClickListener(view -> {
            mBinding.bgOverlay.animate().alpha(0f).setDuration(600);
            mBinding.layoutChangeAddress.setVisibility(View.VISIBLE);
            mBinding.layoutSearch.setVisibility(View.GONE);
            mBinding.bottomSheetAddressLayout.animate().setDuration(400).translationY(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mBinding.bgOverlay.setVisibility(View.GONE);
                }
            });

        });

         */
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
    public void onCurrentLocationClick() {

    }

    @Override
    public void onSearchLocation(String searchText) {

    }

    @Override
    public void onAddressSave() {

    }
}