package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.ActivityAcceptOrderBinding;
import com.example.mainactivity.databinding.ActivityLocationBinding;

public class LocationActivity extends AppCompatActivity {
    ActivityLocationBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLocationBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        mBinding.layoutSearch.setVisibility(View.GONE);
        mBinding.bgOverlay.setVisibility(View.GONE);

        mBinding.btnChange.setOnClickListener(view -> {
            mBinding.bgOverlay.animate().alpha(1f).setDuration(400);
            mBinding.bgOverlay.setVisibility(View.VISIBLE);
            mBinding.layoutChangeAddress.setVisibility(View.GONE);
            mBinding.layoutSearch.setVisibility(View.VISIBLE);
            slideUp(mBinding.bottomSheetAddressLayout);

        });

        mBinding.btnCloseDialog.setOnClickListener(view -> {
            mBinding.bgOverlay.animate().alpha(0f).setDuration(400);
            mBinding.bgOverlay.setVisibility(View.GONE);
            mBinding.layoutChangeAddress.setVisibility(View.VISIBLE);
            mBinding.layoutSearch.setVisibility(View.GONE);
            mBinding.bottomSheetAddressLayout.animate().setDuration(400).translationY(0);

        });
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
}