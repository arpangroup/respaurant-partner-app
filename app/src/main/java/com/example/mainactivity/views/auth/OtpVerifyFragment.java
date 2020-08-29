package com.example.mainactivity.views.auth;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mainactivity.R;
import com.example.mainactivity.databinding.FragmentOtpVerifyBinding;
import com.example.mainactivity.viewmodels.AuthenticationViewModel;

public class OtpVerifyFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private static long COUNT_DOWN_TIME = 1 * 60 *1000; // 1 Min;

    private FragmentOtpVerifyBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    NavController navController;

    private long mTimeLeftInMills = COUNT_DOWN_TIME;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOtpVerifyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        //authenticationViewModel.init();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);

        // Injecting the viewmodel to the view for data-binding
        //mBinding.setAuthViewModel(authenticationViewModel);
    }
}